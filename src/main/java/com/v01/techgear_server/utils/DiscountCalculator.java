package com.v01.techgear_server.utils;

import com.v01.techgear_server.discount.model.Discount;
import com.v01.techgear_server.enums.DiscountType;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
public class DiscountCalculator {
    public static BigDecimal calculateFinalPrice(Product product, ProductDetail detail, List<Discount> availableDiscounts) {
        BigDecimal originalPrice = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        Discount validDiscount = findValidDiscount(detail.getVoucherCode(), availableDiscounts, product.getDiscounts(), now);

        if (validDiscount != null) {
            BigDecimal discountAmount = calculateDiscountAmount(originalPrice, validDiscount);
            BigDecimal finalPrice = originalPrice.subtract(discountAmount);

            // Debug logs
            System.out.println("✅ Discount Applied: " + validDiscount.getDiscountCode());
            System.out.println("➡ Type: " + validDiscount.getDiscountType() + ", Value: " + validDiscount.getDiscountPercentage());
            System.out.println("💰 Original Price: " + originalPrice + " | Discount Amount: " + discountAmount + " | Final Price: " + finalPrice);

            return finalPrice.max(BigDecimal.ZERO);
        }

        // No valid discount
        System.out.println("⚠ No valid discount applied. Returning original price: " + originalPrice);
        return originalPrice;
    }

    private static Discount findValidDiscount(String voucherCode, List<Discount> vouchers, List<Discount> productDiscounts, LocalDateTime now) {
        return Stream.concat(
                        (voucherCode != null
                                ? vouchers.stream().filter(d -> d.getDiscountCode().equalsIgnoreCase(voucherCode))
                                : Stream.empty()),
                        productDiscounts != null ? productDiscounts.stream() : Stream.empty()
                )
                .peek(d -> {
                    System.out.println("🔎 Checking Discount: " + d.getDiscountCode());
                    System.out.println(" - Active: " + d.getIsDiscountActive());
                    System.out.println(" - Start Date: " + d.getStartDate());
                    System.out.println(" - Expiry Date: " + d.getExpiryDate());
                    System.out.println(" - Now: " + now);
                    System.out.println(" - Valid Start: " + !d.getStartDate().isAfter(now));
                    System.out.println(" - Valid Expiry: " + !d.getExpiryDate().isBefore(now));
                })
                .filter(d -> d.getIsDiscountActive()
                        && !d.getStartDate().isAfter(now)
                        && !d.getExpiryDate().isBefore(now))
                .findFirst()
                .orElse(null);
    }

    private static BigDecimal calculateDiscountAmount(BigDecimal price, Discount discount) {
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            return price.multiply(BigDecimal.valueOf(discount.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (discount.getDiscountType() == DiscountType.AMOUNT) {
            return BigDecimal.valueOf(discount.getDiscountPercentage());
        }
        return BigDecimal.ZERO;
    }
}
