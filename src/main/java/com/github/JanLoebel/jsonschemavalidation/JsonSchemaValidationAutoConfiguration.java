package com.github.JanLoebel.jsonschemavalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.JanLoebel.jsonschemavalidation.advice.JsonValidationRequestBodyControllerAdvice;
import com.github.JanLoebel.jsonschemavalidation.provider.JsonSchemaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSchemaValidationAutoConfiguration {

    @Autowired
    private JsonSchemaProvider jsonSchemaProvider;

    @Bean
    @ConditionalOnMissingBean
    public JsonValidationRequestBodyControllerAdvice jsonValidationRequestBodyControllerAdvice(ObjectMapper objectMapper) {
        return new JsonValidationRequestBodyControllerAdvice(objectMapper, jsonSchemaProvider);
    }

}
