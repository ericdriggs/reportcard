package io.github.ericdriggs.reportcard.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.FailedFileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

@Service
@Slf4j
public class S3Service {

    private Region region;
    private String bucketName;

    private URI endpointOverride;

    public S3Service(@Value("${s3.region}") String region,
                     @Value("${s3.bucket}") String bucketName,
                     @Value("${s3.endpoint:null}") String endpointOverride
    ) {
        this.region = Region.of(region);
        this.bucketName = bucketName;
        this.endpointOverride = (endpointOverride == null) ? null : URI.create(endpointOverride);
    }

    protected S3AsyncClient getS3AsyncClient() {
        S3CrtAsyncClientBuilder builder = S3AsyncClient.crtBuilder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region);

        if (endpointOverride != null) {
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
    public CompletedDirectoryUpload uploadDirectory(MultipartFile[] files, String prefix) {
        final Path tmpPath = Files.createTempDirectory("s3");
        final String tmpPathString = tmpPath.toFile().getAbsolutePath();
        try {
            for (MultipartFile file : files) {
                final Path filepath = Paths.get(tmpPathString, file.getOriginalFilename());
                file.transferTo(filepath);
            }
            return uploadDirectory(tmpPathString, prefix);
        } finally {
            FileUtils.deleteDirectory(new File(tmpPathString));
        }
    }

    public CompletedDirectoryUpload uploadDirectory(String sourceDirectory, String prefix) {

        S3TransferManager s3TransferManager = getTransferManager();

        DirectoryUpload directoryUpload =
                s3TransferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(bucketName)
                        .s3Prefix(prefix)
                        .build());

        CompletedDirectoryUpload completedDirectoryUpload = directoryUpload.completionFuture().join();
        Set<String> failedUploads = new TreeSet<>();
        for (FailedFileUpload failedFileUpload : completedDirectoryUpload.failedTransfers()) {
            failedUploads.add(failedFileUpload.request().source().toString());

        }
        log.error("S3Service --  failed to transfer -- failedUploads: {}", failedUploads);
        if (completedDirectoryUpload.failedTransfers().size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, failedUploads.toString());
        }
        return completedDirectoryUpload;
    }

}
