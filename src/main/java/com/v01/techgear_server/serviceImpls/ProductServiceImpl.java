package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.v01.techgear_server.dto.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ProductFilteringException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.exception.ValidationException;
import com.v01.techgear_server.mapping.ImageMapper;
import com.v01.techgear_server.mapping.ProductMapper;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.jpa.ProductRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ImageMapper imageMapper;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    private static final long MAX_IMAGE_SIZE = 1024L * 1024 * 5;
    private static final int MAX_PAGE_SIZE = 1024 * 1024 * 5;

    private void validateImageFile(MultipartFile image) {
        // Check the file type
        if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) {
            throw new BadRequestException("Invalid image file type");
        }

        // Check the file size
        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Image file size exceeds the maximum allowed size");
        }
    }

    private void validateProductDTO(ProductDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getProductName().isEmpty()) {
            throw new BadRequestException("Product name cannot be null or empty");
        }
    }

    private Image saveImage(MultipartFile image) {
        try {
            return fileStorageService.storeSingleImage(image).thenApply(imageMapper::toEntity).join();
        } catch (IOException e) {
            log.error("Error storing image", e);
            throw new BadRequestException("Failed to store image");
        }
    }

    /**
     * Create a new product.
     * 
     * @param productDTO JSON string representing the product to be created.
     * @param image      The image file to be uploaded and associated with the
     *                   product.
     * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
     *         Request status if an error occurs.
     */
    @Override
    @Transactional(rollbackFor = {
            BadRequestException.class,
            ResourceNotFoundException.class,
            IOException.class,
            DataAccessException.class
    }, noRollbackFor = {
            ValidationException.class }, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 30)
    public CompletableFuture<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image) {
        if (productDTO.getId() != null) {
            throw new BadRequestException("Product ID cannot be set");
        }

        // Validate the image file
        if (image != null && !image.isEmpty()) {
            validateImageFile(image);
        }

        validateProductDTO(productDTO);
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Save the image file if it is not null
                Image savedImage = null;
                if (image != null && !image.isEmpty()) {
                    savedImage = saveImage(image);
                }

                // Map the ProductDTO to a Product entity
                Product product = productMapper.toEntity(productDTO);

                // Associate the saved image with the product if it exists
                if (savedImage != null) {
                    product.setImage(savedImage);
                }

                // Save the product to the repository
                Product savedProduct = productRepository.save(product);

                // Map the saved product entity back to a ProductDTO
                return productMapper.toDTO(savedProduct);
            } catch (Exception e) {
                log.error("Error creating product", e);
                throw new BadRequestException("Failed to create product");
            }
        });
    }

    @Override
    @CacheEvict(value = "productCache", key = "#productId")
    @Transactional(rollbackFor = { ResourceNotFoundException.class,
            DataAccessException.class }, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<Void> deleteProduct(Long productId) {
        return CompletableFuture.runAsync(() -> {
            if (productId == null) {
                throw new BadRequestException("Product ID cannot be null");
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            productRepository.delete(product);
        });
    }

    @Override
    @CacheEvict(value = "productCache", key = "#productId")
    @Transactional(rollbackFor = {
            ResourceNotFoundException.class,
            DataAccessException.class
    }, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<ProductDTO> updateProduct(Long productId, ProductDTO productDTO, MultipartFile image) {
        return CompletableFuture.supplyAsync(() -> {
            Product existingProduct = validateProductUpdate(productId, productDTO);
            // Save the image file if it is not null
            Image savedImage = null;
            if (image != null && !image.isEmpty()) {
                savedImage = saveImage(image);
            }
            // Update the product fields
            existingProduct.setName(productDTO.getProductName());
            existingProduct.setProductDescription(productDTO.getProductDescription());
            existingProduct.setPrice(productDTO.getProductPrice());
            existingProduct.setAvailability(productDTO.getProductAvailability());
            // Associate the saved image with the product if it exists
            if (savedImage != null) {
                existingProduct.setImage(savedImage);
            }

            // Save the updated product to the repository
            Product updatedProduct = productRepository.save(existingProduct);

            // Map the updated product entity back to a ProductDTO
            return productMapper.toDTO(updatedProduct);
        });
    }

    // Comprehensive validation for product update
    private Product validateProductUpdate(Long productId, ProductDTO productDTO) {
        if (productId == null) {
            throw new BadRequestException("Product ID cannot be null");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        validateProductDTO(productDTO);
        return existingProduct;
    }

    @Override
    public CompletableFuture<Page<ProductFilterSortResponse>> productFilteringSorting(
            ProductFilterSortRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create Filter DTO
                ProductFilterDTO filterDTO = createFilterDTO(request);

                // Create Sort Configuration
                Pageable pageRequest = createPageRequest(request);

                // Create Specification
                Specification<Product> specification = createSpecification(filterDTO);

                // Execute Query
                Page<Product> products = productRepository.findAll(specification, pageRequest);

                // Map to Response DTO
                return products.map(productMapper::productFilterSortToDTO);
            } catch (Exception e) {
                log.error("Error in product filtering", e);
                throw new ProductFilteringException("Failed to filter products", e);
            }
        }, getForkJoinPool());
    }

    /**
     * Create Filter DTO from Request
     */
    private ProductFilterDTO createFilterDTO(ProductFilterSortRequest request) {
        return ProductFilterDTO.builder()
                .name(request.getName())
                .minPrice(request.getMinPrice())
                .maxPrice(request.getMaxPrice())
                .category(request.getCategory())
                .brand(request.getBrand())
                .build();
    }

    /**
     * Create Page Request with Dynamic Sorting
     */
    private Pageable createPageRequest(ProductFilterSortRequest request) {
        List<Sort.Order> sortOrders = parseSortOrders(request.getSort());

        return PageRequest.of(
                Math.max(0, request.getPage()),
                Math.min(request.getSize(), MAX_PAGE_SIZE),
                Sort.by(sortOrders));
    }

    /**
     * Parse Sort Orders from JSON StringO
     */
    private List<Sort.Order> parseSortOrders(String sortJson) {
        try {
            if (StringUtils.hasText(sortJson)) {
                List<ProductSortDTO> sortDTOs = objectMapper.readValue(
                        sortJson,
                        new TypeReference<List<ProductSortDTO>>() {
                        });

                return List.copyOf(sortDTOs.stream()
                        .map(this::convertToSortOrder)
                        .toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("Invalid sort configuration: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Convert Sort DTO to Sort Order
     */
    private Sort.Order convertToSortOrder(ProductSortDTO sortDTO) {
        Sort.Direction direction = Optional.ofNullable(sortDTO.getDirection()).filter(dir -> dir.equalsIgnoreCase("desc")).map(dir -> Sort.Direction.DESC).orElse(Sort.Direction.ASC);

        return new Sort.Order(direction, sortDTO.getField());
    }

    /**
     * Create Specification for Filtering
     */
    private Specification<Product> createSpecification(ProductFilterDTO filterDTO) {
        return ProductFilterSpecification.getProductSpecification(filterDTO);
    }

    /**
     * Custom Fork Join Pool for Async Operations
     */
    private Executor getForkJoinPool() {
        return Executors.newWorkStealingPool(
                Runtime.getRuntime().availableProcessors());
    }

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<ProductDTO> getProductByBrand(String brand) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> products = productRepository.findProductByBrand(brand);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("No products found with brand: " + brand);
            }
            return productMapper.toDTO(products.get());
        });
    }

    @Override
    @Cacheable(value = "productCache", keyGenerator = "customKeyGenerator", condition = "#category != null", unless = "#result == null")
    @Transactional(readOnly = true)
    public CompletableFuture<ProductDTO> getProductByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> products = productRepository.findProductByCategory(category);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("No products found with category: " + category);
            }
            return productMapper.toDTO(products.get());
        });
    }

    @Override
    @Cacheable(value = "productCache", keyGenerator = "customKeyGenerator", condition = "#productId != null", unless = "#result == null || #result.getProductId() == null || #result.getProductId() == 0")
    @Transactional(readOnly = true)
    public CompletableFuture<ProductDTO> getProductById(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                throw new ResourceNotFoundException("No product found with id: " + productId);
            }
            return productMapper.toDTO(product.get());
        });
    }

    @Override
    @Cacheable(value = "productCache", keyGenerator = "customKeyGenerator", condition = "#productName != null", unless = "#result == null")
    @Transactional(readOnly = true)
    public CompletableFuture<ProductDTO> getProductByName(String productName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> product = productRepository.findProductByName(productName);
            if (product.isEmpty()) {
                throw new ResourceNotFoundException("No product found with name: " + productName);
            }
            return productMapper.toDTO(product.get());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<ProductDTO>> getProductByPriceRange(Double minPrice, Double maxPrice) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findProductsByPriceRange(minPrice, maxPrice);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No products found with price range: " + minPrice + " to " + maxPrice);
            }
            return products.stream()
                    .map(productMapper::toDTO)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<Integer> checkProductStockLevel(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                throw new ResourceNotFoundException("No product found with id: " + productId);
            }
            Integer stockLevel = product.get().getStockLevel();
            if (stockLevel == 0) {
                throw new BadRequestException("Product with id: " + productId + " is out of stock");
            }
            return stockLevel;
        });
    }
}
