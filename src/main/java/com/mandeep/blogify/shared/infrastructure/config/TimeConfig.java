package com.mandeep.blogify.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {
    @Bean
    public Clock clock() {
        // The one true clock for the production system
        return Clock.systemUTC();
    }
}