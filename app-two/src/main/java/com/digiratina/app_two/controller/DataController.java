package com.digiratina.app_two.controller;

import com.digiratina.app_two.service.DataGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("performance." + DataController.class.getName());
    private static final Logger requestLogger = LoggerFactory.getLogger("request." + DataController.class.getName());

    private final DataGeneratorService dataGeneratorService;

    @Autowired
    public DataController(DataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }

    @GetMapping("/rest")
    public List<Map<String, String>> generateDataRest(
            @RequestParam(name = "attributes", defaultValue = "3") int attributes,
            @RequestParam(name = "array", defaultValue = "3") int arraySize,
            @RequestHeader(name = "X-Request-ID", required = false) String requestId) {

        logRequest("REST", requestId, attributes, arraySize);
        long startTime = System.nanoTime();

        try {
            List<Map<String, String>> result = dataGeneratorService.generateData(attributes, arraySize, requestId);
            logPerformance(startTime, "REST", attributes, arraySize, result);
            return result;
        } catch (Exception e) {
            logError("REST", requestId, e);
            throw e;
        }
    }

    private void logRequest(String protocol, String requestId, int attributes, int arraySize) {
        requestLogger.info("{} Request - ID: {}, Attributes: {}, ArraySize: {}",
                protocol,
                requestId != null ? requestId : "N/A",
                attributes,
                arraySize);
    }

    private void logPerformance(long startTime, String protocol, int attributes, int arraySize, List<Map<String, String>> result) {
        long durationNanos = System.nanoTime() - startTime;
        double durationMillis = durationNanos / 1_000_000.0;
        int responseSize = calculateResponseSize(result);

        performanceLogger.info("{} - ExecutionTime={}ms, ResponseSize={}bytes, Attributes={}, ArraySize={}",
                protocol,
                String.format("%.3f", durationMillis),
                responseSize,
                attributes,
                arraySize);
    }

    private int calculateResponseSize(List<Map<String, String>> result) {
        try {
            return result.toString().getBytes().length;
        } catch (Exception e) {
            logger.warn("Failed to calculate response size", e);
            return -1;
        }
    }

    private void logError(String protocol, String requestId, Exception e) {
        logger.error("{} Request Failed - ID: {}", protocol, requestId != null ? requestId : "N/A", e);
        performanceLogger.error("{} Request Failed - ID: {}", protocol, requestId != null ? requestId : "N/A");
    }
}