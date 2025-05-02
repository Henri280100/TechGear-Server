package com.v01.techgear_server.product.service;

import com.v01.techgear_server.discount.dto.DiscountDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DiscountService {
    /**
     * Validate if a discount code is valid and applicable to a product.
     *
     * @param discountCode The discount code to validate.
     * @param productId    The ID of the product to which the discount code is applied.
     * @return True if the discount code is valid, false otherwise.
     */
    boolean validateDiscountCode(String discountCode, Long productId);

    /**
     * Get the discount details for a specific product.
     *
     * @param productId The ID of the product for which to retrieve discount details.
     * @return A DTO containing the discount details for the specified product.
     */
    CompletableFuture<DiscountDTO> getDiscountDetails(Long productId);

    /**
     * Apply a discount to a product.
     *
     * @param productId The ID of the product to which the discount will be applied.
     * @param discountDTO The DTO containing the discount details.
     * @return True if the discount was successfully applied, false otherwise.
     */
    CompletableFuture<Boolean> applyDiscount(Long productId, DiscountDTO discountDTO);
    /**
     * Remove a discount from a product.
     *
     * @param productId The ID of the product from which the discount will be removed.
     * @return True if the discount was successfully removed, false otherwise.
     */
    CompletableFuture<Boolean> removeDiscount(Long productId);
    /**
     * Get the list of all available discounts.
     *
     * @return A list of DTOs containing the details of all available discounts.
     */
    CompletableFuture<List<DiscountDTO>> getAllDiscounts();
    /**
     * Get the details of a specific discount.
     *
     * @param discountId The ID of the discount to retrieve.
     * @return A DTO containing the details of the specified discount.
     */
    CompletableFuture<DiscountDTO> getDiscountById(Long discountId);
    /**
     * Update the details of a specific discount.
     *
     * @param discountId The ID of the discount to update.
     * @param discountDTO The DTO containing the updated discount details.
     * @return True if the discount was successfully updated, false otherwise.
     */
    CompletableFuture<Boolean> updateDiscount(Long discountId, DiscountDTO discountDTO);
    /**
     * Delete a specific discount.
     *
     * @param discountId The ID of the discount to delete.
     * @return True if the discount was successfully deleted, false otherwise.
     */
    CompletableFuture<Boolean> deleteDiscount(Long discountId);
    /**
     * Create a new discount.
     *
     * @param discountDTO The DTO containing the details of the new discount.
     * @return The created DiscountDTO.
     */
    CompletableFuture<List<DiscountDTO>> createDiscounts(List<DiscountDTO> discountDTO);
}
