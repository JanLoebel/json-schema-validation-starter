package com.github.JanLoebel.jsonschemavalidation.provider;

import com.networknt.schema.*;
import com.github.JanLoebel.jsonschemavalidation.JsonSchemaValidationException;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;

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
            return new FileInputStream(ResourceUtils.getFile(url));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not load url: " + url, e);
        }
    }
}
