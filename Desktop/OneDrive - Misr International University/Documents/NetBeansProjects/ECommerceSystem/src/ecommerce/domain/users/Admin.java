/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.domain.users;
import java.time.LocalDateTime;
/**
 *
 * @author asus
 */

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(String id, String username, String password, String email, LocalDateTime registeredDate) {
        super(id, username, password, email, "ADMIN", registeredDate);
    }
}
