package io.github.ericdriggs.reportcard;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SuppressWarnings("SpellCheckingInspection")
@SpringBootApplication(scanBasePackages = {"io.github.ericdriggs.reportcard"}, exclude = {R2dbcAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "io.github.ericdriggs.reportcard")
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
