//TODO: delete?
// package com.ericdriggs.reportcard.client;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.apache.commons.io.filefilter.RegexFileFilter;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
//
//
////TODO: try with java net client using contnet type for each part //https://stackoverflow.com/questions/46392160/java-9-httpclient-send-a-multipart-form-data-request
//public class PostHttpClient {
//
//    public static final PostHttpClient INSTANCE = new PostHttpClient();
//
//
////TODO: mask authorization
////    static {
////        interceptor.redactHeader("Authorization");
////        interceptor.redactHeader("Proxy-Authorization");
////    }
//
//    public static final HttpClient client = HttpClient.newBuilder()
//            .connectTimeout(Duration.ofSeconds(30))
//            .version(HttpClient.Version.HTTP_1_1)
//            .build();
//
//
//    protected final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
//
//    protected HttpResponse postTestReport(PostRequest scannerPostRequest) {
//
//        try {
//            HttpRequest request;
//            {
//
//                File[] files;
//                {
//                    File dir = new File(scannerPostRequest.getTestReportPath());
//                    FileFilter fileFilter = new RegexFileFilter(scannerPostRequest.getTestReportRegex());
//                    files = dir.listFiles(fileFilter);
//                    if (files == null || files.length == 0) {
//                        Map<String, String> validationErrors = new HashMap<>();
//                        validationErrors.put(ClientArg.TEST_REPORT_PATH.name(), "no files found");
//                        validationErrors.put(ClientArg.TEST_REPORT_REGEX.name(), "no files found");
//                        throw new BadRequestException(validationErrors);
//                    }
//                }
//
//                for (File file : files) {
//                    publisher.addPart("files",  file.getAbsolutePath(), "text/xml");
//                }
//                request = HttpRequest.newBuilder()
//                        .uri(URI.create(scannerPostRequest.getPostUrl()))
//                        .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
//                        .timeout(Duration.ofMinutes(1))
//                        .POST(publisher.build())
//                        .build();
//
//            }
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() != 200) {
//                String responseString = response.body();
//                throw new ResponseStatusException(HttpStatus.valueOf(response.statusCode()), responseString);
//            }
//            return response;
//        } catch (IOException | InterruptedException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//}
