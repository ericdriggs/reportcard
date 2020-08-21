package com.ericdriggs.reportcard.config;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_17;

@Component
@Profile("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class MyEmbeddedMysql {
    @Value("${db.driverClassName}")
    String driverClassName;
    @Value("${db.url}")
    String url;
    @Value("${db.username}")
    String username;
    @Value("${db.password}")
    String password;
    @Value("${db.schema}")
    String schema;
    @Value("${db.ddlsql}")
    String ddlsql;


//    private final static String schema = "reportcard";
//    private final static String username = "test";
//    private final static String password = "test";

    private DriverManagerDataSource dataSource;
    EmbeddedMysql mysqld;


    public MyEmbeddedMysql() {

        System.out.println("##### LOADING TestEmbeddedMysql #######");

//        Integer port = FreePortFinder.findFreeLocalPort();
        Integer port = 13306;
        MysqldConfig config = aMysqldConfig(v8_0_17)
                .withCharset(UTF8)
                .withUser(username, password)
                .withTimeZone("UTC")
                .withTimeout(2, TimeUnit.MINUTES)
                .withServerVariable("max_connect_errors", 666)
                .withPort(port)
                .build();

        mysqld = anEmbeddedMysql(config)
                .addSchema(schema,
                        classPathScript(ddlsql)
                ).start();

        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);

        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    public DriverManagerDataSource getDataSource() {
        return dataSource;
    }

    public void reloadSchema() {
        mysqld.reloadSchema(schema,
                classPathScript("reportcard-mysql.sql")
        );
    }

    public void close() {
        mysqld.stop();
    }

}
