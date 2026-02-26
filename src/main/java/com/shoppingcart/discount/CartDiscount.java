package com.shoppingcart.discount;

import com.shoppingcart.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A cart-wide discount applied to every item in the cart.
 * e.g. 10% off the entire cart.
 */
public class CartDiscount implements Discount {
    private final BigDecimal rate;

    /**
     * @param rate discount rate between 0 and 1 (e.g. 0.10 for 10% off)
     */
    public CartDiscount(BigDecimal rate) {
        validateRate(rate);
        this.rate = rate;
    }

    @Override
    public BigDecimal getDiscount(CartItem item) {
        return item.getTotalPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return rate.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString()
                + "% off entire cart";
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
