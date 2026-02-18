package io.github.ericdriggs.reportcard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.client.util.TarGzUtil;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


//https://medium.com/swlh/spring-boot-webclient-cheat-sheet-5be26cfa3e
//https://stackoverflow.com/questions/50223891/how-to-extract-response-header-status-code-from-spring-5-webclient-clientrespo
public class PostWebClient {

    public static final PostWebClient INSTANCE = new PostWebClient();


//TODO: mask authorization
//    static {
//        interceptor.redactHeader("Authorization");
//        interceptor.redactHeader("Proxy-Authorization");
//    }

    public static final WebClient client =
            WebClient
                    .builder()
                    .filters(exchangeFilterFunctions -> LogFilters.prepareFilters())
                    .build();


    protected final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    public MultiValueMap<String, HttpEntity<?>> fromFile(File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        return builder.build();
    }

    protected Mono<String> postTestReport(PostRequest scannerPostRequest) {

        Path junitTarGz = null;
        Path karateTarGz = null;

        try {
            // Create JUnit tar.gz from test report directory
            String testReportPath = scannerPostRequest.getReportMetaData().getTestReportPath();
            String testReportRegex = scannerPostRequest.getReportMetaData().getTestReportRegex();

            try {
                junitTarGz = TarGzUtil.createTarGzFromDirectory(
                        Path.of(testReportPath),
                        testReportRegex
                );
            } catch (IllegalArgumentException e) {
                Map<String, String> validationErrors = new HashMap<>();
                validationErrors.put(ClientArg.TEST_REPORT_PATH.name(), "no files found");
                validationErrors.put(ClientArg.TEST_REPORT_REGEX.name(), "no files found");
                throw new BadRequestException(validationErrors);
            }

            // Create Karate tar.gz if karateJsonFile path is provided
            String karateJsonFile = scannerPostRequest.getReportMetaData().getKarateJsonFile();
            if (karateJsonFile != null && !karateJsonFile.isEmpty()) {
                try {
                    karateTarGz = TarGzUtil.createTarGzFromDirectory(
                            Path.of(karateJsonFile),
                            ".*\\.json$"
                    );
                } catch (IllegalArgumentException e) {
                    // Log warning but don't fail - Karate is optional
                    System.err.println("Warning: Failed to create Karate tar.gz: " + e.getMessage());
                }
            }

            // Build multipart request
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            try {
                multipartBodyBuilder.part("reportMetaData", objectMapper.writeValueAsString(scannerPostRequest), MediaType.APPLICATION_JSON);
                multipartBodyBuilder.part("junit.tar.gz", new FileSystemResource(junitTarGz.toFile()), MediaType.APPLICATION_OCTET_STREAM);

                if (karateTarGz != null) {
                    multipartBodyBuilder.part("karate.tar.gz", new FileSystemResource(karateTarGz.toFile()), MediaType.APPLICATION_OCTET_STREAM);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            WebClient.RequestHeadersSpec<?> requestHeadersSpec = client.post()
                    .uri(scannerPostRequest.getPostUrl())
                    .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()));

            Mono<String> monoResponse = requestHeadersSpec.exchangeToMono(resp -> {
                if (resp.statusCode()
                        .equals(HttpStatus.OK)) {
                    return resp.bodyToMono(String.class);
                } else {
                    throw new ResponseStatusException(HttpStatus.valueOf(resp.statusCode().value()), resp.bodyToMono(String.class).block());
                }
            });
            return monoResponse;

        } finally {
            // Clean up temporary tar.gz files
            if (junitTarGz != null) {
                try {
                    Files.deleteIfExists(junitTarGz);
                } catch (IOException e) {
                    System.err.println("Warning: Failed to delete temporary JUnit tar.gz file: " + junitTarGz);
                }
            }
            if (karateTarGz != null) {
                try {
                    Files.deleteIfExists(karateTarGz);
                } catch (IOException e) {
                    System.err.println("Warning: Failed to delete temporary Karate tar.gz file: " + karateTarGz);
                }
            }
        }
    }
}
