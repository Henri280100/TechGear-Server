package com.v01.techgear_server.resolver.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.ProductRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class ProductQueryResolver implements DataFetcher<List<Product>> {
    private static Logger LOGGER = LoggerFactory.getLogger(ProductQueryResolver.class);
    private final ProductRepository productRepository;

    public ProductQueryResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> get(DataFetchingEnvironment environment) {
        LOGGER.info("Fetching all products...");
        return productRepository.findAll();
    }

    public Product getProductById(DataFetchingEnvironment environment) {
        Long id = environment.getArgument("id");
        LOGGER.info("Fetching product with id: {}", id);

        return productRepository.findById(id).orElse(null);
    }

    public Product getProductName(DataFetchingEnvironment environment) {
        String name = environment.getArgument("name");
        LOGGER.info("Fetching product with name: {}", name);
        return productRepository.findProductName(name).orElse(null);
    }

}
