package com.abach42.superhero.ai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {
    @Bean
    @Qualifier(ProfileContentStrategy.PROFILE_CONTENT)
    public VectorStore profileStore(JdbcTemplate jdbcTemplate, EmbeddingModel openAiModel) {
        return PgVectorStore.builder(jdbcTemplate, openAiModel)
                .vectorTableName("vector_profiles")
                .build();
    }

    @Bean
    @Qualifier(AllContentStrategy.ALL_CONTENT)
    public VectorStore allStore(JdbcTemplate jdbcTemplate, EmbeddingModel ollamaModel) {
        return PgVectorStore.builder(jdbcTemplate, ollamaModel)
                .vectorTableName("vector_all")
                .build();
    }
}
