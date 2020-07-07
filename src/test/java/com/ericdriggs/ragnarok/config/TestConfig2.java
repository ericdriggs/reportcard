//package com.ericdriggs.ragnarok.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@TestConfiguration
//public class TestConfig2 {
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
////    private final static String schema = "ragnarok";
////    private final static String username = "root";
////    private final static String password = "";
//
//    @Autowired
//    private MyEmbeddedMysql myEmbeddedMysql;
//    private DriverManagerDataSource dataSource;
//
//
//    @Value("${spring.datasource.driverClassName}")
//    String driverClassName;
//    @Value("${spring.datasource.url}")
//    String url;
//    @Value("${spring.datasource.username}")
//    String username;
//    @Value("${spring.datasource.password}")
//    String password;
//
//    /**
//     * database connectivity properties.
//     * @return data source
//     */
//    @Bean(name = "dataSource")
//    public DataSource getDataSource() {
//        DataSource dataSource = DataSourceBuilder
//                .create()
//                .username(username)
//                .password(password)
//                .url(url)
//                .driverClassName(driverClassName)
//                .build();
//        return dataSource;
//    }
//}
