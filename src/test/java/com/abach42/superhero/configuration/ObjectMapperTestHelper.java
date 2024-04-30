package com.abach42.superhero.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/*
* ObjectMapper shall be used as helper to avoid *manual writing of json* String code 
* in files or variables.
* So we must strip away some configurations, disable(MapperFeature) is deprecated.
*/
@Profile("test")
@Service
public class ObjectMapperTestHelper {
    @Autowired
    ObjectMapper objectMapper;

    public ObjectMapper get() {
        return objectMapper.setAnnotationIntrospector(new IgnoreJacksonWriteOnlyAccess());
    }

    private class IgnoreJacksonWriteOnlyAccess extends JacksonAnnotationIntrospector {
        @Override
        public JsonProperty.Access findPropertyAccess(Annotated m) {
            return JsonProperty.Access.AUTO;
        }
    }
}
