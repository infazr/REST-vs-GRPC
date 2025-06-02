package com.digiratina.app_two.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
public class DataGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(DataGeneratorService.class);

    private static final String[] POSSIBLE_ATTR_NAMES = {
            "id", "name", "value", "timestamp", "flag",
            "count", "price", "active", "description", "category"
    };

    private static final String[] POSSIBLE_ATTR_VALUES = {
            "A", "B", "C", "X", "Y", "Z",
            "true", "false", "100", "200", "300",
            "sample", "test", "demo", "temp"
    };

    private final Random random = new Random();

    public List<Map<String, String>> generateData(int attributes, int arraySize, String requestId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        long startTime = System.nanoTime();

        try {
            List<Map<String, String>> result = generateRandomObjects(attributes, arraySize);
            logger.debug("Generated {} objects with {} attributes each", arraySize, attributes);
            return result;
        } finally {
            MDC.remove("requestId");
        }
    }

    private List<Map<String, String>> generateRandomObjects(int attributes, int arraySize) {
        List<Map<String, String>> result = new ArrayList<>(arraySize);

        for (int i = 0; i < arraySize; i++) {
            Map<String, String> obj = new HashMap<>(attributes);
            for (int j = 0; j < attributes; j++) {
                String attrName = getUniqueAttributeName(obj, j);
                String attrValue = POSSIBLE_ATTR_VALUES[random.nextInt(POSSIBLE_ATTR_VALUES.length)];
                obj.put(attrName, attrValue);
            }
            result.add(obj);
        }
        return result;
    }

    private String getUniqueAttributeName(Map<String, String> obj, int index) {
        String baseName = POSSIBLE_ATTR_NAMES[random.nextInt(POSSIBLE_ATTR_NAMES.length)];
        String attrName = baseName;
        int suffix = 1;

        while (obj.containsKey(attrName)) {
            attrName = baseName + "_" + suffix++;
        }
        return attrName;
    }
}
