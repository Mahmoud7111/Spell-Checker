package ecommerce.UI.dashboards;

import ecommerce.data.DataStore;
import ecommerce.domain.Shopping.*;
import ecommerce.domain.users.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class CustomerDashboard {

    private static Customer currentCustomer;
    private static Cart currentCart;

    private static ObservableList<Product> productList;
    private static ObservableList<Item> cartList;

    private static TableView<Product> productTable;
    private static TableView<Item> cartTable;

    public static void show(Stage stage, Customer customer) {
        currentCustomer = customer;

        // Load or create cart
        currentCart = DataStore.getInstance().getCarts()
                .getOrDefault(currentCustomer.getId(), new Cart(currentCustomer.getId()));
        Cart.setDataStore(DataStore.getInstance());

        productList = FXCollections.observableArrayList(DataStore.getInstance().getProducts().values());
        cartList = FXCollections.observableArrayList(currentCart.getItems());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Left: Product list and search
        VBox leftPane = new VBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search Products...");
        productTable = createProductTable();
        Button addButton = new Button("Add to Cart");
        addButton.setOnAction(e -> addToCart());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));
        leftPane.getChildren().addAll(searchField, productTable, addButton);

        // Right: Cart and actions
        VBox rightPane = new VBox(10);
        cartTable = createCartTable();
        HBox cartButtons = new HBox(10);
        Button removeBtn = new Button("Remove Item");
        Button payBtn = new Button("Pay");
        Button cancelBtn = new Button("Cancel Cart");
        removeBtn.setOnAction(e -> removeFromCart());
        payBtn.setOnAction(e -> payCart());
        cancelBtn.setOnAction(e -> cancelCart());
        cartButtons.getChildren().addAll(removeBtn, payBtn, cancelBtn);
        rightPane.getChildren().addAll(cartTable, cartButtons);

        // Bottom: Reports tabs
        TabPane tabPane = new TabPane();
        Tab ordersTab = new Tab("All Orders", createOrdersTable());
        tabPane.getTabs().addAll(ordersTab);

        root.setLeft(leftPane);
        root.setRight(rightPane);
        root.setBottom(tabPane);

        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("Customer Dashboard - " + currentCustomer.getUsername());
        stage.show();
    }

    private static TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>(productList);
        table.getColumns().addAll(
                new TableColumn<>("ID") {{ setCellValueFactory(new PropertyValueFactory<>("productId")); }},
                new TableColumn<>("Name") {{ setCellValueFactory(new PropertyValueFactory<>("name")); }},
                new TableColumn<>("Desc") {{ setCellValueFactory(new PropertyValueFactory<>("description")); }},
                new TableColumn<>("Price") {{ setCellValueFactory(new PropertyValueFactory<>("price")); }},
                new TableColumn<>("Stock") {{ setCellValueFactory(new PropertyValueFactory<>("stock")); }},
                new TableColumn<>("Seller") {{ setCellValueFactory(new PropertyValueFactory<>("sellerId")); }}
        );
        return table;
    }

    
    private static TableView<Item> createCartTable() {
    TableView<Item> table = new TableView<>(cartList);

    TableColumn<Item, String> productCol = new TableColumn<>("Product");
    productCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProductId()));

    TableColumn<Item, Integer> quantityCol = new TableColumn<>("Quantity");
    quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

    TableColumn<Item, String> totalCol = new TableColumn<>("Total");
    totalCol.setCellValueFactory(c -> 
        new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getTotal()))
    );

    table.getColumns().addAll(productCol, quantityCol, priceCol, totalCol);
    return table;
}
    

    private static void filterProducts(String keyword) {
        productList.setAll(DataStore.getInstance().getProducts().values().stream()
                .filter(p -> Product.searchProduct(p, keyword))
                .toList());
    }

    private static void addToCart() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        currentCart.addItem(selected.getProductId(), 1, selected.getPrice());
        cartList.setAll(currentCart.getItems());
    }

    private static void removeFromCart() {
        Item selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        currentCart.removeItem(selected.getProductId());
        cartList.setAll(currentCart.getItems());
    }

    private static void payCart() {
        if (currentCart.getItems().isEmpty()) return;
        Order order = new Order("ORD" + System.currentTimeMillis(), currentCustomer.getId(), 
                                FXCollections.observableArrayList(currentCart.getItems()));
        order.pay();
        Order.addOrder(order);
        currentCart.clear();
        cartList.setAll(currentCart.getItems());
        showAlert("Payment Successful", "Order has been paid successfully.");
    }

    private static void cancelCart() {
        currentCart.clear();
        cartList.setAll(currentCart.getItems());
    }

    private static TableView<Order> createOrdersTable() {
        TableView<Order> table = new TableView<>();
        ObservableList<Order> orders = FXCollections.observableArrayList(
                Order.getOrdersForCustomer(currentCustomer.getId())
        );
        table.setItems(orders);
        table.getColumns().addAll(
                new TableColumn<>("Order ID") {{ setCellValueFactory(new PropertyValueFactory<>("orderId")); }},
                new TableColumn<>("Date") {{ setCellValueFactory(new PropertyValueFactory<>("orderDate")); }},
                new TableColumn<>("Paid") {{ setCellValueFactory(new PropertyValueFactory<>("paid")); }},
                new TableColumn<>("Total") {{ setCellValueFactory(new PropertyValueFactory<>("totalAmount")); }}
        );
        return table;
    }



    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}