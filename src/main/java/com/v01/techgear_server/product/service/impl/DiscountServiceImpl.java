package com.v01.techgear_server.product.service.impl;

import com.v01.techgear_server.discount.dto.DiscountDTO;
import com.v01.techgear_server.discount.mapping.DiscountMapper;
import com.v01.techgear_server.discount.model.Discount;
import com.v01.techgear_server.discount.repository.DiscountRepository;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.product.repository.ProductRepository;
import com.v01.techgear_server.product.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final ProductRepository productRepository;

    @Override
    public boolean validateDiscountCode(String discountCode, Long productId) {
        if (discountCode == null || discountCode.isEmpty()) {
            throw new BadRequestException("Discount code cannot be null or empty");
        }
        if (productId == null) {
            throw new BadRequestException("Product ID cannot be null");
        }
        List<Discount> discounts = discountRepository.findByDiscountCode(discountCode);
        if (discounts.isEmpty()) {
            return false;
        }
        Optional<Discount> discount = discountRepository.findDiscountFromProducts(productId);
        return discount.isPresent() && discounts.contains(discount.get());
    }

    @Override
    public CompletableFuture<DiscountDTO> getDiscountDetails(Long productId) {
        if (productId == null) {
            throw new BadRequestException("Product ID cannot be null");
        }
        Optional<Discount> discount = discountRepository.findDiscountFromProducts(productId);
        if (discount.isEmpty()) {
            throw new BadRequestException("Discount not found for the given product ID");
        }
        return CompletableFuture.completedFuture(discountMapper.toDTO(discount.get()));
    }

    @Override
    public CompletableFuture<Boolean> applyDiscount(Long productId, DiscountDTO discountDTO) {
        if (productId == null || discountDTO == null) {
            throw new BadRequestException("Product ID and Discount DTO cannot be null");
        }
        if (discountDTO.getDiscountName() == null || discountDTO.getDiscountName().isEmpty()) {
            throw new BadRequestException("Discount name cannot be null or empty");
        }
        if (!productRepository.existsById(productId)) {
            throw new BadRequestException("Product not found");
        }
        Discount discount = discountMapper.toEntity(discountDTO);
        discount.setProducts(List.of(productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"))));
        Discount savedDiscount = discountRepository.save(discount);
        return CompletableFuture.completedFuture(savedDiscount != null);
    }

    @Override
    public CompletableFuture<Boolean> removeDiscount(Long productId) {
        if (productId == null) {
            return CompletableFuture.failedFuture(new BadRequestException("Product ID cannot be null"));
        }
        return CompletableFuture.supplyAsync(() -> productRepository.existsById(productId))
                .thenCompose(exists -> {
                    if (!exists) {
                        return CompletableFuture.failedFuture(new BadRequestException("Product not found"));
                    }
                    return CompletableFuture.supplyAsync(() -> discountRepository.findDiscountFromProducts(productId));
                })
                .thenCompose(discount -> {
                    if (discount.isEmpty()) {
                        return CompletableFuture.failedFuture(new BadRequestException("Discount not found"));
                    }
                    discountRepository.delete(discount.get());
                    return CompletableFuture.completedFuture(true);
                });

    }

    @Override
    public CompletableFuture<List<DiscountDTO>> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        return CompletableFuture.completedFuture(discountMapper.toDTOList(discounts));
    }

    @Override
    public CompletableFuture<DiscountDTO> getDiscountById(Long discountId) {
        if (discountId == null) {
            throw new BadRequestException("Discount ID cannot be null");
        }
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BadRequestException("Discount not found"));
        return CompletableFuture.completedFuture(discountMapper.toDTO(discount));
    }

    @Override
    public CompletableFuture<Boolean> updateDiscount(Long discountId, DiscountDTO discountDTO) {
        if (discountId == null || discountDTO == null) {
            throw new BadRequestException("Discount ID and DTO cannot be null");
        }
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BadRequestException("Discount not found"));
        discountMapper.updateEntityFromDTO(discountDTO, existingDiscount);
        Discount updatedDiscount = discountRepository.save(existingDiscount);
        return CompletableFuture.completedFuture(updatedDiscount != null);
    }

    @Override
    public CompletableFuture<Boolean> deleteDiscount(Long discountId) {
        if (discountId == null) {
            throw new BadRequestException("Discount ID cannot be null");
        }
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BadRequestException("Discount not found"));
        discountRepository.delete(discount);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<List<DiscountDTO>> createDiscounts(List<DiscountDTO> discountDTO) {
        if (discountDTO == null || discountDTO.isEmpty()) {
            throw new BadRequestException("Discount list cannot be null or empty");
        }
        if (discountDTO.getFirst().getDiscountName() == null || discountDTO.getFirst().getDiscountName().isEmpty()) {
            throw new BadRequestException("Discount name cannot be null or empty");
        }

        List<Discount> discounts = discountMapper.toEntityList(discountDTO);
        List<Discount> savedDiscounts = discountRepository.saveAll(discounts);
        return CompletableFuture.completedFuture(discountMapper.toDTOList(savedDiscounts));
    }
}
