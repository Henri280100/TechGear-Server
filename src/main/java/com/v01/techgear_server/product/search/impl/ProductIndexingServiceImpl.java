package com.v01.techgear_server.product.search.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.exception.ProductIndexingException;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductDetailDTO;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.product.repository.ProductRepository;
import com.v01.techgear_server.product.search.ProductIndexingService;
import com.v01.techgear_server.product.search.ProductSchemaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.model.ImportDocumentsParameters;
import org.typesense.model.IndexAction;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductIndexingServiceImpl implements ProductIndexingService {
    @Autowired
    private final ProductSchemaService productSchemaService;
    private final Client typesenseClient;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        long totalProducts = productRepository.count();
        log.info("Total products in database: {}", totalProducts);

        int page = 0;
        int batchSize = 2000;
        Page<ProductDTO> productPage;
        int indexedCount = 0;

        do {
            Pageable pageable = PageRequest.of(page, batchSize);
            productPage = productRepository.findAllForIndexing(pageable);

            log.info("page: {}", productPage.get().map(ProductDTO::getFinalPrice).collect(Collectors.toList()));

            List<ProductDTO> products = productPage.getContent();
            log.info("Batch {}: Found {} products", page, products.size());

            if (!products.isEmpty()) {
                indexProductBatch(products);
                indexedCount += products.size();
            }
            page++;
        } while (productPage.hasNext());

        log.info("Sync completed. Indexed {} of {} products", indexedCount, totalProducts);
    }

    @Override
    public void indexProduct(List<ProductDTO> products) {
        indexProductBatch(products); // Delegate to batch indexing
    }


    @Override
    public void indexSingleProduct(ProductDTO productDTO) {
        indexProductBatch(Collections.singletonList(productDTO));
    }

    private void indexProductBatch(List<ProductDTO> products) {
        if (products == null || products.isEmpty()) {
            log.warn("No products to index");
            return;
        }

        try {
            // Convert ProductDTO to Typesense documents
            List<Map<String, Object>> documents = products.stream().map(dto -> {
                if (Objects.isNull(dto.getFinalPrice())) {
                    Optional<BigDecimal> finalPriceOpt = Optional.ofNullable(productRepository.getFinalPriceForProduct(dto.getId()));
                    finalPriceOpt.ifPresent(dto::setFinalPrice);
                    dto.setFinalPrice(productRepository.getFinalPriceForProduct(dto.getId()));
                }

                if (Objects.isNull(dto.getProductTags()) || dto.getProductTags().isEmpty()) {
                    Optional<Product> productOpt = productRepository.findByIdWithTags(dto.getId());
                    productOpt.ifPresent(product -> dto.setProductTags(product.getTags()));
                }


                Map<String, Object> document = new HashMap<>(13);
                document.put("id", dto.getId().toString());
                document.put("productId", dto.getId());
                document.put("name", dto.getProductName());
                document.put("productDescription", dto.getProductDescription());

                document.put("finalPrice", dto.getFinalPrice());
                List<String> tags = dto.getProductTags() != null ? dto.getProductTags() : Collections.emptyList();
                document.put("tags", tags);
                document.put("minPrice", dto.getProductMinPrice());
                document.put("maxPrice", dto.getProductMaxPrice());
                document.put("availability", dto.getProductAvailability());
                document.put("stockLevel", dto.getProductStockLevel());
                document.put("brand", dto.getProductBrand().toLowerCase());
                document.put("image", dto.getProductImage());
                document.put("features", dto.getProductFeatures());
                document.put("category", dto.getProductCategory().toLowerCase());
                return document;
            }).collect(Collectors.toList());
            BigDecimal finalPrice = productRepository.getFinalPriceForProduct(products.getFirst().getId());

            log.info("Final price: {}", finalPrice);

            // Set import parameters
            ImportDocumentsParameters parameters = new ImportDocumentsParameters();
            parameters.setAction(IndexAction.UPSERT);

            // Use import_ for bulk import
            String importResults = typesenseClient.collections("product").documents().import_(documents, parameters);

            // Only parse response if it indicates an error
            if (importResults.contains("\"success\":false")) {
                log.debug("Parsing import results due to potential errors: {}", importResults);
                Map<String, Object> response = objectMapper.readValue(importResults, new TypeReference<Map<String, Object>>() {
                });
                if (response.containsKey("results")) {
                    List<Map<String, Object>> results = objectMapper.convertValue(response.get("results"), new TypeReference<List<Map<String, Object>>>() {
                    });
                    results.forEach(result -> {
                        if (!(Boolean) result.get("success")) {
                            log.error("Failed to index document: {}", result.get("error"));
                        }
                    });
                } else if (response.containsKey("success") && !(Boolean) response.get("success")) {
                    log.error("Import failed: {}", response.get("error"));
                    throw new ProductIndexingException("Typesense import failed: " + response.get("error"));
                } else {
                    log.warn("Unexpected response format: {}", importResults);
                }
            }
        } catch (Exception e) {
            log.error("Failed to index batch: {}", e.getMessage());
            throw new ProductIndexingException("Failed to index products", e);
        }
    }



}