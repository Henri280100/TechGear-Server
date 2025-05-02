package com.v01.techgear_server.product.service.impl;

import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.common.service.FileStorageService;
import com.v01.techgear_server.discount.model.Discount;
import com.v01.techgear_server.discount.repository.DiscountRepository;
import com.v01.techgear_server.enums.ProductStatus;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.exception.ValidationException;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductDetailDTO;
import com.v01.techgear_server.product.dto.ProductSpecificationDTO;
import com.v01.techgear_server.product.mapping.ProductDetailMapper;
import com.v01.techgear_server.product.mapping.ProductMapper;
import com.v01.techgear_server.product.mapping.ProductSpecificationMapper;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductCategory;
import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.product.model.ProductSpecification;
import com.v01.techgear_server.product.repository.ProductCategoryRepository;
import com.v01.techgear_server.product.repository.ProductDetailRepository;
import com.v01.techgear_server.product.repository.ProductRepository;
import com.v01.techgear_server.product.service.DiscountService;
import com.v01.techgear_server.product.service.ProductService;
import com.v01.techgear_server.utils.CalculateHype;
import com.v01.techgear_server.utils.DiscountCalculator;
import com.v01.techgear_server.utils.ImageUploadService;
import com.v01.techgear_server.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final long MAX_IMAGE_SIZE = 1024L * 1024 * 5;
    private final ImageUploadService uploadService;
    private final FileStorageService fileStorageService;
    private final DiscountService discountService;

    private final ProductMapper productMapper;
    private final ProductDetailMapper productDetailMapper;
    private final ProductSpecificationMapper productSpecificationMapper;


    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository detailRepository;
    private final ProductCategoryRepository categoryRepository;


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

    private String saveImage(MultipartFile image) {
        try {
            ImageDTO imageDTO = fileStorageService.storeSingleImage(image).join();
            return imageDTO.getImageUrl();
        } catch (IOException e) {
            log.error("Error storing image", e);
            throw new BadRequestException("Failed to store image");
        }
    }


    @Override
    @Transactional(rollbackFor = {BadRequestException.class, ResourceNotFoundException.class, IOException.class, DataAccessException.class},
            noRollbackFor = {ValidationException.class}, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 30)
    @CacheEvict(value = "productSearchCache", allEntries = true)
    public CompletableFuture<List<ProductDTO>> createProduct(List<ProductDTO> productDTOs, List<MultipartFile> images) {
        if (productDTOs == null || productDTOs.isEmpty()) {
            throw new BadRequestException("Product list cannot be null or empty");
        }

        String categoryName = productDTOs.getFirst().getProductCategory();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new BadRequestException("Product category name is required");
        }

        ProductCategory category = categoryRepository.findFirstByCategoryName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + categoryName));
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                validateImageFile(image);
            }
        }
        productDTOs.forEach(this::validateProductDTO);
        try {
            List<Product> products = productMapper.toEntityList(productDTOs);

            products.forEach(p -> {
                p.setCategory(category);
                p.setProductDetails(new ArrayList<>());
                p.setProductRatings(new ArrayList<>());
                p.setOrderItems(new ArrayList<>());
                p.setWishlistItems(new ArrayList<>());
                p.setDiscounts(new ArrayList<>());
                if (p.getTags() == null) {
                    p.setTags(new ArrayList<>());
                }
                String baseSlug = SlugUtils.toSlug(p.getName());
                String slug = baseSlug;
                int count = 1;
                while (productRepository.existsBySlug(slug)) {
                    slug = baseSlug + "-" + count++;
                }
                p.setSlug(slug);
            });

            log.info("Saving products: {}", products);
            List<Product> savedProducts = productRepository.saveAll(products);
            log.info("Saved products: {}", savedProducts);

            if (images != null && !images.isEmpty()) {
                uploadService.saveImagesAndUpdateEntities(
                        images,
                        savedProducts,
                        Product::setImageUrl,
                        productRepository::saveAll
                );
            }

            return CompletableFuture.completedFuture(productMapper.toDTOList(savedProducts));
        } catch (IOException e) {
            log.error("Error storing image and update product", e);
            throw new BadRequestException("Failed to store image and update product");
        }
    }


    @Override
    @Transactional(rollbackFor = {BadRequestException.class, ResourceNotFoundException.class, IOException.class, DataAccessException.class},
            noRollbackFor = {ValidationException.class}, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 30)
    public CompletableFuture<List<ProductDetailDTO>> createProductDetail(List<ProductDetailDTO> productDetailDTOs, List<MultipartFile> detailImages, List<MultipartFile> videos) {
        if (detailImages != null && !detailImages.isEmpty()) {
            for (MultipartFile image : detailImages) {
                validateImageFile(image);
            }
        }
        try {
            if (productDetailDTOs == null || productDetailDTOs.isEmpty()) {
                throw new BadRequestException("Product detail list cannot be null or empty");
            }
            List<ProductDetail> productDetails = productDetailMapper.toEntityList(productDetailDTOs);
            List<ProductDetail> savedProductDetails = detailRepository.saveAll(productDetails);

            long daysSinceRelease = ChronoUnit.DAYS.between(LocalDate.now(), productDetails.getFirst().getReleaseDate().toLocalDate());

            /**
             * Determine the product status based on the release date.
             *
             * <ul>
             *     <li>New: 0 days since release date</li>
             *     <li>Hot: 1-7 days since release date</li>
             *     <li>Coming Soon: more than 7 days before release date</li>
             *     <li>Normal: more than 7 days after release date</li>
             * </ul>
             */
            ProductStatus status = switch ((int) ChronoUnit.DAYS.between(productDetails.getFirst().getReleaseDate().toLocalDate(), LocalDate.now())) {
                case 0 -> ProductStatus.NEW;
                case 1, 2, 3, 4, 5, 6, 7 -> ProductStatus.HOT;
                default -> daysSinceRelease < 0 ? ProductStatus.COMING_SOON : ProductStatus.NORMAL;
            };


            String hype = CalculateHype.calculateHype(status, daysSinceRelease);
            productDetails.forEach(pd -> {
                for (int i = 0; i < productDetails.size(); i++) {
                    Long productId = productDetailDTOs.get(i).getProductId();
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
                    productDetails.get(i).setProduct(product);
                    List<Discount> allDiscounts = Optional.ofNullable(pd.getVoucherCode())
                            .filter(v -> !v.trim().isEmpty())
                            .map(v -> discountRepository.findByDiscountCode(v.trim()))
                            .orElse(Collections.emptyList());

                    System.out.println("Voucher code: " + pd.getVoucherCode().trim());
                    System.out.println("Found discounts: " + allDiscounts.size());

                    // Always set price (original)
                    BigDecimal originalPrice = pd.getPrice() != null ? pd.getPrice() : BigDecimal.ZERO;
                    pd.setPrice(originalPrice);

                    // Calculate finalPrice
                    if (!allDiscounts.isEmpty()) {
                        // If there are discounts

                        pd.setFinalPrice(DiscountCalculator.calculateFinalPrice(product, pd, allDiscounts));
                    } else {
                        // No discounts ➔ finalPrice = original price
                        pd.setFinalPrice(originalPrice);
                    }
                }
                pd.setHype(hype);
                pd.setProductStatus(status);
                pd.setDayLeft(String.valueOf(daysSinceRelease));
                pd.setProductDetailsDesc(productDetailDTOs.getFirst().getProductDescription());
                pd.setTitle(productDetailDTOs.getFirst().getTitle());
                pd.setWarranty(productDetailDTOs.getFirst().getWarranty());
                pd.setReleaseDate(productDetailDTOs.getFirst().getReleaseDate().atStartOfDay());

                List<ProductSpecificationDTO> filteredDTOs = Optional.ofNullable(productDetailDTOs.getFirst().getSpecification())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(dto -> dto.getProductSpecName() != null && !dto.getProductSpecName().trim().isEmpty() &&
                                dto.getProductSpecValue() != null && !dto.getProductSpecValue().trim().isEmpty() &&
                                dto.getProductSpecGuarantee() != null && !dto.getProductSpecGuarantee().trim().isEmpty()
                        )
                        .toList();

                System.out.println("Filtered DTOs: " + filteredDTOs.size()); // for debugging

                List<ProductSpecification> specEntities = filteredDTOs.stream()
                        .map(dto -> {
                            ProductSpecification specEntity = productSpecificationMapper.toEntity(dto); // Map DTO to Entity
                            specEntity.setProductDetail(pd); // Set ProductDetail reference here
                            return specEntity;
                        })
                        .collect(Collectors.toList());


                pd.setSpecifications(specEntities);
                pd.setColors(String.valueOf(new ArrayList<>()));
            });
            if (detailImages != null && !detailImages.isEmpty()) {
                uploadService.saveImagesAndUpdateEntities(
                        detailImages,
                        savedProductDetails,
                        ProductDetail::setDetailImageUrl,
                        detailRepository::saveAll
                );
            }

            if (videos != null && !videos.isEmpty()) {
                uploadService.saveVideosAndUpdateEntity(
                        videos,
                        savedProductDetails,
                        ProductDetail::setDetailVideoUrl,
                        detailRepository::saveAll
                );
            }

            return CompletableFuture.completedFuture(productDetailMapper.toDTOList(savedProductDetails));
        } catch (IOException e) {
            log.error("Error storing image and update product detail", e);
            throw new BadRequestException("Failed to store image and update product detail");
        }
    }


    @Override
    @CacheEvict(value = "productCache", key = "#productId")
    @Transactional(rollbackFor = {ResourceNotFoundException.class,
            DataAccessException.class}, isolation = Isolation.READ_COMMITTED)
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
    @Async
    @CachePut(value = "productCache", key = "#productId")
    @Transactional(rollbackFor = {
            ResourceNotFoundException.class,
            DataAccessException.class
    }, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<ProductDTO> updateProduct(Long productId, ProductDTO productDTO, MultipartFile image) {
        return CompletableFuture.supplyAsync(() -> {
            Product existingProduct = validateProductUpdate(productId, productDTO);
            // Save the image file if it is not null
            String savedImage = null;
            if (image != null && !image.isEmpty()) {
                savedImage = saveImage(image);
            }
            // Update the product fields
            existingProduct.setName(productDTO.getProductName());
            existingProduct.setProductDescription(productDTO.getProductDescription());
            existingProduct.setMaxPrice(productDTO.getProductMaxPrice());
            existingProduct.setMinPrice(productDTO.getProductMinPrice());
            existingProduct.setAvailability(productDTO.getProductAvailability());
            // Associate the saved image with the product if it exists
            if (savedImage != null) {
                existingProduct.setImageUrl(savedImage);
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
            int stockLevel = product.get().getStockLevel();
            if (stockLevel == 0) {
                throw new BadRequestException("Product with id: " + productId + " is out of stock");
            }
            return stockLevel;
        });
    }
}
