package com.digiratina.app_one.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Value("${app-two.grpc.host:222.165.138.144}")
    private String appTwoGrpcHost;

    @Value("${app-two.grpc.port:5090}")
    private int appTwoGrpcPort;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(appTwoGrpcHost, appTwoGrpcPort)
                .usePlaintext()
                .build();
    }
}