package com.v01.techgear_server.product.controller;

import com.v01.techgear_server.discount.dto.DiscountDTO;
import com.v01.techgear_server.product.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/discount")
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping("/new-discount")
    public CompletableFuture<ResponseEntity<List<DiscountDTO>>> createDiscounts(
            @RequestBody List<DiscountDTO> discountDTO
    ) {
        return discountService.createDiscounts(discountDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error creating discounts", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }
    
    @DeleteMapping("/delete-discount/{discountId}")
    public CompletableFuture<ResponseEntity<Boolean>> deleteDiscount(
            @PathVariable Long discountId
    ) {
        return discountService.deleteDiscount(discountId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error deleting discount", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }

    @PutMapping("/update-discount/{discountId}")
    public CompletableFuture<ResponseEntity<Boolean>> updateDiscount(
            @PathVariable Long discountId,
            @RequestBody DiscountDTO discountDTO
    ) {
        return discountService.updateDiscount(discountId, discountDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error updating discount", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }

    @GetMapping("/get-discount/{discountId}")
    public CompletableFuture<ResponseEntity<DiscountDTO>> getDiscount(
            @PathVariable Long discountId
    ) {
        return discountService.getDiscountById(discountId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error getting discount", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }

    @GetMapping("/get-all-discounts")
    public CompletableFuture<ResponseEntity<List<DiscountDTO>>> getAllDiscounts() {
        return discountService.getAllDiscounts()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error getting all discounts", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }

    @GetMapping("/get-discount-details/{productId}")
    public CompletableFuture<ResponseEntity<DiscountDTO>> getDiscountDetails(
            @PathVariable Long productId
    ) {
        return discountService.getDiscountDetails(productId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error getting discount details", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }

    @PostMapping("/apply-discount/{productId}")
    public CompletableFuture<ResponseEntity<Boolean>> applyDiscount(
            @PathVariable Long productId,
            @RequestBody DiscountDTO discountDTO
    ) {
        return discountService.applyDiscount(productId, discountDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error applying discount", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }
    @DeleteMapping("/remove-discount/{productId}")
    public CompletableFuture<ResponseEntity<Boolean>> removeDiscount(
            @PathVariable Long productId
    ) {
        return discountService.removeDiscount(productId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error removing discount", ex);
                    return ResponseEntity.status(500).body(null);
                });
    }
}
