package com.coreprovider.coreprovider.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON operations using both Gson and Jackson libraries.
 * Provides methods for converting objects to JSON string and JsonNode
 * representations.
 */
public final class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final Gson gson;
    private static final ObjectMapper mapper;

    static {
        gson = new Gson();
        mapper = new ObjectMapper();
    }

    // Prevent instantiation
    private JsonUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param source the object to convert
     * @return JSON string representation of the object, or null if source is null
     */
    public static String toJsonString(final Object source) {
        if (source == null) {
            return null;
        }

        return gson.toJson(source);
    }

    /**
     * Converts an object to Jackson's JsonNode representation.
     *
     * @param source the object to convert
     * @return JsonNode representation of the object, or null if source is null
     * @throws IllegalStateException if JSON parsing fails
     */
    public static JsonNode toJsonNode(final Object source) {
        if (source == null) {
            return null;
        }

        String jsonString = gson.toJson(source);

        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to JsonNode", e);
            throw new IllegalStateException("JSON parsing failed", e);
        }
    }
}