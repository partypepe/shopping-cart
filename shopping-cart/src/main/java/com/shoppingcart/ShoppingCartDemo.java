package com.shoppingcart;

import com.shoppingcart.discount.CartDiscount;
import com.shoppingcart.discount.Discount;
import com.shoppingcart.discount.ProductDiscount;
import com.shoppingcart.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo class showing how to use the ShoppingCart with example product data.
 * Example data is structured like a JSON array of objects: id, name, price, category.
 */
public class ShoppingCartDemo {
    private static List<Map<String, String>> exampleProducts() {
        List<Map<String, String>> catalog = new ArrayList<>();

        Map<String, String> headphonesData = new LinkedHashMap<>();
        headphonesData.put("id", "1");
        headphonesData.put("name", "Headphones");
        headphonesData.put("price", "50");
        headphonesData.put("category", "Electronics");
        catalog.add(headphonesData);

        Map<String, String> runningShoesData = new LinkedHashMap<>();
        runningShoesData.put("id", "2");
        runningShoesData.put("name", "Running Shoes");
        runningShoesData.put("price", "100");
        runningShoesData.put("category", "Footwear");
        catalog.add(runningShoesData);

        Map<String, String> coffeeMugData = new LinkedHashMap<>();
        coffeeMugData.put("id", "3");
        coffeeMugData.put("name", "Coffee Mug");
        coffeeMugData.put("price", "10");
        coffeeMugData.put("category", "Kitchen");
        catalog.add(coffeeMugData);

        Map<String, String> laptopData = new LinkedHashMap<>();
        laptopData.put("id", "4");
        laptopData.put("name", "Premium Laptop");
        laptopData.put("price", "1000");
        laptopData.put("category", "Electronics");
        catalog.add(laptopData);

        Map<String, String> winterJacketData = new LinkedHashMap<>();
        winterJacketData.put("id", "5");
        winterJacketData.put("name", "Winter Jacket");
        winterJacketData.put("price", "200");
        winterJacketData.put("category", "Clothing");
        catalog.add(winterJacketData);

        return catalog;
    }

    private static Product toProduct(Map<String, String> productData) {
        return new Product(
                productData.get("id"),
                productData.get("name"),
                new BigDecimal(productData.get("price")),
                productData.get("category"));
    }

    public static void main(String[] args) {
        List<Map<String, String>> productCatalog = exampleProducts();

        Product headphones = toProduct(productCatalog.get(0));
        Product runningShoes = toProduct(productCatalog.get(1));
        Product coffeeMug = toProduct(productCatalog.get(2));
        Product premiumLaptop = toProduct(productCatalog.get(3));
        // Winter Jacket available as toProduct(productCatalog.get(4)) if needed

        ShoppingCart cart = new ShoppingCart();

        System.out.println("=== Shopping Cart Demo ===\n");

        System.out.println("Adding items to cart...");
        cart.addItem(headphones, 2);
        cart.addItem(runningShoes, 1);
        cart.addItem(coffeeMug, 3);
        cart.printCartInfo();

        System.out.println("Applying 15% product discount on Running Shoes...");
        Discount runningShoesDiscount = new ProductDiscount("2", new BigDecimal("0.15"));
        cart.addDiscount(runningShoesDiscount);
        cart.printCartInfo();

        System.out.println("Applying 10% cart-wide discount (stacks additively)...");
        Discount cartWideDiscount = new CartDiscount(new BigDecimal("0.10"));
        cart.addDiscount(cartWideDiscount);
        cart.printCartInfo();

        System.out.println("Updating quantity of Headphones from 2 to 4...");
        cart.updateQuantity(headphones, 4);
        cart.printCartInfo();

        System.out.println("Adding Premium Laptop...");
        cart.addItem(premiumLaptop, 1);
        cart.printCartInfo();

        System.out.println("Removing the product discount on Running Shoes...");
        cart.removeDiscount(runningShoesDiscount);
        cart.printCartInfo();

        System.out.println("Final cart:");
        cart.printCartInfo();
    }
}
