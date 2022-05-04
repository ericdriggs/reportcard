package io.github.ericdriggs.reportcard.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ClientApplication implements ApplicationRunner {
	private static Logger log = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}


	@Override
	public void run(ApplicationArguments args) {
		//TODO: either don't log args or mask password(s)
		log.info("# NonOptionArgs: " + args.getNonOptionArgs().size());

		log.info("NonOptionArgs:");
		args.getNonOptionArgs().forEach(System.out::println);

		log.info("# OptionArgs: " + args.getOptionNames().size());
		log.info("OptionArgs:");

		args.getOptionNames().forEach(optionName -> System.out.println(optionName + "=" + args.getOptionValues(optionName)));

		PostRequest postRequest = ClientProperties.getReportPostPayload(args);
		Mono<String> postResultMono = PostWebClient.INSTANCE.postTestReport(postRequest);
		String postResult = postResultMono.block();
		log.info("postResult:\n " + postResult);
	}

}