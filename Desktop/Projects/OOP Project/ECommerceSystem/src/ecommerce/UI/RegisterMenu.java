/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.UI;

/**
 *
 * @author asus
 */


import ecommerce.domain.users.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterMenu {

    public static void show(Stage stage) {

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        TextField email = new TextField();

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("ADMIN", "SELLER", "CUSTOMER");
        roleBox.setValue("CUSTOMER");

        Label message = new Label();

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back");

        registerBtn.setOnAction(e -> {
            try {
                String id = UUID.randomUUID().toString();
                String role = roleBox.getValue();

                User user;

                switch (role) {
                    case "ADMIN":
                        user = new Admin(
                                id,
                                username.getText(),
                                password.getText(),
                                email.getText(),
                                LocalDateTime.now()
                        );  break;
                    case "SELLER":
                        user = new Seller(
                                id,
                                username.getText(),
                                password.getText(),
                                email.getText(),
                                "Default Store",
                                LocalDateTime.now()
                        );  break;
                    default:
                        user = new Customer(
                                id,
                                username.getText(),
                                password.getText(),
                                email.getText(),
                                "Unknown Address",
                                "000000",
                                LocalDateTime.now()
                        );  break;
                }

                user.addUser();
                message.setText("Account created. You can login now.");

            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> LoginMenu.show(stage));

        VBox root = new VBox(10,
                new Label("Username"), username,
                new Label("Password"), password,
                new Label("Email"), email,
                new Label("Role"), roleBox,
                registerBtn,
                backBtn,
                message
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 420));
        stage.setTitle("Register");
        stage.show();
    }
}