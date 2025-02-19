package com.example.ProviderTrustly.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Database configuration for Trustly payment service.
 * Configures connection pool and JDBC templates using HikariCP.
 */
@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class DatabaseConfig {

    private final Environment environment;

    @Autowired
    public DatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates and configures the main database connection pool for Trustly service.
     */
    @Bean
    @Qualifier("TrustlyServiceJdbcTemplate")
    public DataSource trustlyDataSource() {
        var hikariDS = new HikariDataSource();

        // Database connection settings
        hikariDS.setDriverClassName(getProperty("spring.datasource.driverClassName"));
        hikariDS.setJdbcUrl(getProperty("spring.datasource.url"));
        hikariDS.setUsername(getProperty("spring.datasource.username"));
        hikariDS.setPassword(getProperty("spring.datasource.password"));

        // Pool configuration
        hikariDS.setMaximumPoolSize(getIntProperty("spring.datasource.maxActive"));
        hikariDS.setMinimumIdle(getIntProperty("spring.datasource.minIdle"));

        // Connection validation
        hikariDS.setConnectionInitSql(getProperty("spring.datasource.validationQuery"));

        return hikariDS;
    }

    /**
     * Creates a named parameter JDBC template for SQL operations.
     */
    @Bean
    @Qualifier("namedJdbcTemplate")
    public NamedParameterJdbcTemplate jdbcTemplate() {
        return new NamedParameterJdbcTemplate(trustlyDataSource());
    }

    private String getProperty(String key) {
        return environment.getProperty(key);
    }

    private int getIntProperty(String key) {
        return Integer.parseInt(environment.getProperty(key));
    }
}