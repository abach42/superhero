package com.abach42.superhero.ai.contextual;

import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeamRagResponseConverter {

    @Bean
    public BeanOutputConverter<TeamRagResponseDto> outputConverter() {
        return new BeanOutputConverter<>(TeamRagResponseDto.class);
    }
}
