/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.Shopping;

import ecommerce.data.DataStore;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author asus
 */
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String customerId;
    private final Map<String, Item> items = new HashMap<>();

    private static DataStore dataStore;

    public static void setDataStore(DataStore ds) {
        dataStore = ds;
    }

    public Cart(String customerId) {
        if (customerId == null || customerId.isBlank())
            throw new IllegalArgumentException("Customer ID cannot be empty");
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    // ------------------------
    // Cart operations
    // ------------------------

    public void addItem(String productId, int quantity, double priceAtTime) {
        Item item = items.get(productId);

        if (item == null) {
            items.put(productId, new Item(productId, quantity, priceAtTime));
        } else {
            item.increaseQuantity(quantity);
        }

        save();
    }

    public void removeItem(String productId) {
        items.remove(productId);
        save();
    }

    public void clear() {
        items.clear();
        save();
    }
    
    
    

   
    //cart 
    public static void addCart(Cart cart) {
        DataStore.getInstance().getCarts().put(cart.getCustomerId(), cart);
        DataStore.getInstance().saveCarts();
    }

    public static void removeCart(String customerId) {
        DataStore.getInstance().getCarts().remove(customerId);
        DataStore.getInstance().saveCarts();
    }
    
    

    // ------------------------
    // Persistence
    // ------------------------

    public void save() {
        if (dataStore == null)
            throw new IllegalStateException("DataStore not set");

        dataStore.carts.put(customerId, this);
        dataStore.saveCarts();
    }

    public void delete() {
        if (dataStore == null)
            throw new IllegalStateException("DataStore not set");

        dataStore.carts.remove(customerId);
        dataStore.saveCarts();
    }

    @Override
    public String toString() {
        return String.format(
            "Cart{customerId=%s, items=%d)",
            customerId, items.size() /*getTotal()*/
        );
    }
}
