//package com.ericdriggs.reportcard.config;
//
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//
////@PropertySource({"application.properties"})
//public class Config {
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
//
//}
