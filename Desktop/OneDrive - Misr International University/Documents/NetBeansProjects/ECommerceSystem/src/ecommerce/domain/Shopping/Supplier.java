/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.Shopping;

//import ecommerce.domain.Shopping.*;
import ecommerce.data.DataStore;
import ecommerce.domain.users.Seller;

import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author asus
 */
public class Supplier {

    private final String supplierId;
    private final String supplierName;

    private int totalOrders;
    private double totalRevenue;

    private final List<Product> products = new ArrayList<>();

    public Supplier(Seller seller) {
        this.supplierId = seller.getId();
        this.supplierName = seller.getUsername();
        
    }

    

    // ---------------- Getters ----------------

    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    
    // ================= REPORT =================
// List of suppliers and their products with pricing
    public static List<Supplier> listSuppliersAndPricing() {
        DataStore ds = DataStore.getInstance();
        List<Supplier> result = new ArrayList<>();

        for (var user : ds.getUsers().values()) {
            if (user instanceof Seller seller) {
                result.add(new Supplier(seller));
            }
        }

        return result;
    }


    
    // Orders of this supplier
    public List<Order> getOrders() {
        List<Order> result = new ArrayList<>();
        DataStore ds = DataStore.getInstance();

        for (Order order : ds.getOrders().values()) {

            for (Item item : order.getItems()) {
                Product p = ds.getProducts().get(item.getProductId());

                if (p != null && p.getSellerId().equals(supplierId)) {
                    result.add(order);
                    break;
                }
            }
        }
        return result;
    }
    
    
    
    // Supplier with max orders
    public static Supplier getSupplierWithMaxOrders() {
        Supplier best = null;
        int max = 0;

        for (Supplier s : listSuppliersAndPricing()) {
            int count = s.getOrders().size();
            if (count > max) {
                max = count;
                best = s;
            }
        }
        return best;
    }
    
   

    /// @return 
    public static Supplier getSupplierWithMaxRevenue() {
        List<Supplier> suppliers = listSuppliersAndPricing();

        Supplier best = null;
        double maxRevenue = -1;

        for (Supplier s : suppliers) {
            if (s.getTotalRevenue() > maxRevenue) {
                maxRevenue = s.getTotalRevenue();
                best = s;
            }
        }

        return best;
    }

    
    
    
    @Override
    public String toString() {
        return "Supplier{" +
                "id='" + supplierId + '\'' +
                ", name='" + supplierName + '\'' +
                ", orders=" + totalOrders +
                ", revenue=" + totalRevenue +
                '}';
    }
}