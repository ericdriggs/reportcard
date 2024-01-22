package io.github.ericdriggs.reportcard.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class S3Service {

    private final static ChecksumAlgorithm CHECKSUM_ALGORITHM = ChecksumAlgorithm.SHA1;

    private final Environment environment;

    private final Region region;
    private final String bucketName;

    private final URI endpointOverride;

    @Autowired
    public S3Service(Environment environment) {
        this.environment = environment;
        this.region = Region.of(getProperty("s3.region", "us-east-1"));
        this.bucketName = getProperty("s3.bucket", "testbucket");
        this.endpointOverride = getEndpointOverride();
    }

    protected S3AsyncClient getS3AsyncClient() {
        S3CrtAsyncClientBuilder builder = S3AsyncClient.crtBuilder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region);

        if (endpointOverride != null) {
            builder.forcePathStyle(true);
            builder.endpointOverride(endpointOverride);
        }
        return builder.build();
    }

    protected S3TransferManager getTransferManager() {
        return S3TransferManager.builder()
                .s3Client(getS3AsyncClient())
                .uploadDirectoryFollowSymbolicLinks(true)
                .build();
    }

    @SneakyThrows(IOException.class)
    public DirectoryUploadResponse uploadDirectory(MultipartFile[] files, String prefix) {
        final Path tmpPath = Files.createTempDirectory("s3.");
        final String tmpPathString = tmpPath.toFile().getAbsolutePath();
        try {
            for (MultipartFile file : files) {
                final String fileName = file.getName();
                final Path filepath = Paths.get(tmpPathString, fileName);
                file.transferTo(filepath);
            }
            return uploadDirectory(tmpPathString , prefix);
        } finally {
            FileUtils.deleteDirectory(new File(tmpPathString));
        }
    }

    public DirectoryUploadResponse uploadDirectory(String sourceDirectory, String prefix) {

        S3TransferManager s3TransferManager = getTransferManager();

        UploadFileRequest.builder();
        DirectoryUpload directoryUpload =
                s3TransferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(bucketName)
                        .s3Prefix(prefix)
                        .uploadFileRequestTransformer(ufr -> ufr.putObjectRequest(
                                PutObjectRequest.builder()
                                        .checksumAlgorithm(CHECKSUM_ALGORITHM)
                                        .key(ufr.build().putObjectRequest().key())
                                        .bucket(bucketName)
                                        .build())
                        )
                        .build());

        CompletedDirectoryUpload completedDirectoryUpload = directoryUpload.completionFuture().join();

        List<FailedFileUploadResponse> failedUploadResponses = new ArrayList<>();
        for (FailedFileUpload failedFileUpload : completedDirectoryUpload.failedTransfers()) {
            failedUploadResponses.add(FailedFileUploadResponse.builder().request(failedFileUpload.request().toString()).exception(failedFileUpload.exception()).build());
        }
        DirectoryUploadResponse directoryUploadResponse = DirectoryUploadResponse.builder().failedFileUploadResponses(failedUploadResponses).build();

        if (completedDirectoryUpload.failedTransfers().size() > 0) {
            log.error("S3Service --  failed to transfer -- directoryUploadResponse: {}", directoryUploadResponse.toJson());
            throw new ResponseStatusException(HttpStatus.CONFLICT, directoryUploadResponse.toString());
        }

        return directoryUploadResponse;
    }

    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    public ListObjectsV2Response listObjectsForBucket() {
        S3AsyncClient client =  getS3AsyncClient();
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        CompletableFuture<ListObjectsV2Response> futureResponse = client.listObjectsV2(listObjectsV2Request);
        return futureResponse.get();
    }

    public CompletedFileUpload uploadFile(File file, String key) {

        S3TransferManager s3TransferManager = getTransferManager();

        FileUpload fileUpload =
                s3TransferManager.uploadFile(UploadFileRequest.builder()
                        .source(file)
                        .putObjectRequest(
                                PutObjectRequest.builder()
                                        .key(key)
                                        .bucket(bucketName)
                                        .checksumAlgorithm(CHECKSUM_ALGORITHM)
                                        .build()
                        )
                        .build());

        CompletedFileUpload completedFileUpload = fileUpload.completionFuture().join();
        PutObjectResponse putObjectResponse = completedFileUpload.response();
        if (!putObjectResponse.sdkHttpResponse().isSuccessful()) {
            throw new ResponseStatusException(HttpStatus.valueOf(putObjectResponse.sdkHttpResponse().statusCode()), putObjectResponse.sdkHttpResponse().statusText().orElse("unknown"));
        }
        return completedFileUpload;
    }

    private String getProperty(String propertyName, String defaultValue) {
        final String val = environment.getProperty(propertyName);
        return val == null ? defaultValue : val;
    }

    private URI getEndpointOverride() {
        String endpointOverride = getProperty("s3.endpoint", null);
        if (endpointOverride == null) {
            return null;
        }
        log.info("s3 endpointOverride:" + endpointOverride);
        return URI.create(endpointOverride);
    }
}
