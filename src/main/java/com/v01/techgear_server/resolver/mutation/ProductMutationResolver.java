package com.v01.techgear_server.resolver.mutation;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.ProductRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class ProductMutationResolver {
    private static Logger LOGGER = LoggerFactory.getLogger(ProductMutationResolver.class);
    private final ProductRepository productRepository;

    public ProductMutationResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public DataFetcher<Product> dateFetcherUpdateProductPrice() {
        DataFetcher<Product> retVal = new DataFetcher<Product>() {

            @Override
            public Product get(DataFetchingEnvironment environment) throws Exception {
                Long id = environment.getArgument("id");
                double newPrice = environment.getArgument("price");
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
                product.setPrice(newPrice);
                LOGGER.info("Updated product with id: {}", id);
                return productRepository.save(product);
            }

        };
        return retVal;
    }

    public DataFetcher<Product> dataFetcherUpdateAllProduct() {
        DataFetcher<Product> retVal = new DataFetcher<Product>() {

            @Override
            public Product get(DataFetchingEnvironment env) throws Exception {
                Long id = env.getArgument("id");
                Map<String, Object> input = env.getArgument("input");

                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

                String newName = input.getOrDefault("name", product.getName()).toString();
                double newPrice = Double
                        .parseDouble(input.getOrDefault("price", Double.toString(product.getPrice())).toString());
                Category newCategory = Category
                        .valueOf(input.getOrDefault("category", product.getCategory().toString()).toString());

                product.setName(newName);
                product.setPrice(newPrice);
                product.setCategory(newCategory);

                LOGGER.info("Updated product with id: {}", id);

                return productRepository.save(product);
            }

        };
        return retVal;
    }

    public DataFetcher<Product> dataFetcherUpdateProductName() {
        DataFetcher<Product> retVal = new DataFetcher<Product>() {

            @Override
            public Product get(DataFetchingEnvironment env) throws Exception {
                Long id = env.getArgument("id");
                Map<String, Object> input = env.getArgument("name");

                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
                String productNewName = input.getOrDefault("name", product.getName()).toString();


                // Check if the new name already exists
                Optional<Product> existingProduct = productRepository.findProductName(productNewName);
                if (existingProduct.isPresent() && !existingProduct.get().getProduct_id().equals(id)) {
                    
                    throw new RuntimeException("Product name '" + input + "' already exists.");
                }

                product.setName(input.getOrDefault("name", product.getName()).toString());

                LOGGER.info("Updated product name: {}", product.getName());

                return productRepository.save(product);

            }

        };
        return retVal;
    }
}
