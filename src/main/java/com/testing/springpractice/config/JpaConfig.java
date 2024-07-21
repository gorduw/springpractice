package com.testing.springpractice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Autowired
    private AuditorAwareImpl auditorAwareImpl;

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return auditorAwareImpl;
    }
}
