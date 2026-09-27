package com.employeeapp.employeeservice.audit.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AuditDatabaseConfig {

    @Value("${spring.datasource.url}")
    private String primaryUrl;

    @Value("${spring.datasource.username}")
    private String primaryUsername;

    @Value("${spring.datasource.password}")
    private String primaryPassword;

    @Value("${app.audit.datasource.url}")
    private String url;

    @Value("${app.audit.datasource.username}")
    private String username;

    @Value("${app.audit.datasource.password}")
    private String password;

    @Bean
    @Primary
    DataSource dataSource() {
        return new DriverManagerDataSource(primaryUrl, primaryUsername, primaryPassword);
    }

    @Bean("auditDataSource")
    DataSource auditDataSource() {
        return new DriverManagerDataSource(url, username, password);
    }

    @Bean
    JdbcTemplate auditJdbcTemplate(@Qualifier("auditDataSource") DataSource auditDataSource) {
        return new JdbcTemplate(auditDataSource);
    }
}
