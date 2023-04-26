package io.github.ericdriggs.reportcard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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


        File[] files;
        {
            File dir = new File(scannerPostRequest.getReportMetaData().getTestReportPath());
            FileFilter fileFilter = new RegexFileFilter(scannerPostRequest.getReportMetaData().getTestReportRegex());
            files = dir.listFiles(fileFilter);
            if (files == null || files.length == 0) {
                Map<String, String> validationErrors = new HashMap<>();
                validationErrors.put(ClientArg.TEST_REPORT_PATH.name(), "no files found");
                validationErrors.put(ClientArg.TEST_REPORT_REGEX.name(), "no files found");
                throw new BadRequestException(validationErrors);
            }
        }

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        try {
            multipartBodyBuilder.part("reportMetaData", objectMapper.writeValueAsString(scannerPostRequest), MediaType.APPLICATION_JSON);

            for (File file : files) {
                multipartBodyBuilder.part("files", new FileSystemResource(file), MediaType.TEXT_XML);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        WebClient.RequestHeadersSpec<?> requestHeadersSpec = client.post()
                .uri(scannerPostRequest.getPostUrl())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()));

//        WebClient.ResponseSpec responseSpec = requestHeadersSpec.retrieve();
//        responseSpec.to


        Mono<String> monoResponse = requestHeadersSpec.exchangeToMono(resp -> {
            if (resp.statusCode()
                    .equals(HttpStatus.OK)) {
                return resp.bodyToMono(String.class);
            } else {
                throw new ResponseStatusException(HttpStatus.valueOf(resp.statusCode().value()), resp.bodyToMono(String.class).block());
            }
        });
        return monoResponse;
    }
}
