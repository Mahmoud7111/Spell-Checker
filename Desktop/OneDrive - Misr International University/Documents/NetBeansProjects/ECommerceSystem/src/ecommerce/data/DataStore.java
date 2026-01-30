/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.data;

import ecommerce.domain.users.User;
import ecommerce.domain.Shopping.Cart;
import ecommerce.domain.Shopping.Order;
import ecommerce.domain.Shopping.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author asus
 */
public class DataStore {

    
    private static DataStore instance;

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance; // if it exists reuse it
    }
    
    

    public final HashMap<String, User> users;
    public final HashMap<String, Product> products;
    public final HashMap<String, Cart> carts;
    public final HashMap<String, Order> orders;

    private final String dir = "dataFolder";   //dir -> directory path
    private final String usersFile = dir + File.separator + "users.dat";
    private final String productsFile = dir + File.separator + "products.dat";
    private final String cartsFile = dir + File.separator + "carts.dat";
    private final String ordersFile = dir + File.separator + "orders.dat";

    // File.separator : create file paths   \ زي كده ال
    
    public DataStore() {
        ensureDir();
        
        users = FileManager.loadFromFile(usersFile, new HashMap<>());    //the second argument is the default (an empty HashMap)
                                                                            //returned when the file does not exist or load fails.
        products = FileManager.loadFromFile(productsFile, new HashMap<>());
        carts = FileManager.loadFromFile(cartsFile, new HashMap<>());
        orders = FileManager.loadFromFile(ordersFile, new HashMap<>());
    }

    private void ensureDir() {   //Creates the data/ folder if it doesn’t exist.
        File d = new File(dir);  //constructs a File object representing the folder path
        if (!d.exists())
            d.mkdirs();  //a method part of File class//creates the directory//returns true if directory is created else returns false
    }

    
    
    //getters
    public HashMap<String, User> getUsers() {
        return users;
    }

    public HashMap<String, Product> getProducts() {
        return products;
    }

    public HashMap<String, Cart> getCarts() {
        return carts;
    }

    public HashMap<String, Order> getOrders() {
        return orders;
    }
    
    
    
    // Simple saves (call these after you mutate the maps)
    public void saveUsers() {
        FileManager.saveToFile(usersFile, users);
    }
    public void saveProducts(){
        FileManager.saveToFile(productsFile, products); 
    }
    public void saveCarts() {
        FileManager.saveToFile(cartsFile, carts); 
    }
    public void saveOrders() {
        FileManager.saveToFile(ordersFile, orders); 
    }

    public void saveAll() {
        saveUsers();
        saveProducts();
        saveCarts();
        saveOrders();
    }
    
    
    
   
    public List<User> searchUsers(String keyword) {
    if (keyword == null || keyword.isBlank())
        return new ArrayList<>(users.values());

    String key = keyword.toLowerCase();

    return users.values().stream()
            .filter(u ->
                    u.getId().toLowerCase().contains(key) ||
                    u.getUsername().toLowerCase().contains(key) ||
                    u.getEmail().toLowerCase().contains(key) ||
                    u.getRole().toLowerCase().contains(key)
            )
            .collect(Collectors.toList());
}

    
    
}