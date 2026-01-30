/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.users;

import ecommerce.data.DataStore;
import java.time.LocalDateTime;
/**
 *
 * @author asus
 */

public class Seller extends User {
    private static final long serialVersionUID = 1L;

    private  String storeName; //عادي مثلا  login ده لل username ده الاسم عادي   انما 

    public Seller(String id, String username, String password, String email, String storeName, LocalDateTime registeredDate) {
        super(id, username, password, email, "SELLER", registeredDate);
        setStoreName(storeName);
        
    }

    public final void setStoreName(String storeName){
    if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException("Store name cannot be empty");
        }
        this.storeName = storeName.trim();
        DataStore.getInstance().saveUsers(); // persist automatically
    }
    public String getStoreName() {
        return storeName;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", storeName=%s", storeName);
    }
}

