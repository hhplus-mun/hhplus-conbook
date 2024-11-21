package io.hhplus.conbook.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    public static <T> String toJson(T obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
