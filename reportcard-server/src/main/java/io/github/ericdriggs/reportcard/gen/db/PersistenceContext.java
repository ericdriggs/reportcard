package io.github.ericdriggs.reportcard.gen.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderTable;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${db.connection.string}")
    private String dbConnectionStringValue;

    @Value("${db.port}")
    private String dbPortValue;

    @Value("${db.host}")
    private String dbHostValue;

    @Value("${db.name}")
    private String dbNameValue;

    @Value("${db.username}")
    private String dbUserNameValue;

    @Value("${db.password}")
    private String dbPasswordValue;

    @Bean
    public DataSource dataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();

        final String dbUserName = getProperty("db.username", dbUserNameValue);
        final String dbPassword = getProperty("db.password", dbPasswordValue);
        final String dbPort = getProperty("db.port", dbPortValue);
        final String jdbcUrl = getJdbcUrl(dbPort);


        dataSource.setUser(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPort(Integer.parseInt(dbPort));

        return dataSource;
    }

    private String getJdbcUrl(String dbPort) {

        final String dbHost = getProperty("db.host", dbHostValue);
        final String dbName = getProperty("db.name", dbNameValue);

        return dbConnectionStringValue
                .replace("{dbHost}", getProperty("db.host", dbHost))
                .replace("{dbPort}", getProperty("db.port", dbPort))
                .replace("{dbName}", getProperty("db.name", dbName));
    }

    //allow overriding using runtime environment for localstack testing
    private String getProperty(String propertyName, String defaultValue) {
        return environment.getProperty(propertyName) == null? environment.getProperty(propertyName) : defaultValue;
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

//    @Bean
//    public ExceptionTranslator exceptionTransformer() {
//        return new ExceptionTranslator();
//    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    @Bean
    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        //
        jooqConfiguration.set(new Settings().withRenderTable(RenderTable.ALWAYS));
        jooqConfiguration.set(connectionProvider());
//        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

        String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
        SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
        jooqConfiguration.set(dialect);

        return jooqConfiguration;
    }
}