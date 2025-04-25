package com.v01.techgear_server.utils;

import com.v01.techgear_server.discount.model.Discount;
import com.v01.techgear_server.enums.DiscountType;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class DiscountCalculator {
    public static BigDecimal calculateFinalPrice(Product product, ProductDetail detail, List<Discount> availableDiscounts) {
        BigDecimal originalPrice = detail.getPrice();
        BigDecimal finalPrice = originalPrice;
        LocalDateTime now = LocalDateTime.now();
        Discount validDiscount = findValidDiscount(detail.getVoucherCode(), availableDiscounts, product.getDiscounts(), now);

        if (validDiscount != null) {
            BigDecimal discountAmount = calculateDiscountAmount(originalPrice, validDiscount);
            finalPrice = originalPrice.subtract(discountAmount);
        }

        return finalPrice.max(BigDecimal.ZERO);
    }

    private static Discount findValidDiscount(String voucherCode, List<Discount> vouchers, List<Discount> productDiscounts, LocalDateTime now) {
        return Stream.concat(
                        (voucherCode != null ? vouchers.stream().filter(d -> d.getDiscountCode().equalsIgnoreCase(voucherCode)) : Stream.empty()),
                        productDiscounts != null ? productDiscounts.stream() : Stream.empty()
                )
                .filter(d -> d.getIsDiscountActive() &&
                        LocalDateTime.parse(d.getStartDate()).isBefore(now) &&
                        LocalDateTime.parse(d.getExpiryDate()).isAfter(now))
                .findFirst().orElse(null);
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
