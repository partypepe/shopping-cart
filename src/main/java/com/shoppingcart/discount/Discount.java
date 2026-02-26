package com.shoppingcart.discount;

import com.shoppingcart.model.CartItem;

import java.math.BigDecimal;

/**
 * Represents a discount that can be applied to items in a shopping cart.
 * Each implementation returns the discount amount for a given cart item.
 * Multiple discounts stack additively.
 */
public interface Discount {

    /**
     * Calculates the discount amount for a given cart item.
     *
     * @param item the cart item to evaluate
     * @return the discount amount (>= 0). Return ZERO if this discount does not apply to the item.
     */
    BigDecimal getDiscount(CartItem item);

    /**
     * Returns a human-readable description of this discount.
     */
    String getDescription();
}
