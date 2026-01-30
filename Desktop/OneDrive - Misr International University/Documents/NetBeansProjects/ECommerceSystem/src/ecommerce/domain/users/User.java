package ecommerce.domain.users;

import ecommerce.data.*;

import java.io.Serializable;
import java.time.LocalDateTime;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author asus
 */


public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;            
    private String username;
    private String password;            
    private String email;
    private final LocalDateTime registeredDate;  
    private final String role;          

    
    protected static DataStore dataStore;   

    public static void setDataStore(DataStore ds) {
        dataStore = ds;
    }
    
    
    // constructor 
    public User(String id, String username, String password, String email, String role, LocalDateTime registeredDate) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;

    // If loaded from DB/file → use existing date
    // If newly created → use now()
    this.registeredDate = (registeredDate != null) 
            ? registeredDate 
            : LocalDateTime.now();
    setUsername(username);   //This ensures that every Customer object is always created with valid data.
                              //ERROR: setAddress() is a non-final, public method, so it can be overridden in a subclass.
                              //Java warns you: if a subclass overrides setAddress(),
                               //that overridden version could be called before the subclass constructor runs, which is unsafe.
                               // solution: make the setter final so it cannot be overridden.
    setPassword(password); 
    setEmail(email);       
}


    //Getters
    public String getId() {
        return id; 
    }
    public String getUsername() { 
        return username; 
    }
    public String getEmail() {
        return email; 
    }
    public LocalDateTime getRegisteredDate() {
        return registeredDate; 
    }
    public String getRole() {
        return role;
    }
    public boolean checkPassword(String input) {   //class Authration هنستخدمه بعدين في 
            return this.password.equals(input);
        }
    
    
    // these 3 setters are considered edit method
    public final void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty");
        this.username = username.trim();
        DataStore.getInstance().saveUsers();
    }

    public final void setPassword(String password) {
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Password too short");
        this.password = password;
        DataStore.getInstance().saveUsers();
    }

    public final void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");
        this.email = email.trim();
        DataStore.getInstance().saveUsers();
    }
    
    

    public boolean wasRegisteredBetween(LocalDateTime start, LocalDateTime end) {  //will be used in reports later
        return !registeredDate.isBefore(start) && !registeredDate.isAfter(end);
    }

    
    public void addUser() {
        if (dataStore == null) throw new IllegalStateException("DataStore not set");
        dataStore.getUsers().put(this.id, this);
        dataStore.saveUsers();
    }

    public void removeUser() {
        if (dataStore == null) throw new IllegalStateException("DataStore not set");
        dataStore.getUsers().remove(this.id);
        dataStore.saveUsers();
    }
    
    
    //   //////////////////Authration\\\\\\\\\\\\\\\\\\\\\\
    public static User login(String username, String password) {
    for (User user : DataStore.getInstance().getUsers().values()) {
        if (user.getUsername().equals(username) && user.checkPassword(password)) 
            return user; // successful login
        
    }
    throw new IllegalArgumentException("Invalid username or password");
}

public static boolean isAdmin(User user) {
    return user != null && user.getRole().equals("ADMIN");
}

public static boolean isSeller(User user) {
    return user != null && user.getRole().equals("SELLER");
}

public static boolean isCustomer(User user) {
    return user != null && user.getRole().equals("CUSTOMER");
}

    
    @Override
    public String toString() {
        return String.format("%s{id=%s, username=%s, email=%s, role=%s}",
                getClass().getSimpleName(), id, username, email, role);
    }
}