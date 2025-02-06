package com.v01.techgear_server.config;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.resources.Node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.context.annotation.Configuration
public class TypesenseConfig {
    @Value("${typesense.apiKey}")
    private String apiKey;

    @Value("${typesense.host}")
    private String host;

    @Value("${typesense.port}")
    private String port;

    @Value("${typesense.protocol}")
    private String protocol;

    @Value("${typesense.connection-timeout-seconds}")
    private int connectionTimeoutSeconds;

    @Bean
    public Client typesenseClient() {
        List<Node> nodes = List.of(new Node(protocol, host, port));
        Configuration conf = new Configuration(
            nodes,
            Duration.ofSeconds(connectionTimeoutSeconds),
            apiKey);
        Client client = new Client(conf);
        try {
            client.health.retrieve();
            log.info("Connected to Typesense server");
        } catch (Exception e) {
            log.error("Failed to connect to Typesense server", e);
        }
        return client;
    }
}