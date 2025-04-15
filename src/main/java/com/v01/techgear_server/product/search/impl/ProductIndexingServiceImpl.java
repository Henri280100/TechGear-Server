package com.v01.techgear_server.product.search.impl;

import com.v01.techgear_server.exception.ProductIndexingException;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.repository.ProductRepository;
import com.v01.techgear_server.product.search.ProductIndexingService;
import com.v01.techgear_server.product.search.ProductSchemaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductIndexingServiceImpl implements ProductIndexingService {
    private final Client typesenseClient;
    private final ProductRepository productRepository;
    private final ProductSchemaService productSchemaService;


    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing Typesense...");
            productSchemaService.createProductSchema(typesenseClient);
            syncAllProductsToTypesense();
            log.info("Typesense initialized and synced with Postgres");
        } catch (Exception e) {
            log.error("Failed to create product collection", e);
            throw new ProductIndexingException("Failed to initialize product collection", e);
        }
    }

    private void syncAllProductsToTypesense() {
        log.info("Syncing products to Typesense...");
        List<Product> products = productRepository.findAll();
        log.info("Found {} product in Postgres", products.size());
        for (Product product : products) {
            log.info("Indexing product: {}", product.getProductId());
            indexProduct(product);
        }
        log.info("Sync completed");
    }

    private Map<String, Object> prepareProductForIndexing(Product product) {
        Map<String, Object> documentData = new HashMap<>();

        documentData.put("id", product.getProductId().toString());
        documentData.put("productId", product.getProductId());
        documentData.put("name", product.getName());
        documentData.put("productDescription", product.getProductDescription());
        documentData.put("price", product.getPrice());
        documentData.put("minPrice", product.getMinPrice());
        documentData.put("maxPrice", product.getMaxPrice());
        documentData.put("category", product.getCategory().name());
        documentData.put("stockLevel", product.getStockLevel());
        documentData.put("brand", product.getBrand());
        documentData.put("availability", product.getAvailability().name());
        documentData.put("features", product.getFeatures());
        documentData.put("slug", product.getSlug());

        return documentData;
    }

    @Override
    public Map<String, Object> indexProduct(Product product) {
        if (product == null) {
            throw new ProductIndexingException("Cannot index null product");
        }
        try {
            // Prepare and index document
            Map<String, Object> documentMap = prepareProductForIndexing(product);
            Map<String, Object> upsertedDocument = typesenseClient
                    .collections("product")
                    .documents()
                    .upsert(documentMap);

            log.info("Product {} indexed successfully", product.getProductId());
            return upsertedDocument;
        } catch (Exception e) {
            log.error("Failed to index product {}", product.getProductId(), e);
            throw new ProductIndexingException("Indexing failed for product " + product.getProductId(), e);
        }
    }


}