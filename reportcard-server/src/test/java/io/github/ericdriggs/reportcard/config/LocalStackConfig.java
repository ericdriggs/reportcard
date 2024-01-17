package io.github.ericdriggs.reportcard.config;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackConfig {

    private static final String BUCKET_NAME = "testbucket";

    static LocalStackContainer localStackContainer = newLocalStackContainer();


    @Bean
    public LocalStackContainer localStackContainer() {
        return localStackContainer;
    }


    @SneakyThrows({InterruptedException.class, IOException.class})
    static LocalStackContainer newLocalStackContainer() {
        LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0.2"));
        localStack.start();
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
        System.setProperty("s3.endpoint", localStack.getEndpointOverride(S3).toString());
      return localStack;
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.bucket", () -> BUCKET_NAME);
        registry.add("s3.endpoint", () -> localStackContainer.getEndpointOverride(S3).toString());
    }
}