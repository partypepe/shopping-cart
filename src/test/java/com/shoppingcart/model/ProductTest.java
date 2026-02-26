package com.shoppingcart.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testProductCreation() {
        Product product = new Product("1", "Headphones", new BigDecimal("59.99"), "Electronics");
        assertEquals("1", product.getId());
        assertEquals("Headphones", product.getName());
        assertEquals(0, new BigDecimal("59.99").compareTo(product.getPrice()));
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    void testProductEquality() {
        Product product1 = new Product("1", "Headphones", new BigDecimal("59.99"), "Electronics");
        Product product2 = new Product("1", "Different Name", new BigDecimal("99.99"), "Different Category");
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testProductInequality() {
        Product product1 = new Product("1", "Headphones", new BigDecimal("59.99"), "Electronics");
        Product product2 = new Product("2", "Headphones", new BigDecimal("59.99"), "Electronics");
        assertNotEquals(product1, product2);
    }

    @Test
    void testProductNullId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(null, "Headphones", new BigDecimal("59.99"), "Electronics"));
    }

    @Test
    void testProductEmptyId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("", "Headphones", new BigDecimal("59.99"), "Electronics"));
    }

    @Test
    void testProductNullName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("1", null, new BigDecimal("59.99"), "Electronics"));
    }

    @Test
    void testProductNullPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("1", "Headphones", null, "Electronics"));
    }

    @Test
    void testProductNegativePrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("1", "Headphones", new BigDecimal("-10.00"), "Electronics"));
    }

    @Test
    void testProductZeroPrice() {
        Product product = new Product("1", "Headphones", BigDecimal.ZERO, "Electronics");
        assertEquals(0, BigDecimal.ZERO.compareTo(product.getPrice()));
    }
}
