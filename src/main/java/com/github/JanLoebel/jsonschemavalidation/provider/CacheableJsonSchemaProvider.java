package com.github.JanLoebel.jsonschemavalidation.provider;

import com.networknt.schema.JsonSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheableJsonSchemaProvider extends DefaultJsonSchemaProvider {

    private final Map<String, JsonSchema> cache = new ConcurrentHashMap<>();

    @Override
    public JsonSchema loadSchema(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        return putToCache(url, super.loadSchema(url));
    }

    private JsonSchema putToCache(String url, JsonSchema jsonSchema) {
        cache.put(url, jsonSchema);
        return jsonSchema;
    }

}
