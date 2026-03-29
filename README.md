# Shopping Cart

A Java shopping cart module with support for product and cart-wide discounts.

## Prerequisites

- Java 11+
- Maven 3.9+

## Build and Run

```bash
mvn clean compile
```

Run the existing unit tests (located under `src/test/`):

```bash
mvn test
```

There is also a demo client class (`ShoppingCartDemo`) that exercises the shopping cart
end-to-end — adding items, applying discounts, updating quantities, and printing cart state:

```bash
mvn exec:java -Dexec.mainClass="com.shoppingcart.ShoppingCartDemo"
```

## Project Structure

```
src/main/java/com/shoppingcart/
├── model/
│   ├── Product.java             # Product data object
│   └── CartItem.java            # Product + quantity wrapper
├── discount/
│   ├── Discount.java            # Discount interface
│   ├── ProductDiscount.java     # % off a specific product
│   └── CartDiscount.java        # % off the entire cart
├── ShoppingCart.java            # Cart aggregate / service
└── ShoppingCartDemo.java        # Demo entry point

src/test/java/com/shoppingcart/
├── model/
│   ├── ProductTest.java
│   └── CartItemTest.java
└── ShoppingCartTest.java
```

## Usage

```java
import com.shoppingcart.model.Product;
import com.shoppingcart.discount.*;
import com.shoppingcart.ShoppingCart;
import java.math.BigDecimal;

Product shoes = new Product("1", "Running Shoes", new BigDecimal("100"), "Footwear");
Product mug = new Product("2", "Coffee Mug", new BigDecimal("10"), "Kitchen");

ShoppingCart cart = new ShoppingCart();
cart.addItem(shoes, 2);
cart.addItem(mug, 3);

// Apply discounts (stack additively)
cart.addDiscount(new ProductDiscount("1", new BigDecimal("0.15"))); // 15% off shoes
cart.addDiscount(new CartDiscount(new BigDecimal("0.10")));         // 10% off entire cart
// Running Shoes gets 25% off (15% product + 10% cart)

cart.printCartInfo();
BigDecimal total = cart.calculateTotal();
```

## Features

- **Add / remove / update** items in the cart
- **Extensible discounts** via the `Discount` interface
 - `ProductDiscount` -- percentage off a specific product
 - `CartDiscount` -- percentage off every item in the cart
 - Discounts stack additively per line item
- **BigDecimal** for precise currency arithmetic
- Products identified by ID; adding the same product increases quantity
- Input validation with `IllegalArgumentException`
