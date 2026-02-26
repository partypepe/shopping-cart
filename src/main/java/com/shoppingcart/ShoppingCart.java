package com.shoppingcart;

import com.shoppingcart.discount.Discount;
import com.shoppingcart.model.CartItem;
import com.shoppingcart.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a shopping cart that can hold items with quantities.
 * Supports an extensible discount system via the Discount interface.
 * Uses a Map internally for O(1) product lookup by ID.
 */
public class ShoppingCart {
    private final Map<String, CartItem> items;
    private final List<Discount> discounts;

    public ShoppingCart() {
        this.items = new HashMap<>();
        this.discounts = new ArrayList<>();
    }

    /**
     * Adds an item to the cart. If the item already exists, increases its quantity.
     *
     * @param product the product to add
     * @param quantity the quantity to add (must be greater than 0)
     * @throws IllegalArgumentException if product is null or quantity is invalid
     */
    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        String productId = product.getId();
        CartItem existingItem = items.get(productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.put(productId, new CartItem(product, quantity));
        }
    }

    /**
     * Removes an item from the cart completely.
     *
     * @param product the product to remove
     * @return true if the item was removed, false if it was not in the cart
     */
    public boolean removeItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return items.remove(product.getId()) != null;
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param product the product whose quantity to update
     * @param quantity the new quantity (must be greater than 0)
     * @return true if the quantity was updated, false if the product was not in the cart
     * @throws IllegalArgumentException if quantity is invalid
     */
    public boolean updateQuantity(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = items.get(product.getId());
        if (item != null) {
            item.setQuantity(quantity);
            return true;
        }
        return false;
    }

    /**
     * Adds a discount to the cart. Multiple discounts stack additively.
     */
    public void addDiscount(Discount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }
        discounts.add(discount);
    }

    /**
     * Removes a discount from the cart.
     *
     * @return true if the discount was found and removed
     */
    public boolean removeDiscount(Discount discount) {
        return discounts.remove(discount);
    }

    /**
     * Returns a copy of all discounts applied to this cart.
     */
    public List<Discount> getDiscounts() {
        return new ArrayList<>(discounts);
    }

    /**
     * Calculates the discounted total for a single cart item.
     * All discounts are applied additively; the total discount is capped at the line subtotal.
     */
    public BigDecimal getDiscountedLineTotal(CartItem item) {
        BigDecimal lineSubtotal = item.getTotalPrice();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (Discount discount : discounts) {
            totalDiscount = totalDiscount.add(discount.getDiscount(item));
        }
        totalDiscount = totalDiscount.min(lineSubtotal);
        return lineSubtotal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the total price of all items in the cart after applying all discounts.
     *
     * @return the total price as a BigDecimal
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(getDiscountedLineTotal(item));
        }
        return total;
    }

    /**
     * Calculates the subtotal before any discounts are applied.
     */
    public BigDecimal calculateSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    /**
     * Gets the number of distinct items in the cart.
     *
     * @return the number of items
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Gets the total quantity of all items in the cart.
     *
     * @return the total quantity
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * Gets a copy of all items in the cart.
     *
     * @return a list of cart items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Checks if the cart is empty.
     *
     * @return true if the cart is empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Clears all items and discounts from the cart.
     */
    public void clear() {
        items.clear();
        discounts.clear();
    }

    private static final String CART_INFO_DELIMITER = "----------------------------------------";

    /**
     * Prints all cart information: number of items, list of items (name and quantity), and total value.
     * Uses delimiters at start and end for clearer separation.
     */
    public void printCartInfo() {
        System.out.println(CART_INFO_DELIMITER);
        System.out.println("Number of items in cart: " + getItemCount());
        System.out.println("Total quantity: " + getTotalQuantity());
        if (!discounts.isEmpty()) {
            System.out.println("Discounts applied:");
            for (Discount discount : discounts) {
                System.out.println("  * " + discount.getDescription());
            }
        }
        System.out.println("Items:");
        for (CartItem cartItem : getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal discountedTotal = getDiscountedLineTotal(cartItem);
            BigDecimal lineSubtotal = cartItem.getTotalPrice();
            if (discountedTotal.compareTo(lineSubtotal) < 0) {
                System.out.printf("  - %s (x%d) @ $%s each = $%s (was $%s)%n",
                        product.getName(),
                        cartItem.getQuantity(),
                        product.getPrice(),
                        discountedTotal,
                        lineSubtotal);
            } else {
                System.out.printf("  - %s (x%d) @ $%s each = $%s%n",
                        product.getName(),
                        cartItem.getQuantity(),
                        product.getPrice(),
                        discountedTotal);
            }
        }
        BigDecimal subtotal = calculateSubtotal();
        BigDecimal total = calculateTotal();
        if (total.compareTo(subtotal) < 0) {
            System.out.println("Subtotal: $" + subtotal);
            System.out.println("You save: $" + subtotal.subtract(total));
        }
        System.out.println("Total cart value: $" + total);
        System.out.println(CART_INFO_DELIMITER);
        System.out.println();
    }
}
