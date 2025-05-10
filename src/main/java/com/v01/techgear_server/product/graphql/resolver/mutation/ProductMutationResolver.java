package com.v01.techgear_server.product.graphql.resolver.mutation;


import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.repository.ProductRepository;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Caching;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProductMutationResolver {
    private final ProductRepository productRepository;


    @Caching(
            put = @org.springframework.cache.annotation.CachePut(value = "productCache", key = "#productId"),
            evict = @org.springframework.cache.annotation.CacheEvict(value = "productCache", allEntries = true)
    )
    @MutationMapping
    public Product updateProductImage(@Argument("productId") Long productId,
                                      @Argument("imageUrl") String image) {
        if (productId == null || image == null) {
            log.error("Product ID and image are required but were null");
            throw new GraphQLException("Product ID and image are required");
        }

        log.info("Updating product image for product ID: {}", productId);

        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new GraphQLException("Product not found with ID: " + productId);
                });
    }
}
