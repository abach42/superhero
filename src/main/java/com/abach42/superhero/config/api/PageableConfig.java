package com.abach42.superhero.config.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Respecting configuration value of paging.default.pageSize in custom pageable object.
 */
@Configuration
public class PageableConfig {

    @Bean
    static Integer defaultPageSize(@Value("${paging.default.pageSize:10}") Integer pageSize) {
        return pageSize;
    }
}