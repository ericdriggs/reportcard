package io.github.ericdriggs.reportcard.storage;

import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
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
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private final int uploadRetryCount = 3;

    @Autowired
    public S3Service(Environment environment) {
        this.environment = environment;
        this.region = Region.of(getProperty("S3_REGION", "us-east-1"));
        this.bucketName = getProperty("S3_BUCKET", "testbucket");
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

    protected S3Client getS3Client() {
        S3ClientBuilder builder = S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create())
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

    public ListObjectsV2Response listObjects(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request
                .builder()
                .bucket(bucketName)
                .prefix(prefix)
                .delimiter("/")
                .maxKeys(1000) //more than this would require pagination
                .build();
        return getS3Client().listObjectsV2(request);
    }

    public ResponseBytes<GetObjectResponse> getObjectBytes(String key) {
        GetObjectRequest getObjectRequest =  GetObjectRequest.builder().bucket(bucketName).key(key).build();
        return getS3Client().getObjectAsBytes(getObjectRequest);
    }

    public DirectoryUploadResponse uploadTarGz(String prefix, boolean shouldExpand, MultipartFile tarGz) {
        if (shouldExpand) {
            return uploadTarGZExpanded(prefix, tarGz);
        } else {
            return uploadDirectory(prefix, tarGz);
        }
    }

    @SneakyThrows(IOException.class)
    public DirectoryUploadResponse uploadTarGZExpanded(String prefix, MultipartFile tarGz) {
        Path tempDir = null;
        try {

            for (int i=0; i<uploadRetryCount; i++) {
                try {
                    tempDir = Files.createTempDirectory("s3.");
                    InputStream inputStream = tarGz.getInputStream();
                    TarExtractorCommonsCompress tarExtractor = new TarExtractorCommonsCompress(inputStream, true, tempDir);
                    tarExtractor.untar();
                } catch(Exception ex) {
                    log.warn("exception extracting tar file, attempt: {}", i, ex);
                    if (tempDir != null) {
                        FileUtils.deleteDirectory(tempDir.toFile());
                    }
                    tempDir = null;
                }
            }

            if (tempDir == null) {
                throw new NullPointerException("uploadTarGZExpanded - failed to extract file for prefix: " + prefix);
            }

            return uploadDirectory(prefix, tempDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("uploadTarGZ failed for prefix: {}", prefix, ex);
            throw ex;
        }
        finally {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }

    @SneakyThrows(IOException.class)
    public DirectoryUploadResponse uploadDirectory(String prefix, MultipartFile... files) {
        final Path tempDir = Files.createTempDirectory("s3.");
        Exception ex = null;
        for (int i = 1; i<= uploadRetryCount; i++) {
            try {
                for (MultipartFile file : files) {
                    final String fileName = file.getName();
                    final Path filepath = Paths.get(tempDir.toString(), fileName);
                    file.transferTo(filepath);
                }
                return uploadDirectory(prefix, tempDir);
            } catch (Exception e) {
                log.warn("uploadTarGZExpanded upload attempt failed. prefix: {}, attempt: {}", prefix, i, e);
                ex = e;
            }finally {
                FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
        if (ex != null) {
            log.error("uploadTarGZExpanded upload failed. prefix: {}", prefix, ex);
            ex.printStackTrace();
        }
        throw new IllegalStateException("upload failed", ex);
    }

    private DirectoryUploadResponse uploadDirectory(String prefix, Path path) {

        S3TransferManager s3TransferManager = getTransferManager();

        //UploadFileRequest.builder();
        DirectoryUpload directoryUpload =
                s3TransferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(path)
                        .bucket(bucketName)
                        .s3Prefix(prefix)

                        .uploadFileRequestTransformer(ufr -> ufr.putObjectRequest(
                                PutObjectRequest.builder()
                                        .checksumAlgorithm(CHECKSUM_ALGORITHM)
                                        .key(ufr.build().putObjectRequest().key())
                                        .bucket(bucketName)
                                        //TO_MAYBE: if content type is not being set correctly, should be able to deduce from extension of key using function here
                                        //.contentType(ufr.build().putObjectRequest().contentType(getContentTypeFromExtension(key)))
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
        final String envVal = environment.getProperty(propertyName);
        final String systemVal = System.getProperty(propertyName);
        final String val = systemVal != null ? systemVal : envVal;
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
