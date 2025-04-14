package com.v01.techgear_server.config;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.graphql.GraphQLExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import com.v01.techgear_server.product.graphql.resolver.query.ProductQueryResolver;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Configuration
public class GraphQLConfig {

    private final ProductQueryResolver productQueryResolver;

    public GraphQLConfig(ProductQueryResolver productQueryResolver) {
        this.productQueryResolver = productQueryResolver;

    }

    @Bean
    public GraphQL graphQL() throws IOException {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        TypeDefinitionRegistry typeRegistry = loadSchemas();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private TypeDefinitionRegistry loadSchemas() throws IOException {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        // Load all schema files from the specified directory
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:graphql/*.graphql");

        for (Resource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                typeRegistry.merge(schemaParser.parse(inputStream));
            }
        }

        return typeRegistry;
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("getProductById", productQueryResolver)
                        .dataFetcher("getProductByName", productQueryResolver)
                )
                .build();
    }

    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(LongScalar.LONG).build();
    }

    @Bean
    GraphQLExecutor graphQLExecutor(GraphQLConfig config, ObjectMapper objectMapper) {
        return new GraphQLExecutor(config, objectMapper);
    }
}
