/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.UI.dashboards;

import ecommerce.domain.users.Admin;
import ecommerce.UI.LoginMenu;
import ecommerce.data.DataStore;
import ecommerce.domain.Shopping.Order;
import ecommerce.domain.Shopping.Product;
import ecommerce.domain.Shopping.Supplier;
import ecommerce.domain.users.Customer;
import ecommerce.domain.users.Seller;
import ecommerce.domain.users.User;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard {

    public static void show(Stage stage, Admin admin) {

        Button manageProducts = new Button("Manage Products");
        Button productReports = new Button("Product Reports");

        Button manageUsers = new Button("Manage Users");
        Button userReports = new Button("User Reports");

        Button orderReports = new Button("Order Reports");
        Button logout = new Button("Logout");

        manageProducts.setOnAction(e ->
                AdminProductManagement.show(stage)
        );

        productReports.setOnAction(e ->
                AdminProductReports.show(stage)
        );

        manageUsers.setOnAction(e ->
                AdminUserManagement.show(stage)
        );

        userReports.setOnAction(e ->
                AdminUserReports.show(stage)
        );

        orderReports.setOnAction(e ->
                AdminOrderReports.show(stage)
        );

        logout.setOnAction(e ->
                LoginMenu.show(stage)
        );

        VBox root = new VBox(
                15,
                manageProducts,
                productReports,
                manageUsers,
                userReports,
                orderReports,
                logout
        );

        stage.setScene(new Scene(root, 400, 450));
        stage.setTitle("Admin Dashboard");
    }
    
    
    //---------ProductManagement ----------
    public class AdminProductManagement {

    private static final ObservableList<Product> productList =
            FXCollections.observableArrayList();

    public static void show(Stage stage) {

        // ------------------ UI Components ------------------
        TextField searchField = new TextField();
        searchField.setPromptText("Search by any field...");

        ListView<Product> listView = new ListView<>(productList);

        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Back");

        // ------------------ Load Products ------------------
        refreshProducts();

        // ------------------ Search ------------------
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            productList.clear();
            for (Product p : DataStore.getInstance().getProducts().values()) {
                if (Product.searchProduct(p, newVal)) {
                    productList.add(p);
                }
            }
        });

        // ------------------ Add Product ------------------
        addBtn.setOnAction(e -> showProductForm(stage, null));

        // ------------------ Edit Product ------------------
        editBtn.setOnAction(e -> {
            Product selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showProductForm(stage, selected);
            }
        });

        // ------------------ Delete Product ------------------
        deleteBtn.setOnAction(e -> {
            Product selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.removeProduct();
                refreshProducts();
            }
        });

        backBtn.setOnAction(e ->
                AdminDashboard.show(stage, null)
        );

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn, backBtn);
        VBox root = new VBox(10, searchField, listView, buttons);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 600, 450));
        stage.setTitle("Admin - Manage Products");
        stage.show();
    }

    // ------------------ Helpers ------------------

    private static void refreshProducts() {
        productList.clear();
        productList.addAll(DataStore.getInstance().getProducts().values());
    }

    private static void showProductForm(Stage stage, Product product) {

        boolean isEdit = product != null;

        TextField name = new TextField(isEdit ? product.getName() : "");
        TextField desc = new TextField(isEdit ? product.getDescription() : "");
        TextField price = new TextField(isEdit ? String.valueOf(product.getPrice()) : "");
        TextField stock = new TextField(isEdit ? String.valueOf(product.getStock()) : "");
        TextField sellerId = new TextField(isEdit ? product.getSellerId() : "");

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        save.setOnAction(e -> {
            try {
                if (isEdit) {
                    product.setName(name.getText());
                    product.setDescription(desc.getText());
                    product.setPrice(Double.parseDouble(price.getText()));
                    product.setStock(Integer.parseInt(stock.getText()));
                    product.setSellerId(sellerId.getText());
                } else {
                    Product p = new Product(
                            UUID.randomUUID().toString(),
                            name.getText(),
                            desc.getText(),
                            Double.parseDouble(price.getText()),
                            Integer.parseInt(stock.getText()),
                            sellerId.getText()
                    );
                    p.addProduct();
                }
                refreshProducts();
                show(stage);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        cancel.setOnAction(e -> show(stage));

        VBox form = new VBox(10,
                new Label("Name"), name,
                new Label("Description"), desc,
                new Label("Price"), price,
                new Label("Stock"), stock,
                new Label("Seller ID"), sellerId,
                save, cancel
        );
        form.setPadding(new Insets(15));

        stage.setScene(new Scene(form, 400, 500));
    }
}
    
    
    //---------ProductReports--------
    public class AdminProductReports {

    public static void show(Stage stage) {

        TextField productIdField = new TextField();
        productIdField.setPromptText("Product ID");

        DatePicker fromDate = new DatePicker();
        DatePicker toDate = new DatePicker();

        Label result = new Label();

        Button piecesSoldBtn = new Button("Pieces Sold");
        Button bestSellerBtn = new Button("Best Seller Product");
        Button mostRevenueBtn = new Button("Most Revenue Product");
        Button suppliersBtn = new Button("List Suppliers & Pricing");
        Button backBtn = new Button("Back");

        piecesSoldBtn.setOnAction(e -> {
            int sold = Product.getPiecesSold(
                    productIdField.getText(),
                    fromDate.getValue().atStartOfDay(),
                    toDate.getValue().atStartOfDay()
            );
            result.setText("Pieces sold: " + sold);
        });

        bestSellerBtn.setOnAction(e -> {
            Product p = Product.getBestSellerProduct(
                    fromDate.getValue().atStartOfDay(),
                    toDate.getValue().atStartOfDay()
            );
            result.setText(p == null ? "No data" : p.toString());
        });

        mostRevenueBtn.setOnAction(e -> {
            Product p = Product.getMostRevenueProduct(
                    fromDate.getValue().atStartOfDay(),
                    toDate.getValue().atStartOfDay()
            );
            result.setText(p == null ? "No data" : p.toString());
        });

        suppliersBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Supplier s : Supplier.listSuppliersAndPricing()) {
                sb.append(s).append("\n");
            }
            result.setText(sb.toString());
        });

        backBtn.setOnAction(e -> AdminDashboard.show(stage, null));

        VBox root = new VBox(10,
                new Label("From"), fromDate,
                new Label("To"), toDate,
                productIdField,
                piecesSoldBtn,
                bestSellerBtn,
                mostRevenueBtn,
                suppliersBtn,
                result,
                backBtn
        );
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 500, 600));
        stage.setTitle("Admin - Product Reports");
        stage.show();
    }
}
    
    //----------User Management--------
    public class AdminUserManagement {

    private static final ObservableList<User> users =
            FXCollections.observableArrayList();

    public static void show(Stage stage) {

        TextField search = new TextField();
        search.setPromptText("Search users");

        ListView<User> list = new ListView<>(users);

        Button add = new Button("Add");
        Button edit = new Button("Edit");
        Button remove = new Button("Remove");
        Button back = new Button("Back");

        refresh();

        search.textProperty().addListener((o, a, b) ->
                users.setAll(DataStore.getInstance().searchUsers(b))
        );

        add.setOnAction(e -> createUser(stage));

        edit.setOnAction(e -> {
            User u = list.getSelectionModel().getSelectedItem();
            if (u != null) createUser(stage, u);
        });

        remove.setOnAction(e -> {
            User u = list.getSelectionModel().getSelectedItem();
            if (u != null) {
                u.removeUser();
                refresh();
            }
        });

        back.setOnAction(e -> AdminDashboard.show(stage, null));

        VBox root = new VBox(10, search, list, add, edit, remove, back);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 450, 500));
        stage.setTitle("Admin - Manage Users");
        stage.show();
    }

    private static void refresh() {
        users.setAll(DataStore.getInstance().getUsers().values());
    }

    private static void createUser(Stage stage) {
        createUser(stage, null);
    }

    private static void createUser(Stage stage, User user) {

        TextField username = new TextField(user != null ? user.getUsername() : "");
        TextField email = new TextField(user != null ? user.getEmail() : "");
        PasswordField password = new PasswordField();
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("ADMIN", "SELLER", "CUSTOMER");

        Button save = new Button("Save");

        save.setOnAction(e -> {
            if (user == null) {
                User u = switch (role.getValue()) {
                    case "ADMIN" -> new Admin(UUID.randomUUID().toString(), username.getText(), password.getText(), email.getText(), LocalDateTime.now());
                    case "SELLER" -> new Seller(UUID.randomUUID().toString(), username.getText(), password.getText(), email.getText(), "Store", LocalDateTime.now());
                    default -> new Customer(UUID.randomUUID().toString(), username.getText(), password.getText(), email.getText(), "Address", "000", LocalDateTime.now());
                };
                u.addUser();
            } else {
                user.setUsername(username.getText());
                user.setEmail(email.getText());
            }
            show(stage);
        });

        VBox root = new VBox(10, username, email, password, role, save);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 400, 400));
    }
}
    
    
    //-----------User Reports---------------
    public class AdminUserReports {

    public static void show(Stage stage) {

        DatePicker from = new DatePicker();
        DatePicker to = new DatePicker();

        Label result = new Label();

        Button supOrders = new Button("Supplier Max Orders");
        Button supRevenue = new Button("Supplier Max Revenue");
        Button custOrders = new Button("Customer Max Orders");
        Button custRevenue = new Button("Customer Max Revenue");
        Button revenue = new Button("Avg / Total Revenue");
        Button back = new Button("Back");

        supOrders.setOnAction(e ->
                result.setText(String.valueOf(Supplier.getSupplierWithMaxOrders()))
        );

        supRevenue.setOnAction(e ->
                result.setText(String.valueOf(Supplier.getSupplierWithMaxRevenue()))
        );

        custOrders.setOnAction(e ->
                result.setText(String.valueOf(Order.customerWithMaxOrders()))
        );

        custRevenue.setOnAction(e ->
                result.setText(String.valueOf(Order.customerWithMaxRevenue()))
        );

        revenue.setOnAction(e -> {
            double total = Order.totalRevenue(from.getValue().atStartOfDay(), to.getValue().atStartOfDay());
            double avg = Order.averageRevenue(from.getValue().atStartOfDay(), to.getValue().atStartOfDay());
            result.setText("Total: " + total + " | Avg: " + avg);
        });

        back.setOnAction(e -> AdminDashboard.show(stage, null));

        VBox root = new VBox(10,
                from, to,
                supOrders, supRevenue,
                custOrders, custRevenue,
                revenue,
                result,
                back
        );
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 500, 550));
        stage.setTitle("Admin - User Reports");
        stage.show();
    }
}
    
    //--------oreder report--------
    public class AdminOrderReports {

    public static void show(Stage stage) {

        // ----------- Inputs -----------
        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Order ID");

        DatePicker fromDate = new DatePicker();
        DatePicker toDate = new DatePicker();

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        // ----------- Buttons -----------
        Button viewOrderBtn = new Button("View Order Details");
        Button totalRevenueBtn = new Button("Total Revenue");
        Button avgRevenueBtn = new Button("Average Revenue");
        Button viewAllOrdersBtn = new Button("View All Orders");
        Button backBtn = new Button("Back");

        // ----------- Actions -----------

        viewOrderBtn.setOnAction(e -> {
            Order order = Order.getOrderById(orderIdField.getText());
            if (order == null) {
                resultArea.setText("Order not found.");
            } else {
                resultArea.setText(order.toString());
            }
        });

        totalRevenueBtn.setOnAction(e -> {
            LocalDateTime from = fromDate.getValue().atStartOfDay();
            LocalDateTime to = toDate.getValue().atStartOfDay();
            double total = Order.totalRevenue(from, to);
            resultArea.setText("Total Revenue: " + total);
        });

        avgRevenueBtn.setOnAction(e -> {
            LocalDateTime from = fromDate.getValue().atStartOfDay();
            LocalDateTime to = toDate.getValue().atStartOfDay();
            double avg = Order.averageRevenue(from, to);
            resultArea.setText("Average Revenue: " + avg);
        });

        viewAllOrdersBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Order order : DataStore.getInstance().getOrders().values()) {
                sb.append(order).append("\n\n");
            }
            resultArea.setText(sb.isEmpty() ? "No orders found." : sb.toString());
        });

        backBtn.setOnAction(e -> AdminDashboard.show(stage, null));

        // ----------- Layout -----------
        VBox root = new VBox(10,
                new Label("Order ID"),
                orderIdField,
                viewOrderBtn,
                new Separator(),
                new Label("From"),
                fromDate,
                new Label("To"),
                toDate,
                totalRevenueBtn,
                avgRevenueBtn,
                new Separator(),
                viewAllOrdersBtn,
                resultArea,
                backBtn
        );

        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 550, 650));
        stage.setTitle("Admin - Order Reports");
        stage.show();
    }
}
    
    
}