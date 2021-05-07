package com.ericdriggs.reportcard.client;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}


	@Override
	public void run(ApplicationArguments args) {
		//FIXME: either don't log args or mask password
		System.out.println("# NonOptionArgs: " + args.getNonOptionArgs().size());

		System.out.println("NonOptionArgs:");
		args.getNonOptionArgs().forEach(System.out::println);

		System.out.println("# OptionArgs: " + args.getOptionNames().size());
		System.out.println("OptionArgs:");

		args.getOptionNames().forEach(optionName -> System.out.println(optionName + "=" + args.getOptionValues(optionName)));

		PostRequest scannerPostRequest = ClientProperties.getReportPostPayload(args);



	}
}