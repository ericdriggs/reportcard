package io.github.ericdriggs.reportcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;

@Profile("test")
public class LocalStackConfig {
    // Any mock-AWS services required for any tests need to be added to this
    // manifest so that LocalStack knows what to spin up.
    private static final LocalStackContainer.Service[] REQUIRED_SERVICES = {
            LocalStackContainer.Service.S3
    };

    private LocalStackContainer localStackContainer;

    @Bean
    public LocalStackContainer localStackContainer() {
        localStackContainer = new LocalStackContainer()
                .withServices(REQUIRED_SERVICES)

                //.withReuse(true)
                //.withLabel("reuse.uuid", "4a648123-30ae-43e4-b6fd-939dfd2f4554 ")
                .withEnv("HOSTNAME_EXTERNAL", "localhost");
        localStackContainer.start();

        return this.localStackContainer;
    }
}