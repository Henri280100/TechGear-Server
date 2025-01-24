package com.v01.techgear_server.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.config.GraphQLConfig;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphQLExecutor {
    private final GraphQLConfig graphQLConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public GraphQLExecutor(GraphQLConfig graphQLConfig, ObjectMapper objectMapper) {
        this.graphQLConfig = graphQLConfig;
        this.objectMapper = objectMapper;
        
    }

    public String executeGraphQLQuery(String queryVal) throws IOException {
        log.info("Received query: " + queryVal); // Log received query
        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = executionResult.getData();
        log.info("Execution result: " + respMap); // Log execution result
        List<GraphQLError> errors = executionResult.getErrors();

        if (!errors.isEmpty()) {
            throw new BadRequestException("GraphQL Error: " + errors.get(0).getMessage());
        }

        try {
            return objectMapper.writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to process GraphQL response", e);
        }

    }
}
