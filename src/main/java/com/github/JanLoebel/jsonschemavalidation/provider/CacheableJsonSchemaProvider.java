package com.github.JanLoebel.jsonschemavalidation.provider;

import com.networknt.schema.JsonSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheableJsonSchemaProvider extends DefaultJsonSchemaProvider {

    private final Map<String, JsonSchema> cache = new ConcurrentHashMap<>();
    private final String basePath;

    public CacheableJsonSchemaProvider(String basePath) {
        this.basePath = basePath;
    }

    public CacheableJsonSchemaProvider() {
        this("");
    }

    @Override
    public JsonSchema loadSchema(String url) {
        final String fullPath = basePath + url;
        if (cache.containsKey(fullPath)) {
            return cache.get(fullPath);
        }

        return putToCache(fullPath, super.loadSchema(fullPath));
    }

    private JsonSchema putToCache(String fullPath, JsonSchema jsonSchema) {
        cache.put(fullPath, jsonSchema);
        return jsonSchema;
    }

}
