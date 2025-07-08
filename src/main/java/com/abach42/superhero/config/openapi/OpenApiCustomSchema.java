package com.abach42.superhero.config.openapi;

import com.abach42.superhero.skillprofile.SkillProfileDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiCustomSchema {

    // simply following https://springdoc.org/faq.html#_how_can_i_define_different_schemas_for_the_same_class
    // workaround waiting for io.swagger.v3.oas using groups
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components()
                .addSchemas(SkillProfileDto.SkillProfileSwaggerPut.SKILL_PROFILE_PUT,
                        getSchemaWithDifferentDescription()));
    }

    private Schema<?> getSchemaWithDifferentDescription() {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(
                        new AnnotatedType(
                                SkillProfileDto.SkillProfileSwaggerPut.class).resolveAsRef(false));
        return resolvedSchema.schema.description(
                SkillProfileDto.SkillProfileSwaggerPut.SKILL_PROFILE_PUT);
    }
}
