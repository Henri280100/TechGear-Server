package com.v01.techgear_server.serviceimpls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.typesense.api.Client;
import org.typesense.model.ImportDocumentsParameters;
import org.typesense.model.IndexAction;

import com.v01.techgear_server.dto.BulkIndexResult;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.jpa.ProductRepository;
import com.v01.techgear_server.service.searching.services.ProductIndexingService;
import com.v01.techgear_server.service.searching.services.ProductSchemaService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductIndexingServiceImpl implements ProductIndexingService {
	private final Client typesenseClient;
	private final ProductRepository productRepository;
	private final ProductSchemaService productSchemaService;


	@PostConstruct
	public void createProductCollection() {
		try {
			// Create a collection if not exists
			productSchemaService.createProductSchema(typesenseClient);
			log.info("Product collection ensured in Typesense");
		} catch (Exception e) {
			log.error("Failed to create product collection", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BulkIndexResult bulkIndexProducts() {
		BulkIndexResult.BulkIndexResultBuilder resultBuilder = BulkIndexResult.builder()
		                                                                      .totalDocuments(0)
		                                                                      .successCount(0)
		                                                                      .errorCount(0)
		                                                                      .errorMessages(new ArrayList<>());
		try {
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				log.info("No products to index.");
				return resultBuilder.build();
			}

			// Prepare documents for indexing
			List<Map<String, Object>> productDocumentsList = products.parallelStream()
			                                                         .map(this::prepareProductForIndexing)
			                                                         .toList();

			resultBuilder.totalDocuments(productDocumentsList.size());

			for (Map<String, Object> documents : productDocumentsList) {
				performBulkImport(documents, resultBuilder);
			}

			// Build and return result
			BulkIndexResult result = resultBuilder.build();

			log.info("Bulk indexing completed. Total: {}, Success: {}, Errors: {}",
			         result.getTotalDocuments(),
			         result.getSuccessCount(),
			         result.getErrorCount());

			return result;

		} catch (Exception e) {
			log.error("Bulk indexing failed", e);

			// Build error result
			return resultBuilder
					.errorCount(resultBuilder.build()
					                         .getTotalDocuments())
					.errorMessages(Collections.singletonList(e.getMessage()))
					.build();
		}
	}

	private void performBulkImport(Map<String, Object> documents,
	                               BulkIndexResult.BulkIndexResultBuilder resultBuilder) {
		try {
			// Perform bulk import
			ImportDocumentsParameters importDocumentsParameters = new ImportDocumentsParameters()
					.action(IndexAction.UPSERT);

			typesenseClient.collections("products")
			               .documents()
			               .import_(documents.get("id")
			                                 .toString(), importDocumentsParameters);
			resultBuilder.successCount(resultBuilder.build()
			                                        .getSuccessCount() + 1);
		} catch (Exception e) {
			// Track errors
			resultBuilder.errorCount(
					resultBuilder.build()
					             .getErrorCount() + 1);
			resultBuilder.errorMessages(
					Stream.concat(
							      resultBuilder.build()
							                   .getErrorMessages()
							                   .stream(),
							      Stream.of(e.getMessage()))
					      .toList());

			log.error("Failed to index document: {}", documents, e);
		}
	}

	@Override
	public void deleteProductFromIndex(Long productId) {
		try {
			// Validate product ID
			if (productId == null) {
				log.warn("Attempted to delete product with null ID from index");
				throw new IllegalArgumentException("Product ID cannot be null");
			}

			// Check if product exists in the repository (optional but recommended)
			boolean productExists = productRepository.existsById(productId);
			if (!productExists) {
				log.warn("Attempted to delete non-existent product {} from index", productId);
				throw new ResourceNotFoundException("Product not found with ID: " + productId);
			}
			typesenseClient.collections("products")
			               .documents(productId.toString())
			               .delete();

			log.info("Product with ID {} deleted from index", productId);
		} catch (Exception e) {
			log.error("Failed to delete product {} from index", productId, e);
			throw new IllegalArgumentException("Failed to delete product from index", e);
		}
	}

	@Override
	public Map<String, Object> indexProduct(Product product) {
		try {

			if (product == null) {
				throw new IllegalArgumentException("Product cannot be null");
			}

			// Prepare and index document

			Map<String, Object> documentMap = prepareProductForIndexing(product);
			Map<String, Object> upsertedDocument = typesenseClient
					.collections("products")
					.documents()
					.upsert(documentMap);

			log.info("Product {} indexed successfully", product.getProductId());
			return upsertedDocument;
		} catch (Exception e) {
			log.error("Failed to index product", e);
			return Collections.emptyMap();
		}
	}

	private Map<String, Object> prepareProductForIndexing(Product product) {
		Map<String, Object> documentData = new HashMap<>();

		documentData.put("id", product.getProductId()
		                              .toString());
		documentData.put("name", product.getName());
		documentData.put("description", product.getProductDescription());
		documentData.put("price", product.getPrice());
		documentData.put("category", product.getCategory()
		                                    .name());
		documentData.put("brand", product.getBrand());
		documentData.put("availability", product.getAvailability()
		                                        .name());
		documentData.put("stockLevel", product.getStockLevel());

		// Optional fields
		if (product.getProductDetail() != null) {
			documentData.put("warranty", product.getProductDetail()
			                                    .getFirst()
			                                    .getWarranty());
			// Since a product can have multiple product details, we only take the first one and
			// extract its technical specifications. This is because the search index is designed
			// to have a single technical specification per product, and multiple product details
			// are not supported. If a product has multiple product details in the future, we
			// should revisit this implementation.
			documentData.put("technicalSpecifications", product.getProductDetail()
			                                                   .getFirst()
			                                                   .getTechnicalSpecifications());
		}

		return documentData;
	}

}