package com.abach42.superhero.testconfiguration;

import com.abach42.superhero.testconfiguration.serialization.ApplicationUserDtoSerializer;
import com.abach42.superhero.user.ApplicationUserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class that helps set up a customized Jackson {@link ObjectMapper} for serialization
 * and deserialization. This configuration will only be active under the "test" profile.
 * <br>
 * This class provides a {@link Bean} for the configured {@link ObjectMapper}, `superheroObjectMapper`,
 * which can be injected and used in tests to ensure consistent serialization behavior, especially
 * for sensitive data such as passwords.
 * <p>
 * Usage:
 * <br>
 * {@code @Import({ObjectMapperSerializerHelper.class})}
 * {@code @Autowired private ObjectMapper superheroObjectMapper;}
 */
@Profile("test")
@Configuration
public class ObjectMapperSerializerHelper {

    @Bean
    public ObjectMapper superheroObjectMapper() {
        SimpleModule simpleModule = new SimpleModule()
                .addSerializer(ApplicationUserDto.class, new ApplicationUserDtoSerializer());

        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(simpleModule);
    }
}
