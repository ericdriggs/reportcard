package io.github.ericdriggs.reportcard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static org.testcontainers.containers.MySQLContainer.MYSQL_PORT;

@Component
@Profile("test")
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class MyEmbeddedMysql {

    final String driverClassName;
    final String url;
    final String username;
    final String password;
    final String schema;
    final String ddlsql;
    final String dmlsql;

    final DriverManagerDataSource dataSource;

    //@Container
    MySQLContainer mySQLContainer;

    @Autowired
    private Environment environment;


    public MyEmbeddedMysql(@Value("${db.driverClassName}") String driverClassName,
                           @Value("${db.username}") String username,
                           @Value("${db.password}") String password,
                           @Value("${db.schema}") String schema,
                           @Value("${db.ddlsql}") String ddlsql,
                           @Value("${db.dmlsql}") String dmlsql
    ) {
        mySQLContainer = new MySQLContainer<>("mysql:8.0.33")
                .withDatabaseName(schema)
                .withUsername(username)
                .withPassword(password)
                .withCopyFileToContainer(MountableFile.forClasspathResource(ddlsql), "/docker-entrypoint-initdb.d/0_schema.sql")
                .withCopyFileToContainer(MountableFile.forClasspathResource(dmlsql), "/docker-entrypoint-initdb.d/1_config.sql")
                .withCopyFileToContainer(MountableFile.forClasspathResource("db/test/test-data.dml.sql"), "/docker-entrypoint-initdb.d/2_data.sql")

        ;
        mySQLContainer.start();
        this.driverClassName = driverClassName;
        this.url = mySQLContainer.getJdbcUrl();
        this.password = password;
        this.username = username;
        this.schema = schema;
        this.ddlsql = ddlsql;
        this.dmlsql = dmlsql;

        System.out.println("##### LOADING TestEmbeddedMysql #######");

        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(mySQLContainer.getJdbcUrl());
        dataSource.setUsername(username);
        dataSource.setPassword(password);



        System.setProperty("db.connection.string", mySQLContainer.getJdbcUrl());
        System.setProperty("db.port", Integer.toString(mySQLContainer.getMappedPort(MYSQL_PORT)));
    }

    public DriverManagerDataSource getDataSource() {
        return dataSource;
    }

    public void reloadSchema() {
        String tag = mySQLContainer.getContainerId();
        mySQLContainer.getDockerClient().commitCmd(mySQLContainer.getContainerId())
                .withRepository("tempImg")
                .withTag(tag).exec();
        mySQLContainer.stop();
        mySQLContainer.setDockerImageName("tempImg:" + tag);
        mySQLContainer.start();
        dataSource.setUrl(mySQLContainer.getJdbcUrl());
    }

    public void close() {
        mySQLContainer.stop();
    }

}
