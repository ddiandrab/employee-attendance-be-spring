package com.employeeapp.employeeservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // This configuration class is used to customize the Jackson ObjectMapper used
    // for JSON serialization and deserialization in the application.
    @Bean
    public ObjectMapper objectMapper() {
        // The findAndRegisterModules() method automatically discovers and registers all
        // available Jackson modules on the classpath,
        // such as the JavaTimeModule for handling Java 8 date and time types.
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}