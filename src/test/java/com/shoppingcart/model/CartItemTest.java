package com.shoppingcart.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {
    private Product headphones;

    @BeforeEach
    void setUp() {
        headphones = new Product("1", "Headphones", new BigDecimal("59.99"), "Electronics");
    }

    @Test
    void testCartItemCreation() {
        CartItem item = new CartItem(headphones, 2);
        assertEquals(headphones, item.getProduct());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void testGetTotalPrice() {
        CartItem item = new CartItem(headphones, 3);
        BigDecimal expected = new BigDecimal("59.99").multiply(new BigDecimal("3"));
        assertEquals(0, expected.compareTo(item.getTotalPrice()));
    }

    @Test
    void testUpdateQuantity() {
        CartItem item = new CartItem(headphones, 2);
        item.setQuantity(5);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testCartItemNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
    }

    @Test
    void testCartItemInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(headphones, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(headphones, -1));
    }

    @Test
    void testSetQuantityInvalid() {
        CartItem item = new CartItem(headphones, 1);
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-1));
    }
}
