package com.github.JanLoebel.jsonschemavalidation.provider;

import com.github.JanLoebel.jsonschemavalidation.JsonSchemaValidationException;
import com.networknt.schema.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;


@ConditionalOnProperty(prefix = "json.schema.validation", name = "schemaProvider", havingValue = "default")
@Component
public class DefaultJsonSchemaProvider implements JsonSchemaProvider {

    @Override
    public JsonSchema loadSchema(String url) {
        final InputStream inputStream = createInputStream(url);
        final JsonSchemaFactory jsonSchemaFactory = getJsonSchemaFactory();
        return jsonSchemaFactory.getSchema(inputStream, getSchemaValidatorsConfig());
    }

    @Override
    public void handleValidationMessages(Collection<ValidationMessage> validationMessages) {
        if (!validationMessages.isEmpty()) {
            throw new JsonSchemaValidationException(validationMessages);
        }
    }

    protected SchemaValidatorsConfig getSchemaValidatorsConfig() {
        final SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFailFast(false);
        config.setTypeLoose(true);
        config.setHandleNullableField(true);
        return config;
    }

    protected JsonSchemaFactory getJsonSchemaFactory() {
        return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }

    private InputStream createInputStream(String url) {
        try {
            if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
                // TODO validate this
                return new FileInputStream(new File(url));
            }
            if (url.toLowerCase().startsWith("classpath:")) {
                return new ClassPathResource(url.substring("classpath:".length())).getInputStream();
            }

            return new FileInputStream(ResourceUtils.getFile(url));
        } catch (IOException e) {
            throw new IllegalStateException("Could not load url: " + url, e);
        }
    }
}
