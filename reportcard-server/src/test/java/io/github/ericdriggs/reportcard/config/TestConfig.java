//package io.github.ericdriggs.reportcard.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@TestConfiguration
//public class TestConfig extends Config {
//
//    /**
//     * database connectivity properties.
//     *
//     * @return data source
//     */
//    @Profile("test")
//    @Bean(name = "dataSource")
//    public DataSource dataSourceTest() {
//        return myEmbeddedMysql.getDataSource();
//    }
//
//    //TODO: get from properties
//    private final static String schema = "reportcard";
//    private final static String username = "test";
//    private final static String password = "test";
//
//    @Autowired
//    private MyEmbeddedMysql myEmbeddedMysql;
//    private DriverManagerDataSource dataSource;
//}
