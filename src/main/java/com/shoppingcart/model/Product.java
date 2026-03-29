package com.shoppingcart.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a product that can be added to the shopping cart.
 */
public class Product {
    private final String id;
    private final String name;
    private final BigDecimal price;
    private final String category;

    public Product(String id, String name, BigDecimal price, String category) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be null or negative");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }
}
