package com.v01.techgear_server.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.GenericException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductDetailDTO;
import com.v01.techgear_server.product.dto.ProductSearchRequest;
import com.v01.techgear_server.product.dto.ProductSearchResponse;
import com.v01.techgear_server.product.search.ProductSearchService;
import com.v01.techgear_server.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/product")
@Tag(name = "Product", description = "Product API")
public class ProductController {
	private final ProductSearchService searchService;
	private final ProductService productService;
	
	@GetMapping("/category-name")
	@Operation(summary = "Get product by category name", description = "Get product by category name")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ProductDTO.class))),
			@ApiResponse(responseCode = "404", description = "Product not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public CompletableFuture<ResponseEntity<ProductDTO>> getProductByCategoryName(@RequestParam String categoryName) {
		return productService.getProductByCategory(categoryName)
				.thenApply(ResponseEntity::ok)
				.exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
	}
	
	
	@GetMapping("/search")
	@Operation(summary = "Search products", description = "Search products by name, category, or brand")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Products found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ProductSearchResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public CompletableFuture<ResponseEntity<ProductSearchResponse>> searchProducts(
			@Valid @ModelAttribute ProductSearchRequest request) {
		return searchService.searchProduct(request)
				.thenApply(ResponseEntity::ok)
				.exceptionally(ex -> {
					log.error("Error in searchProducts", ex);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(ProductSearchResponse.builder()
									.product(Collections.emptyList())
									.totalResult(0)
									.page(1)
									.perPage(10)
									.facets(Collections.emptyMap())
									.build());
				});
	}
	
	// Update product image
	@PutMapping("/update-image/{productId}")
	@Operation(summary = "Update product image", description = "Update product image")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product image updated",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ProductDTO.class))),
			@ApiResponse(responseCode = "404", description = "Product not found"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public CompletableFuture<ResponseEntity<ProductDTO>> updateProductImage(
			@PathVariable Long productId,
			@RequestPart("image") MultipartFile image) throws IOException {
		return productService.updateProductImage(productId, image)
				.thenApply(ResponseEntity::ok)
				.exceptionally(ex -> {
					if (ex.getCause() instanceof ResourceNotFoundException) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
					} else if (ex.getCause() instanceof BadRequestException) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
					}
				});
		
	}
	
	/**
	 * Create a new product.
	 *
	 * @param productsJson JSON string representing the product to be created.
	 * @param images       The image file to be uploaded and associated with the
	 *                     product.
	 * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
	 * Request
	 * status if an error occurs.
	 */
	@PostMapping("/new")
	public CompletableFuture<ResponseEntity<List<ProductDTO>>> createProduct(
			@RequestPart("product") String productsJson,
			@RequestPart(value = "image", required = false) List<MultipartFile> images) {
		
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			
			List<ProductDTO> dtos = mapper.readValue(productsJson, new TypeReference<List<ProductDTO>>() {
			});
			
			// Create product
			return productService.createProduct(dtos, images != null ? images : Collections.emptyList())
					.thenApply(ResponseEntity::ok)
					.exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
		} catch (IOException e) {
			log.error("Error parsing product JSON", e);
			throw new GenericException("Error parsing product JSON", e);
		}
	}
	
	@PostMapping("/new-detail")
	public CompletableFuture<ResponseEntity<List<ProductDetailDTO>>> createProductDetail(
			@RequestPart("detail") String productDetailJson,
			@RequestPart(value = "detailImage", required = false) List<MultipartFile> detailImages,
			@RequestPart(value = "video", required = false) List<MultipartFile> videos) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			
			List<ProductDetailDTO> detailDTOs = mapper.readValue(productDetailJson,
					new TypeReference<List<ProductDetailDTO>>() {
					});
			
			// Create product detail
			return productService.createProductDetail(detailDTOs,
							detailImages != null ? detailImages : Collections.emptyList(),
							videos != null ? videos : Collections.emptyList())
					.thenApply(ResponseEntity::ok)
					.exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
		} catch (IOException e) {
			log.error("Error parsing product detail JSON", e);
			throw new GenericException("Error parsing product detail JSON", e);
		}
	}
	
	
	/**
	 * Deletes a product with the given ID.
	 *
	 * @param id the ID of the product to delete
	 * @return a ResponseEntity indicating success or failure
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		log.info("Attempting to delete product with id: {}", id);
		CompletableFuture<Boolean> exists = productService.deleteProduct(id).thenApply(v -> true)
				.exceptionally(ex -> false);
		
		return exists.thenApply(result -> {
			if (Boolean.FALSE.equals(result)) {
				log.warn("Attempted to delete non-existing product with id: {}", id);
				return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
			}
			
			log.info("Successfully deleted product with id: {}", id);
			return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
		}).join();
	}
	
	
}
