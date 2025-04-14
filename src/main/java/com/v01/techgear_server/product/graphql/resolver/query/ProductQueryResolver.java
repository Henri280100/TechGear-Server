package com.v01.techgear_server.product.graphql.resolver.query;

import java.util.List;

import org.springframework.stereotype.Component;

import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.repository.ProductRepository;

import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductQueryResolver implements DataFetcher<List<Product>> {
    private final ProductRepository productRepository;

    @Override
    public List<Product> get(DataFetchingEnvironment environment) {
        return productRepository.findAll();

    }

    public Product getProductById(DataFetchingEnvironment environment) {
        Long id = environment.getArgument("id");
        if (id == null) {
            log.error("Product Id cannot be null");
            throw new GraphQLException("Product Id is required");
        }

        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new GraphQLException("Product not found with ID: " + id));
    }

    public Product getProductByName(DataFetchingEnvironment environment) {
        String name = environment.getArgument("name");
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            log.error("Product name cannot be null or empty");
            throw new GraphQLException("Product name is required");
        }

        return productRepository.findProductByName(name)
                .orElseThrow(() -> new GraphQLException("Product not found with name: " + name));
    }
}
