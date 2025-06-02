package com.digiratina.app_two.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Value("${grpc.server.host:localhost}")
    private String grpcServerHost;

    @Value("${grpc.server.port:9090}")
    private int grpcServerPort;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
                .usePlaintext() 
                .build();
    }
}
