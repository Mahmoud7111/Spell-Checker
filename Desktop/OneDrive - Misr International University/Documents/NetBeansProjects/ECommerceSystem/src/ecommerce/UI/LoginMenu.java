/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.UI;
import ecommerce.UI.dashboards.AdminDashboard;
import ecommerce.UI.dashboards.SellerDashboard;
import ecommerce.UI.dashboards.CustomerDashboard;
import ecommerce.domain.users.*;



import ecommerce.domain.users.User;
import java.awt.Image;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginMenu {

    public static void show(Stage stage) {
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        Label message = new Label();

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Create Account");

        loginBtn.setOnAction(e -> {
            try {
                User user = User.login(
                        usernameField.getText(),
                        passwordField.getText()
                );

                if (User.isAdmin(user)) {
                    ecommerce.UI.dashboards.AdminDashboard.show(stage, (Admin)user);
                }
                else if (User.isSeller(user)) {
                    ecommerce.UI.dashboards.SellerDashboard.show(stage,(Seller)user);
                }
                else if (User.isCustomer(user)) {
                    ecommerce.UI.dashboards.CustomerDashboard.show(stage, (Customer)user);
                }
                

            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> RegisterMenu.show(stage));

        VBox root = new VBox(10,
                new Label("Username"),
                usernameField,
                new Label("Password"),
                passwordField,
                loginBtn,
                registerBtn,
                message
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 300));
        stage.setTitle("Login");
        stage.show();
    }
    

    
}