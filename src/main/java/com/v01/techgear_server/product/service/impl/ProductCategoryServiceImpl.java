package com.v01.techgear_server.product.service.impl;

import com.v01.techgear_server.shared.dto.ImageDTO;
import com.v01.techgear_server.shared.service.FileStorageService;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.exception.ValidationException;
import com.v01.techgear_server.product.dto.ProductCategoryDTO;
import com.v01.techgear_server.product.mapping.ProductCategoryMapper;
import com.v01.techgear_server.product.model.ProductCategory;
import com.v01.techgear_server.product.repository.ProductCategoryRepository;
import com.v01.techgear_server.product.service.ProductCategoryService;
import com.v01.techgear_server.utils.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String IMAGE_CONTENT_TYPE = "image/";
    private final ImageUploadService uploadService;
    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper productCategoryMapper;
    private final FileStorageService fileStorageService;

    private void validateImageFile(MultipartFile image) {
        // Check the file type
        if (!Objects.requireNonNull(image.getContentType()).startsWith(IMAGE_CONTENT_TYPE)) {
            throw new BadRequestException("Invalid image file type");
        }

        // Check the file size
        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Image file size exceeds the maximum allowed size");
        }
    }

    private void validateProductCategory(ProductCategoryDTO productCategoryDTO) {
        if (productCategoryDTO.getProductCategoryName() == null || productCategoryDTO.getProductCategoryName().isEmpty()) {
            throw new BadRequestException("Product category name cannot be null or empty");
        }
    }

    @Override
    @Transactional(
            rollbackFor = {BadRequestException.class, ResourceNotFoundException.class, IOException.class, DataAccessException.class},
            noRollbackFor = {ValidationException.class},
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            timeout = 30
    )
    public CompletableFuture<List<ProductCategoryDTO>> createProductCategories(List<ProductCategoryDTO> dtoList, List<MultipartFile> images) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BadRequestException("Product category list cannot be null or empty");
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                validateImageFile(image);
            }
        }

        dtoList.forEach(this::validateProductCategory);
        try {
            List<ProductCategory> categories = productCategoryMapper.toEntityList(dtoList);
            List<ProductCategory> savedCategories = categoryRepository.saveAll(categories);

            if (images != null && !images.isEmpty()) {
                uploadService.saveImagesAndUpdateEntities(
                        images,
                        savedCategories,
                        ProductCategory::setCategoryImage,
                        categoryRepository::saveAll
                );

            }

            return CompletableFuture.completedFuture(productCategoryMapper.toDTOList(savedCategories));
        } catch (DataAccessException | IOException e) {
            log.error("Error saving product categories", e);
            throw new BadRequestException("Error saving product categories");
        }
    }


    private String saveImage(MultipartFile image) {
        try {
            ImageDTO imageDTO = fileStorageService.storeSingleImage(image).join();
            return imageDTO.getImageUrl();
        } catch (IOException e) {
            log.error("Error saving image", e);
            throw new BadRequestException("Error saving image");
        }
    }

    public ProductCategory validateUpdateCategory(Long id, ProductCategoryDTO categoryDTO) {
        if (id == null) throw new BadRequestException("Category ID cannot be null");

        ProductCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));

        validateProductCategory(categoryDTO);
        return existingCategory;
    }

    @Override
    @Async
    @CachePut(value = "productCategoryCache", key = "#productCategoryId")
    @Transactional(rollbackFor = {
            IOException.class, DataAccessException.class
    }, noRollbackFor = {
            ValidationException.class
    }, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 30)
    public CompletableFuture<ProductCategoryDTO> updateProductCategory(Long id, ProductCategoryDTO productCategoryDTO, MultipartFile image) {
        return CompletableFuture.supplyAsync(() -> {
            ProductCategory existingCategory = validateUpdateCategory(id, productCategoryDTO);

            String savedImage = null;
            if (image != null && !image.isEmpty()) {
                savedImage = saveImage(image);
            }
            if (savedImage != null) {
                existingCategory.setCategoryImage(savedImage);
            }

            existingCategory.setCategoryName(productCategoryDTO.getProductCategoryName());
            ProductCategory updatedCategory = categoryRepository.save(existingCategory);
            return productCategoryMapper.toDTO(updatedCategory);
        });
    }


    @Override
    @CacheEvict(value = "productCategoryCache", key = "#productCategoryId")
    @Transactional(rollbackFor = {
            IOException.class, DataAccessException.class
    }, noRollbackFor = {
            ValidationException.class
    }, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 30)
    public CompletableFuture<Void> deleteProductCategory(Long id) {
        return CompletableFuture.runAsync(() -> {
            if (id == null) {
                throw new BadRequestException("Product category ID cannot be null");
            }
            ProductCategory category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

            categoryRepository.delete(category);
        });
    }
}
