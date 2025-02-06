package com.v01.techgear_server.serviceimpls;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;

import com.v01.techgear_server.service.searching.services.ProductSchemaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSchemaServiceImpl implements ProductSchemaService {

	@Override
	public void createProductSchema(Client typesenseClient) {
		try {
			if (typesenseClient.collections()
			                   .retrieve() != null) {
				log.info("Product collection already exists");
			} else {
				typesenseClient.collections()
				               .create(new CollectionSchema()
						                       .name("products")
						                       .fields(Arrays.asList(
								                       new Field().name("productId")
								                                  .type(FieldTypes.INT64)
								                                  .facet(false),
								                       new Field().name("name")
								                                  .type(FieldTypes.STRING)
								                                  .facet(true),
								                       new Field().name("productDescription")
								                                  .type(FieldTypes.STRING)
								                                  .facet(false),
								                       new Field().name("price")
								                                  .type(FieldTypes.FLOAT),
								                       new Field().name("category")
								                                  .type(FieldTypes.STRING)
								                                  .facet(true),
								                       new Field().name("sockLevel")
								                                  .type(FieldTypes.INT32),
								                       new Field().name("brand")
								                                  .type(FieldTypes.STRING)
								                                  .facet(true))));
				log.info("Product collection created successfully");
			}
		} catch (Exception e) {
			log.error("Error creating product collection", e);
		}
	}

	@Override
	public void deleteProductSchema(Client typesenseClient) {
		try {
			typesenseClient.collections("product")
			               .delete();
			log.info("Product collection deleted successfully");
		} catch (Exception err) {
			log.error("Error deleting product collection", err);
		}

	}

}