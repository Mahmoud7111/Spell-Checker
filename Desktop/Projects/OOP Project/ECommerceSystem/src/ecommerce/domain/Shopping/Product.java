/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.Shopping;

import ecommerce.data.DataStore;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.HashMap;

import java.util.Map;

/**
 *
 * @author asus
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String productId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String sellerId;

    
    // Reference to DataStore for persistence
    private static DataStore dataStore;       //Static means there is one shared DataStore for all Product objects (or all Users, etc.).
                                               //We don’t want each Product instance to have its own separate DataStore
                                              //Every object of that class can access the same DataStore to save, update, or delete itself.
    public static void setDataStore(DataStore ds) {  //assigns the central DataStore to a static variable.
        dataStore = ds;
    }
    
    public Product(String productId, String name, String description, double price, int stock, String sellerId) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name required");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        if (sellerId == null || sellerId.isBlank()) throw new IllegalArgumentException("Seller ID required");

        this.productId = productId;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.price = price;
        this.stock = stock;
        this.sellerId = sellerId;
    }

    // Getters
    public String getProductId() {
        return productId; 
    }
    public String getName() { 
        return name;
    }
    public String getDescription() { 
        return description; 
    }
    public double getPrice() { 
        return price; 
    }
    public int getStock() { 
        return stock; 
    }
    public String getSellerId() {
        return sellerId; 
    }

    // Setters work as edit
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name required");
        this.name = name.trim();
        DataStore.getInstance().saveUsers();
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
        DataStore.getInstance().saveUsers();
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
        DataStore.getInstance().saveUsers();
    }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        this.stock = stock;
        DataStore.getInstance().saveUsers();
    }
    
     public final void setSellerId(String sellerId) {
        if (sellerId == null || sellerId.isBlank()) throw new IllegalArgumentException("Seller ID cannot be empty");
        this.sellerId = sellerId.trim();
        DataStore.getInstance().saveProducts();
    }

    // Domain logic
    public void reduceStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (quantity > stock) throw new IllegalArgumentException("Not enough stock");
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.stock += quantity;
    }

    
    // Manage     
    public void addProduct() {
        if (dataStore == null) throw new IllegalStateException("DataStore not set");
        dataStore.products.put(this.productId, this);
        dataStore.saveProducts();
    }

    
    public void removeProduct() {
        if (dataStore == null) throw new IllegalStateException("DataStore not set");
        dataStore.products.remove(this.productId);
        dataStore.saveProducts();
    }

  
    public static boolean searchProduct(Product p, String keyword) {
        if (p == null || keyword == null) return false;
        String lower = keyword.toLowerCase();
        return p.name.toLowerCase().contains(lower) ||
               p.description.toLowerCase().contains(lower) ||
               p.sellerId.toLowerCase().contains(lower);
    }

    
    
    //  /////////////Product Reports\\\\\\\\\\\\\\\\\\
    // Report: Number of pieces sold for this product in a period
    public static int getPiecesSold(
        String productId,
        LocalDateTime start,
        LocalDateTime end
) {
    DataStore ds = DataStore.getInstance();
    int total = 0;

    for (Order order : ds.getOrders().values()) {
        if (order.getOrderDate().isBefore(start) ||
            order.getOrderDate().isAfter(end)) 
            continue;

        for (Item item : order.getItems()) {
            if (item.getProductId().equals(productId)) {
                total += item.getQuantity();
            }
        }
    }
    return total;
}


    // Static report: Best seller product in a period
    public static Product getBestSellerProduct(
        LocalDateTime from,
        LocalDateTime to
) {
    Map<String, Integer> sold = new HashMap<>();

    for (Order order : DataStore.getInstance().getOrders().values()) {
        if (order.getOrderDate().isBefore(from) ||
            order.getOrderDate().isAfter(to)) {
            continue;
        }

        for (Item item : order.getItems()) {
            sold.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
    }

    String bestId = null;
    int max = 0;

    for (var e : sold.entrySet()) {
        if (e.getValue() > max) {
            max = e.getValue();
            bestId = e.getKey();
        }
    }

    return bestId == null ? null
            : DataStore.getInstance().getProducts().get(bestId);
}

    
    
    // Report: Most revenue Product over a specific period of time.
public static Product getMostRevenueProduct(
        LocalDateTime from,
        LocalDateTime to
) {
    Map<String, Double> revenue = new HashMap<>();

    for (Order order : DataStore.getInstance().getOrders().values()) {
        if (order.getOrderDate().isBefore(from) ||
            order.getOrderDate().isAfter(to))
            continue;

        for (Item item : order.getItems()) {
            revenue.merge(
                item.getProductId(),
                item.getTotal(),
                Double::sum
            );
        }
    }

    String bestId = null;
    double max = 0;

    for (var e : revenue.entrySet()) {
        if (e.getValue() > max) {
            max = e.getValue();
            bestId = e.getKey();
        }
    }

    return bestId == null ? null
            : DataStore.getInstance().getProducts().get(bestId);
}

    
    
    
    @Override
    public String toString() {
        return String.format("Product{id=%s, name=%s, price=%.2f, stock=%d, sellerId=%s}", 
                             productId, name, price, stock, sellerId);
    }
}
    
    
