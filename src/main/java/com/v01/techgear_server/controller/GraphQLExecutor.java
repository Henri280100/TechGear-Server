package com.v01.techgear_server.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.config.GraphQLConfig;

import graphql.ExecutionResult;
import graphql.GraphQLError;

public class GraphQLExecutor {
    private final GraphQLConfig graphQLConfig;

    private final ObjectMapper objectMapper;

    private final Logger LOGGER;

    public GraphQLExecutor(GraphQLConfig graphQLConfig, ObjectMapper objectMapper, Logger LOGGER) {
        this.graphQLConfig = graphQLConfig;
        this.objectMapper = objectMapper;
        this.LOGGER = LOGGER;
    }

    public String executeGraphQLQuery(String queryVal) throws IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query
        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = executionResult.getData();
        LOGGER.info("Execution result: " + respMap); // Log execution result
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
