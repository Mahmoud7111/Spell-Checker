/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.Shopping;

import java.io.Serializable;
/**
 *
 * @author asus
 */
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String productId;
    private int quantity;
    private double price; // price per unit at time of order

    public Item(String productId, int quantity, double price) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID required");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");

        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getProductId() {
        return productId; 
    }
    public int getQuantity() {
        return quantity; 
    }
    public double getPrice() {
        return price; 
    }
     // Total cost for this line item
    public double getTotal() {
        return price * quantity;
    }
    
    // Setters
    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity;
    }
    
    public void increaseQuantity(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        this.quantity += amount;
    }
    
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

   

    @Override
    public String toString() {
        return String.format("OrderItem{productId=%s, quantity=%d, price=%.2f}", productId, quantity, price);
    }
}
