/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.Shopping;

import ecommerce.data.DataStore;
import ecommerce.domain.users.Customer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author asus
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

     protected static DataStore dataStore;       //Static means there is one shared DataStore for all Product objects (or all Users, etc.).
                                               //We don’t want each Product instance to have its own separate DataStore
                                              //Every object of that class can access the same DataStore to save, update, or delete itself.
    public static void setDataStore(DataStore ds) {  //assigns the central DataStore to a static variable.
        dataStore = ds;
    }
    
    
    private final String orderId;
    private final String customerId;
    private final List<Item> items;
    private double totalAmount;
    private LocalDateTime orderDate;
    private boolean isPaid;

    public Order(String orderId, String customerId, List<Item> items) {
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("Order ID required");
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("Customer ID required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order must have at least one item");

        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);  // copy to prevent external modifications
        this.totalAmount = calculateTotal();
        this.orderDate = LocalDateTime.now();
        this.isPaid = false;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<Item> getItems() { return new ArrayList<>(items); }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public boolean isPaid() { return isPaid; }

    // Setters for status
    

    // Calculate total amount
    private double calculateTotal() {
        return items.stream().mapToDouble(Item::getTotal).sum();
    }
    public static void addOrder(Order order) {
        DataStore.getInstance().getOrders().put(order.getOrderId(), order);
        DataStore.getInstance().saveOrders();
    }

    public static void removeOrder(String orderId) {
        DataStore.getInstance().getOrders().remove(orderId);
        DataStore.getInstance().saveOrders();
    }

    public static Order getOrderById(String orderId) {
        return DataStore.getInstance().getOrders().get(orderId);
    }
    
    
    
    ////////////////Payment\\\\\\\\\\\\\\\\\
    public void pay() {
        if (this.isPaid) throw new IllegalStateException("Order is already paid");
        this.isPaid = true;
        DataStore.getInstance().saveOrders();
    }
    
    public void cancel() {
        DataStore.getInstance().getOrders().remove(this.orderId);
        DataStore.getInstance().saveOrders();
    }
    
    
    
    //  ////////////Order Reports\\\\\\\\\\\\\\\\\\
    
     // Orders for a customer
    public static List<Order> getOrdersForCustomer(String customerId) {
        List<Order> result = new ArrayList<>();

        for (Order order : DataStore.getInstance().getOrders().values()) {
            if (!order.customerId.equals(customerId)) 
                continue;

            result.add(order);
        }
        return result;
    }
    
        public static Customer customerWithMaxOrders() {
    Map<String, Integer> count = new HashMap<>();
    DataStore ds = DataStore.getInstance();

    for (Order order : ds.getOrders().values()) {

        count.merge(order.getCustomerId(), 1, Integer::sum);
    }

    return count.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> (Customer) ds.getUsers().get(e.getKey()))
        .orElse(null);
}

    
    
    public static Customer customerWithMaxRevenue() {
    Map<String, Double> revenue = new HashMap<>();
    DataStore ds = DataStore.getInstance();

    for (Order order : ds.getOrders().values()) {
        if (!order.isPaid()) continue;

        revenue.merge(order.getCustomerId(),
                      order.getTotalAmount(),
                      Double::sum);
    }

    return revenue.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> (Customer) ds.getUsers().get(e.getKey()))
        .orElse(null);
}

    
    
  
    public static double totalRevenue(
        LocalDateTime from,
        LocalDateTime to
) {
    double total = 0;

    for (Order order : DataStore.getInstance().getOrders().values()) {
        if (!order.isPaid()) continue;
        if (order.getOrderDate().isBefore(from) ||
            order.getOrderDate().isAfter(to)) continue;

        total += order.getTotalAmount();
    }
    return total;
}

public static double averageRevenue(
        LocalDateTime from,
        LocalDateTime to
) {
    int count = 0;
    double total = 0;

    for (Order order : DataStore.getInstance().getOrders().values()) {
        if (!order.isPaid()) continue;
        if (order.getOrderDate().isBefore(from) ||
            order.getOrderDate().isAfter(to)) continue;

        total += order.getTotalAmount();
        count++;
    }
    return count == 0 ? 0 : total / count;
}

    
    
    
    @Override
    public String toString() {
        return String.format("Order{orderId=%s, customerId=%s, items=%s, total=%.2f, date=%s, paid=%b}",
                orderId, customerId, items, totalAmount, orderDate, isPaid);
    }
}
