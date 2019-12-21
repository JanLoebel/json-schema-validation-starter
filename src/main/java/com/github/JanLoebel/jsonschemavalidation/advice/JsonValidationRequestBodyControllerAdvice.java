package com.github.JanLoebel.jsonschemavalidation.advice;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.JanLoebel.jsonschemavalidation.JsonSchemaValidation;
import com.github.JanLoebel.jsonschemavalidation.provider.JsonSchemaProvider;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@ControllerAdvice
public class JsonValidationRequestBodyControllerAdvice implements RequestBodyAdvice {

    private ObjectMapper objectMapper;
    private JsonSchemaProvider jsonSchemaProvider;

    public JsonValidationRequestBodyControllerAdvice(ObjectMapper objectMapper, JsonSchemaProvider jsonSchemaProvider) {
        this.objectMapper = objectMapper;
        this.jsonSchemaProvider = jsonSchemaProvider;
    }

    @Override
    public boolean supports(MethodParameter parameter,
                            Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return parameter.getNestedParameterType().isAnnotationPresent(JsonSchemaValidation.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException {
        final String body = toString(inputMessage);

        final String uri = extractSchemaUri(parameter);
        final JsonNode jsonNode = this.objectMapper.readTree(body);
        final Set<ValidationMessage> validationMessages = this.jsonSchemaProvider.loadSchema(uri).validate(jsonNode);
        this.jsonSchemaProvider.handleValidationMessages(validationMessages);

        return buildHttpInputMessage(body, inputMessage.getHeaders());
    }

    private String extractSchemaUri(MethodParameter parameter) {
        final JsonSchemaValidation annotation =
                parameter.getNestedParameterType().getAnnotation(JsonSchemaValidation.class);
        return annotation.value();
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Do not touch
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Do not touch
        return body;
    }

    private String toString(HttpInputMessage inputMessage) throws IOException {
        final InputStream inputStream = inputMessage.getBody();
        return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }

    private InputStream fromString(String body) {
        return new ByteArrayInputStream(body.getBytes());
    }

    private HttpInputMessage buildHttpInputMessage(String body, HttpHeaders httpHeaders) {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return fromString(body);
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpHeaders;
            }
        };
    }
}
