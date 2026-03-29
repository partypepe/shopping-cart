package com.shoppingcart;

import com.shoppingcart.discount.CartDiscount;
import com.shoppingcart.discount.Discount;
import com.shoppingcart.discount.ProductDiscount;
import com.shoppingcart.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {
    private ShoppingCart cart;
    private Product headphones;
    private Product runningShoes;
    private Product coffeeMug;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        headphones = new Product("1", "Headphones", new BigDecimal("59.99"), "Electronics");
        runningShoes = new Product("2", "Running Shoes", new BigDecimal("89.99"), "Footwear");
        coffeeMug = new Product("3", "Coffee Mug", new BigDecimal("12.99"), "Kitchen");
    }

    @Test
    void testAddItem() {
        cart.addItem(headphones, 1);
        assertEquals(1, cart.getItemCount());
        assertEquals(1, cart.getTotalQuantity());
    }

    @Test
    void testAddItemWithQuantity() {
        cart.addItem(headphones, 3);
        assertEquals(1, cart.getItemCount());
        assertEquals(3, cart.getTotalQuantity());
    }

    @Test
    void testAddSameItemMultipleTimes() {
        cart.addItem(headphones, 2);
        cart.addItem(headphones, 3);
        assertEquals(1, cart.getItemCount());
        assertEquals(5, cart.getTotalQuantity());
    }

    @Test
    void testAddMultipleDifferentItems() {
        cart.addItem(headphones, 1);
        cart.addItem(runningShoes, 2);
        cart.addItem(coffeeMug, 1);
        assertEquals(3, cart.getItemCount());
        assertEquals(4, cart.getTotalQuantity());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(headphones, 1);
        cart.addItem(runningShoes, 1);
        assertTrue(cart.removeItem(headphones));
        assertEquals(1, cart.getItemCount());
        assertFalse(cart.removeItem(headphones));
    }

    @Test
    void testRemoveNonExistentItem() {
        cart.addItem(headphones, 1);
        assertFalse(cart.removeItem(runningShoes));
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testUpdateQuantity() {
        cart.addItem(headphones, 2);
        assertTrue(cart.updateQuantity(headphones, 5));
        assertEquals(5, cart.getTotalQuantity());
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testUpdateQuantityNonExistentItem() {
        cart.addItem(headphones, 1);
        assertFalse(cart.updateQuantity(runningShoes, 3));
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testCalculateTotalSingleItem() {
        cart.addItem(headphones, 1);
        BigDecimal expected = new BigDecimal("59.99");
        assertEquals(0, expected.compareTo(cart.calculateTotal()));
    }

    @Test
    void testCalculateTotalMultipleItems() {
        cart.addItem(headphones, 2);
        cart.addItem(runningShoes, 1);
        BigDecimal expected = new BigDecimal("59.99")
                .multiply(new BigDecimal("2"))
                .add(new BigDecimal("89.99"));
        assertEquals(0, expected.compareTo(cart.calculateTotal()));
    }

    @Test
    void testCalculateTotalWithMultipleQuantities() {
        cart.addItem(headphones, 3);
        cart.addItem(coffeeMug, 2);
        BigDecimal expected = new BigDecimal("59.99")
                .multiply(new BigDecimal("3"))
                .add(new BigDecimal("12.99").multiply(new BigDecimal("2")));
        assertEquals(0, expected.compareTo(cart.calculateTotal()));
    }

    @Test
    void testCalculateTotalEmptyCart() {
        BigDecimal total = cart.calculateTotal();
        assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void testIsEmpty() {
        assertTrue(cart.isEmpty());
        cart.addItem(headphones, 1);
        assertFalse(cart.isEmpty());
    }

    @Test
    void testClear() {
        cart.addItem(headphones, 1);
        cart.addItem(runningShoes, 1);
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void testAddItemNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
    }

    @Test
    void testAddItemInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(headphones, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(headphones, -1));
    }

    @Test
    void testRemoveItemNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> cart.removeItem(null));
    }

    @Test
    void testUpdateQuantityNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(null, 1));
    }

    @Test
    void testUpdateQuantityInvalidQuantity() {
        cart.addItem(headphones, 1);
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(headphones, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(headphones, -1));
    }

    @Test
    void testGetItemsReturnsCopy() {
        cart.addItem(headphones, 1);
        var items = cart.getItems();
        items.clear();
        assertEquals(1, cart.getItemCount());
    }

    // --- Discount tests ---

    @Test
    void testNoDiscountsGivesFullPrice() {
        cart.addItem(runningShoes, 1);
        assertEquals(0, new BigDecimal("89.99").compareTo(cart.calculateTotal()));
        assertEquals(0, cart.calculateSubtotal().compareTo(cart.calculateTotal()));
    }

    @Test
    void testProductDiscountOnly() {
        cart.addItem(runningShoes, 1); // $89.99
        cart.addDiscount(new ProductDiscount("2", new BigDecimal("0.10"))); // 10% off
        // 89.99 * 0.10 = 9.00 discount -> 89.99 - 9.00 = 80.99
        assertEquals(0, new BigDecimal("80.99").compareTo(cart.calculateTotal()));
    }

    @Test
    void testCartDiscountOnly() {
        cart.addItem(headphones, 1); // $59.99
        cart.addDiscount(new CartDiscount(new BigDecimal("0.10"))); // 10% off
        // 59.99 * 0.10 = 6.00 discount -> 59.99 - 6.00 = 53.99
        assertEquals(0, new BigDecimal("53.99").compareTo(cart.calculateTotal()));
    }

    @Test
    void testProductAndCartDiscountStackAdditively() {
        cart.addItem(runningShoes, 1); // $89.99
        cart.addDiscount(new ProductDiscount("2", new BigDecimal("0.15"))); // 15% product
        cart.addDiscount(new CartDiscount(new BigDecimal("0.10")));         // 10% cart
        // product discount: 89.99 * 0.15 = 13.50
        // cart discount:    89.99 * 0.10 = 9.00
        // total discount: 22.50, final: 89.99 - 22.50 = 67.49
        assertEquals(0, new BigDecimal("67.49").compareTo(cart.calculateTotal()));
    }

    @Test
    void testMultipleItemsWithMixedDiscounts() {
        cart.addItem(runningShoes, 1); // $89.99
        cart.addItem(headphones, 1);   // $59.99
        cart.addDiscount(new ProductDiscount("2", new BigDecimal("0.15"))); // 15% off running shoes only
        cart.addDiscount(new CartDiscount(new BigDecimal("0.10")));         // 10% off everything

        // Running shoes: 89.99 - (13.50 + 9.00) = 67.49
        // Headphones:    59.99 - (0 + 6.00) = 53.99
        BigDecimal expected = new BigDecimal("67.49").add(new BigDecimal("53.99"));
        assertEquals(0, expected.compareTo(cart.calculateTotal()));
    }

    @Test
    void testProductDiscountDoesNotAffectOtherProducts() {
        cart.addItem(headphones, 1);   // $59.99
        cart.addItem(runningShoes, 1); // $89.99
        cart.addDiscount(new ProductDiscount("2", new BigDecimal("0.20"))); // 20% off running shoes

        // Headphones: full price 59.99
        // Running shoes: 89.99 * 0.20 = 18.00, discounted = 89.99 - 18.00 = 71.99
        BigDecimal expected = new BigDecimal("59.99").add(new BigDecimal("71.99"));
        assertEquals(0, expected.compareTo(cart.calculateTotal()));
    }

    @Test
    void testSubtotalUnaffectedByDiscounts() {
        cart.addItem(headphones, 2);   // $119.98
        cart.addItem(runningShoes, 1); // $89.99
        cart.addDiscount(new ProductDiscount("1", new BigDecimal("0.10")));
        cart.addDiscount(new CartDiscount(new BigDecimal("0.05")));

        BigDecimal expectedSubtotal = new BigDecimal("209.97");
        assertEquals(0, expectedSubtotal.compareTo(cart.calculateSubtotal()));
        assertTrue(cart.calculateTotal().compareTo(cart.calculateSubtotal()) < 0);
    }

    @Test
    void testRemoveDiscount() {
        cart.addItem(runningShoes, 1);
        Discount discount = new ProductDiscount("2", new BigDecimal("0.10"));
        cart.addDiscount(discount);
        assertEquals(0, new BigDecimal("80.99").compareTo(cart.calculateTotal()));

        assertTrue(cart.removeDiscount(discount));
        assertEquals(0, new BigDecimal("89.99").compareTo(cart.calculateTotal()));
    }

    @Test
    void testDiscountCappedAtLineTotal() {
        cart.addItem(runningShoes, 1); // $89.99
        cart.addDiscount(new ProductDiscount("2", new BigDecimal("0.60")));
        cart.addDiscount(new CartDiscount(new BigDecimal("0.50")));
        // 60% + 50% = 110% but capped at 100%
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.calculateTotal()));
    }

    @Test
    void testAddDiscountNull() {
        assertThrows(IllegalArgumentException.class, () -> cart.addDiscount(null));
    }

    @Test
    void testInvalidProductDiscountRate() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductDiscount("1", new BigDecimal("-0.01")));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductDiscount("1", new BigDecimal("1.01")));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductDiscount("1", null));
    }

    @Test
    void testInvalidCartDiscountRate() {
        assertThrows(IllegalArgumentException.class,
                () -> new CartDiscount(new BigDecimal("-0.01")));
        assertThrows(IllegalArgumentException.class,
                () -> new CartDiscount(new BigDecimal("1.01")));
        assertThrows(IllegalArgumentException.class,
                () -> new CartDiscount(null));
    }

    @Test
    void testInvalidProductDiscountId() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductDiscount(null, new BigDecimal("0.10")));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductDiscount("", new BigDecimal("0.10")));
    }

    @Test
    void testClearAlsoRemovesDiscounts() {
        cart.addItem(runningShoes, 1);
        cart.addDiscount(new CartDiscount(new BigDecimal("0.10")));
        cart.clear();
        assertTrue(cart.getDiscounts().isEmpty());
    }

    @Test
    void testGetDiscountsReturnsCopy() {
        cart.addDiscount(new CartDiscount(new BigDecimal("0.10")));
        var copy = cart.getDiscounts();
        copy.clear();
        assertEquals(1, cart.getDiscounts().size());
    }
}
