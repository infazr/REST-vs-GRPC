package com.digiratina.app_one.controller;

import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("performance." + DataController.class.getName());

    private final RestTemplate restTemplate;
    private final ManagedChannel managedChannel;
    private final String appTwoRestUrl;

    @Autowired
    public DataController(RestTemplate restTemplate,
                          ManagedChannel managedChannel,
                          @Value("${app-two.rest.url:http://222.165.138.144:5081/api/data/rest}") String appTwoRestUrl) {
        this.restTemplate = restTemplate;
        this.managedChannel = managedChannel;
        this.appTwoRestUrl = appTwoRestUrl;
    }

    @GetMapping("/data/rest")
    public List<Map<String, String>> generateDataRest(
            @RequestParam(name = "attributes", defaultValue = "3") int attributes,
            @RequestParam(name = "array", defaultValue = "3") int arraySize,
            @RequestHeader(name = "X-Request-ID", required = false) String requestId) {

        long startTime = System.nanoTime();
        try {
            // Build and make REST call
            String url = buildRestUrl(attributes, arraySize);
            List<Map<String, String>> result = executeRestCall(url);

            // Log performance
            logPerformance("REST", startTime, attributes, arraySize, result.toString().getBytes().length);

            return result;
        } catch (Exception e) {
            logger.error("REST call to app-two failed", e);
            throw new ApiCommunicationException("Failed to communicate with app-two via REST", e);
        }
    }

    @GetMapping("/data/grpc")
    public List<Map<String, String>> generateDataGrpc(
            @RequestParam(name = "attributes", defaultValue = "3") int attributes,
            @RequestParam(name = "array", defaultValue = "3") int arraySize,
            @RequestHeader(name = "X-Request-ID", required = false) String requestId) {

        long startTime = System.nanoTime();
        try {
            com.digiratina.grpc.DataResponse response = executeGrpcCall(attributes, arraySize);

            logPerformance("gRPC", startTime, attributes, arraySize, response.getSerializedSize());

            List<Map<String, String>> result = convertGrpcResponse(response);

            return result;
        } catch (Exception e) {
            logger.error("gRPC call to app-two failed", e);
            throw new ApiCommunicationException("Failed to communicate with app-two via gRPC", e);
        }
    }

    // Helper methods

    private String buildRestUrl(int attributes, int arraySize) {
        return String.format("%s?attributes=%d&array=%d", appTwoRestUrl, attributes, arraySize);
    }

    private List<Map<String, String>> executeRestCall(String url) {
        return restTemplate.getForObject(url, List.class);
    }

    private com.digiratina.grpc.DataResponse executeGrpcCall(int attributes, int arraySize) {
        com.digiratina.grpc.DataServiceGrpc.DataServiceBlockingStub stub = com.digiratina.grpc.DataServiceGrpc.newBlockingStub(managedChannel);
        com.digiratina.grpc.DataRequest request = com.digiratina.grpc.DataRequest.newBuilder()
                .setAttributes(attributes)
                .setArraySize(arraySize)
                .build();
        return stub.generateData(request);
    }

    private List<Map<String, String>> convertGrpcResponse(com.digiratina.grpc.DataResponse response) {
        List<Map<String, String>> result = new ArrayList<>();
        for (com.digiratina.grpc.DataObject dataObject : response.getObjectsList()) {
            result.add(new HashMap<>(dataObject.getAttributesMap()));
        }
        return result;
    }

    private void logPerformance(String protocol, long startTime, int attributes, int arraySize, int responseSize) {
        long durationNanos = System.nanoTime() - startTime;
        double durationMillis = durationNanos / 1_000_000.0;

        performanceLogger.info(
                "{} Communication - Time={}ms, Size={}bytes, Attributes={}, ArraySize={}",
                protocol,
                String.format("%.3f", durationMillis),
                responseSize,
                attributes,
                arraySize
        );
    }

    public static class ApiCommunicationException extends RuntimeException {
        public ApiCommunicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}