package com.v01.techgear_server.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.v01.techgear_server.resolver.mutation.ProductMutationResolver;
import com.v01.techgear_server.resolver.query.ProductQueryResolver;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.jsonwebtoken.io.IOException;

@Configuration
public class GraphQLConfig {

    private final ProductQueryResolver productQueryResolver;
    private final ProductMutationResolver productMutationResolver;

    public GraphQLConfig(ProductQueryResolver productQueryResolver, ProductMutationResolver productMutationResolver) {
        this.productQueryResolver = productQueryResolver;
        this.productMutationResolver = productMutationResolver;
    }

    @Bean
    public GraphQL graphQL() throws IOException, java.io.IOException {
        // SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        TypeDefinitionRegistry typeRegistry = loadSchemas();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private TypeDefinitionRegistry loadSchemas() throws IOException, java.io.IOException {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        // Load all schema files from the specified directory
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:graphql/*.graphqls");

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
                        .dataFetcher("getAllProducts", productQueryResolver)
                        .dataFetcher("getProductById", productQueryResolver))
                .type("Mutation", builder -> builder
                        .dataFetcher("updateProduct", productMutationResolver.dataFetcherUpdateAllProduct())
                        .dataFetcher("updateProductPrice", productMutationResolver.dateFetcherUpdateProductPrice()))
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        new LongScalar();
        return wiringBuilder -> wiringBuilder.scalar(LongScalar.LONG).build();
    }
}
