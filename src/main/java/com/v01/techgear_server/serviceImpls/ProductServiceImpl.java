package com.v01.techgear_server.serviceImpls;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.ProductRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    private static Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private void validateProductDTO(ProductDTO productDTO) {
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            throw new BadRequestException("Product name cannot be null or empty");
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
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        try {
            // Check for invalid data and throw BadRequestException if needed
            validateProductDTO(productDTO);

            // Store the image and get the Image entity
            Image imageEntity = fileStorageService.uploadSingleImage(image);

            // Map DTO to entity
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage(imageEntity);
            product.setReviews(null); // Ensure reviews are not set initially

            // Save the product entity
            Product savedProduct = productRepository.save(product);

            // Convert and return the ProductDTO
            return modelMapper.map(savedProduct, ProductDTO.class);

        } catch (BadRequestException e) {
            // Log specific bad request issues
            LOGGER.error("BadRequestException: {}", e.getMessage());
            throw e; // Re-throwing the exception for proper handling
        } catch (Exception e) {
            // Log general exceptions
            LOGGER.error("Exception occurred while creating product: {}", e.getMessage());
            throw new BadRequestException("Failed to create product");
        }
    }

    /**
     * Delete an existing product.
     * 
     * @param id The ID of the product to be deleted.
     * @return A boolean indicating whether the product was deleted successfully.
     */
    @Override
    public boolean deleteProduct(Long id) {
        // Find the existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        // Delete the product
        productRepository.delete(product);

        return true;
    }

}
