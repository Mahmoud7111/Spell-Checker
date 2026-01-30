/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.users;
import ecommerce.data.DataStore;
import ecommerce.domain.Shopping.Order;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author asus
 */


public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private String address;
    private String phoneNumber;

    public Customer(String id, String username, String password, String email, String address, String phoneNumber, LocalDateTime registeredDate) {
        super(id, username, password, email, "CUSTOMER", registeredDate);
        setAddress(address);        //This ensures that every Customer object is always created with valid data.
        setPhoneNumber(phoneNumber);   //ERROR: setAddress() is a non-final, public method, so it can be overridden in a subclass.
                                       //Java warns you: if a subclass overrides setAddress(),
                                       //that overridden version could be called before the subclass constructor runs, which is unsafe.
    }                                   // solution: make the setter final so it cannot be overridden.

    public String getAddress() {
        return address;
    }

    public final void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address.trim();
        DataStore.getInstance().saveUsers();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        this.phoneNumber = phoneNumber.trim();
        DataStore.getInstance().saveUsers();
    }
    
    

    
    
    // /////////////////Report\\\\\\\\\\\\\\\\\

    

    @Override
    public String toString() {
        return super.toString() + String.format(", address=%s, phoneNumber=%s", address, phoneNumber);
    }
}

