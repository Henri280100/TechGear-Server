package com.v01.techgear_server.product.search.impl;

import com.v01.techgear_server.product.search.ProductSchemaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionResponse;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductSchemaServiceImpl implements ProductSchemaService {
    private static final Logger log = LoggerFactory.getLogger(ProductSchemaServiceImpl.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Override
    public void createProductSchema(Client typesenseClient) {
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            Runnable task = new Runnable() {
                private int attempt = 1;

                @Override
                public void run() {
                    try {
                        log.info("Attempting to retrieve existing collections (Attempt {}/{})...", attempt, MAX_RETRIES);
                        List<CollectionResponse> collections = List.of(typesenseClient.collections().retrieve());
                        log.info("Found collections: {}", collections);

                        if (collections.stream().noneMatch(c -> c.getName().equals("product"))) {
                            log.info("Creating 'product' collection...");
                            typesenseClient.collections().create(new CollectionSchema()
                                    .name("product")
                                    .fields(Arrays.asList(
                                            new Field().name("productId").type(FieldTypes.INT64).facet(false),
                                            new Field().name("name").type(FieldTypes.STRING).facet(false),
                                            new Field().name("productDescription").type(FieldTypes.STRING).facet(false),
                                            new Field().name("price").type(FieldTypes.FLOAT).facet(true),
                                            new Field().name("minPrice").type(FieldTypes.FLOAT).facet(true),
                                            new Field().name("maxPrice").type(FieldTypes.FLOAT).facet(true),
                                            new Field().name("category").type(FieldTypes.STRING).facet(true),
                                            new Field().name("stockLevel").type(FieldTypes.INT32).facet(false),
                                            new Field().name("brand").type(FieldTypes.STRING).facet(true),
                                            new Field().name("availability").type(FieldTypes.STRING).facet(true),
                                            new Field().name("features").type(FieldTypes.STRING).facet(false),
                                            new Field().name("slug").type(FieldTypes.STRING).facet(false)
                                    )));
                            log.info("Product collection created successfully");
                        } else {
                            log.info("Product collection already exists");
                        }
                        scheduler.shutdown();
                    } catch (Exception e) {
                        log.error("Attempt {}/{} failed to create product collection", attempt, MAX_RETRIES, e);
                        if (attempt++ >= MAX_RETRIES) {
                            scheduler.shutdown();
                            throw new RuntimeException("Failed to create Typesense collection after " + MAX_RETRIES + " attempts", e);
                        }
                    }
                }
            };

            scheduler.scheduleWithFixedDelay(task, 0, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
        }
    }

}