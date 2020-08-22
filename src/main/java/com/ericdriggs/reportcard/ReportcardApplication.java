package com.ericdriggs.reportcard;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SuppressWarnings("SpellCheckingInspection")
@SpringBootApplication(scanBasePackages = {"com.ericdriggs.reportcard"})
@EnableJpaRepositories(basePackages = "com.ericdriggs.reportcard")
public class ReportcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportcardApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
				.addValueReader(new RecordValueReader());
		return mapper;
	}

//	@Bean
//	public ExceptionTranslator exceptionTransformer() {
//		return new ExceptionTranslator();
//	}

//	@Bean
//	public DefaultDSLContext dsl() {
//		return new DefaultDSLContext(configuration());
//	}
}
