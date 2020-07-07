package com.ericdriggs.ragnarok;

import com.ericdriggs.ragnarok.config.ExceptionTranslator;
import org.jooq.impl.DefaultDSLContext;
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
@SpringBootApplication(scanBasePackages = {"com.ericdriggs.ragnarok"})
@EnableJpaRepositories(basePackages = "com.ericdriggs.ragnarok")
public class RagnarokApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagnarokApplication.class, args);
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
