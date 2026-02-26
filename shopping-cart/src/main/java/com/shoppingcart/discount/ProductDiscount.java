package com.shoppingcart.discount;

import com.shoppingcart.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A discount applied to a specific product by its ID.
 * e.g. 15% off Running Shoes.
 */
public class ProductDiscount implements Discount {
    private final String productId;
    private final BigDecimal rate;

    /**
     * @param productId the ID of the product this discount applies to
     * @param rate      discount rate between 0 and 1 (e.g. 0.15 for 15% off)
     */
    public ProductDiscount(String productId, BigDecimal rate) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        validateRate(rate);
        this.productId = productId;
        this.rate = rate;
    }

    @Override
    public BigDecimal getDiscount(CartItem item) {
        if (item.getProduct().getId().equals(productId)) {
            return item.getTotalPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return rate.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString()
                + "% off product " + productId;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    private static void validateRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }
    }
}
