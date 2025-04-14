package com.v01.techgear_server.product.search.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;

import com.v01.techgear_server.product.search.ProductSchemaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSchemaServiceImpl implements ProductSchemaService {

	@Override
	public void createProductSchema(Client typesenseClient) {
		log.info("Attempting to connect to Typesense with client: {}", typesenseClient);
		try {
			if (typesenseClient.collections()
					.retrieve() != null) {
				log.info("Product collection already exists");
			} else {
				typesenseClient.collections()
						.create(new CollectionSchema()
								.name("product")
								.fields(Arrays.asList(
										new Field().name("productId").type(FieldTypes.INT64).facet(false),
										new Field().name("name").type(FieldTypes.STRING).facet(false),
										new Field().name("productDescription").type(FieldTypes.STRING).facet(false),
										new Field().name("price").type(FieldTypes.FLOAT).facet(true), // Enable faceting
										new Field().name("category").type(FieldTypes.STRING).facet(true),
										new Field().name("stockLevel").type(FieldTypes.INT32).facet(true),
										new Field().name("availability").type(FieldTypes.STRING).facet(true),
										new Field().name("brand").type(FieldTypes.STRING).facet(true)
								)));
				log.info("Product collection created successfully");
			}
		} catch (Exception e) {
			log.error("Error creating product collection", e);
		}
	}

}