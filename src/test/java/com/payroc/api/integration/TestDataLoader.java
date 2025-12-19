package com.payroc.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.ObjectMappers;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for loading test data from JSON files.
 */
public class TestDataLoader {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMappers.JSON_MAPPER;

    /**
     * Loads test data from a JSON file in the testdata directory.
     *
     * @param filename The name of the JSON file (without path)
     * @param clazz The class type to deserialize into
     * @return The deserialized object
     * @throws RuntimeException if the file cannot be found or parsed
     */
    public static <T> T load(String filename, Class<T> clazz) {
        String resourcePath = "/testdata/" + filename;

        try (InputStream inputStream = TestDataLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Test data file not found: " + resourcePath + ". "
                        + "Make sure the file exists in src/test/resources/testdata/");
            }

            return OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from " + resourcePath, e);
        }
    }
}
