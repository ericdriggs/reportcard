package io.github.ericdriggs.reportcard.gen.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan({ "io.github.ericdriggs.reportcard" })
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
//TODO: can this class be removed? Don't know if it's still doing anything
public class PersistenceContext {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setUser(environment.getRequiredProperty("db.username"));
        dataSource.setPassword(environment.getRequiredProperty("db.password"));

        String jdbcUrl = "jdbc:mysql://localhost:{port}/reportcard?serverTimezone=UTC".replace("{port}", environment.getRequiredProperty("db.port"));
        dataSource.setUrl(jdbcUrl);
        dataSource.setPort(Integer.parseInt(environment.getRequiredProperty("db.port")));

        return dataSource;
    }

    @Bean
    public TransactionAwareDataSourceProxy transactionAwareDataSource() {
        return new TransactionAwareDataSourceProxy(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(transactionAwareDataSource());
    }

    @Bean
    public ExceptionTranslator exceptionTransformer() {
        return new ExceptionTranslator();
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    @Bean
    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

        String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
        SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
        jooqConfiguration.set(dialect);

        return jooqConfiguration;
    }
}