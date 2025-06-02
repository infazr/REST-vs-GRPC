package com.digiratina.app_two.service;

import com.digiratina.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@GrpcService
public class DataService extends DataServiceGrpc.DataServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("performance." + DataService.class.getName());
    private static final Logger requestLogger = LoggerFactory.getLogger("request." + DataService.class.getName());

    private final DataGeneratorService dataGeneratorService;

    @Autowired
    public DataService(DataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }

    @Override
    public void generateData(DataRequest request, StreamObserver<DataResponse> responseObserver) {
        long startTime = System.nanoTime();
        logRequest(request);

        try {
            DataResponse response = processRequest(request);
            sendSuccessResponse(responseObserver, response);
            logPerformance(startTime, request, response);
        } catch (Exception e) {
            handleError(responseObserver, e, request);
        }
    }

    private DataResponse processRequest(DataRequest request) {
        List<Map<String, String>> generatedData = dataGeneratorService.generateData(
                request.getAttributes(),
                request.getArraySize(),
                null // or get from gRPC metadata if needed
        );
        return buildGrpcResponse(generatedData);
    }

    private DataResponse buildGrpcResponse(List<Map<String, String>> generatedData) {
        DataResponse.Builder responseBuilder = DataResponse.newBuilder();
        generatedData.forEach(dataObject -> {
            DataObject.Builder objectBuilder = DataObject.newBuilder();
            objectBuilder.putAllAttributes(dataObject);
            responseBuilder.addObjects(objectBuilder);
        });
        return responseBuilder.build();
    }

    private void sendSuccessResponse(StreamObserver<DataResponse> responseObserver, DataResponse response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void logRequest(DataRequest request) {
        requestLogger.info("gRPC Request - Attributes: {}, ArraySize: {}",
                request.getAttributes(),
                request.getArraySize());
    }

    private void logPerformance(long startTime, DataRequest request, DataResponse response) {
        long durationNanos = System.nanoTime() - startTime;
        double durationMillis = durationNanos / 1_000_000.0;
        int responseSize = response.getSerializedSize();

        performanceLogger.info("gRPC - ExecutionTime={}ms, ResponseSize={}bytes, Attributes={}, ArraySize={}",
                String.format("%.3f", durationMillis),
                responseSize,
                request.getAttributes(),
                request.getArraySize());
    }

    private void handleError(StreamObserver<DataResponse> responseObserver, Exception e, DataRequest request) {
        logger.error("gRPC Request Failed - Attributes: {}, ArraySize: {}",
                request.getAttributes(),
                request.getArraySize(),
                e);

        performanceLogger.error("gRPC Request Failed - Attributes: {}, ArraySize: {}",
                request.getAttributes(),
                request.getArraySize());

        responseObserver.onError(e);
    }
}