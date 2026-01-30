/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package ecommerce.UI;

import ecommerce.data.DataStore;
import ecommerce.domain.Shopping.Cart;
import ecommerce.domain.Shopping.Order;
import ecommerce.domain.Shopping.Product;
import ecommerce.domain.users.User;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 *
 * @author asus
 */




public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Initialize datastore ONCE
        DataStore ds = DataStore.getInstance();  // the only instance
                                                  //Give its reference to all domain classes
        User.setDataStore(ds);
        Product.setDataStore(ds);
        Cart.setDataStore(ds);
        Order.setDataStore(ds);
        
        LoginMenu.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
