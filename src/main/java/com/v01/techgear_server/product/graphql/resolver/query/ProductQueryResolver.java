package com.v01.techgear_server.product.graphql.resolver.query;

import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductCategory;
import com.v01.techgear_server.product.repository.ProductCategoryRepository;
import com.v01.techgear_server.product.repository.ProductRepository;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Caching;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProductQueryResolver {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    @QueryMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    @QueryMapping
    public Product getProductById(@Argument("productId") Long productID) {
        if (productID == null) {
            log.error("Product ID is required but was null");
            throw new GraphQLException("Product ID is required");
        }

        log.info("Fetching product with ID: {}", productID);
        return productRepository.findById(productID)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productID);
                    return new GraphQLException("Product not found with ID: " + productID);
                });
    }

    @QueryMapping
    public Product getProductByName(@Argument String name) {
        if (name == null || name.trim().isEmpty()) {
            log.error("Product name is required but was null or empty");
            throw new GraphQLException("Product name is required");
        }

        log.info("Fetching product with name: {}", name);
        return productRepository.findProductByName(name)
                .orElseThrow(() -> {
                    log.error("Product not found with name: {}", name);
                    return new GraphQLException("Product not found with name: " + name);
                });
    }

    @QueryMapping
    public ProductCategory getAllCategories() {
        log.info("Fetching all product categories");
        return categoryRepository.findAll().getFirst();
    }


}
