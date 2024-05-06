package com.abach42.superhero.config.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.abach42.superhero.dto.SkillProfileDto;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

@Configuration
public class OpenApiCustomSchema {

    // simply following https://springdoc.org/faq.html#_how_can_i_define_different_schemas_for_the_same_class
    // workaround waiting for io.swagger.v3.oas using groups
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components()
                .addSchemas(SkillProfileDto.SkillProfileSwaggerPut.SKILLPROFILE,
                        getSchemaWithDifferentDescription(SkillProfileDto.SkillProfileSwaggerPut.class, SkillProfileDto.SkillProfileSwaggerPut.SKILLPROFILE)));
    }

    private Schema<?> getSchemaWithDifferentDescription(Class<?>  className, String description) {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(
                        new AnnotatedType(className).resolveAsRef(false));
        return resolvedSchema.schema.description(description);
    }
}
