package com.internverse.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {}

    public static String skillsToJson(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(skills);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
