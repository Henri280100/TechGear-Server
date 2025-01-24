package com.v01.techgear_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.v01.techgear_server.repo.jpa")
public class JpaConfig {
}
