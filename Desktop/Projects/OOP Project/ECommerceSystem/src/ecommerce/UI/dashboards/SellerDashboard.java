package ecommerce.UI.dashboards;

import ecommerce.data.DataStore;
import ecommerce.domain.Shopping.Order;
import ecommerce.domain.Shopping.Product;
import ecommerce.domain.users.Seller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SellerDashboard {

    public static void show(Stage stage, Seller seller) {

        Label title = new Label("Seller Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button manageProductsBtn = new Button("Manage My Products");
        Button productReportsBtn = new Button("Product Reports");
        Button orderReportsBtn = new Button("Order Reports");
        Button logoutBtn = new Button("Logout");

        manageProductsBtn.setOnAction(e ->
                SellerProductManagement.show(stage, seller));

        productReportsBtn.setOnAction(e ->
                SellerProductReports.show(stage, seller));

        orderReportsBtn.setOnAction(e ->
                SellerOrderReports.show(stage, seller));

        logoutBtn.setOnAction(e ->
                ecommerce.UI.LoginMenu.show(stage));

        VBox root = new VBox(15,
                title,
                manageProductsBtn,
                productReportsBtn,
                orderReportsBtn,
                logoutBtn
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Seller Dashboard");
        stage.show();
    }



//----------SellerProductManagement----------
public class SellerProductManagement {

    public static void show(Stage stage, Seller seller) {

        TextField id = new TextField();
        TextField name = new TextField();
        TextField desc = new TextField();
        TextField price = new TextField();
        TextField stock = new TextField();
        TextField search = new TextField();

        TextArea output = new TextArea();
        output.setEditable(false);

        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button removeBtn = new Button("Remove");
        Button listBtn = new Button("List My Products");
        Button searchBtn = new Button("Search");
        Button backBtn = new Button("Back");

        addBtn.setOnAction(e -> {
            Product p = new Product(
                    id.getText(),
                    name.getText(),
                    desc.getText(),
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    seller.getId()
            );
            p.addProduct();
            output.setText("Product added.");
        });

        editBtn.setOnAction(e -> {
            Product p = DataStore.getInstance().getProducts().get(id.getText());
            if (p != null && p.getSellerId().equals(seller.getId())) {
                p.setName(name.getText());
                p.setDescription(desc.getText());
                p.setPrice(Double.parseDouble(price.getText()));
                p.setStock(Integer.parseInt(stock.getText()));
                output.setText("Product updated.");
            }
        });

        removeBtn.setOnAction(e -> {
            Product p = DataStore.getInstance().getProducts().get(id.getText());
            if (p != null && p.getSellerId().equals(seller.getId())) {
                p.removeProduct();
                output.setText("Product removed.");
            }
        });

        listBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            DataStore.getInstance().getProducts().values().forEach(p -> {
                if (p.getSellerId().equals(seller.getId()))
                    sb.append(p).append("\n");
            });
            output.setText(sb.toString());
        });

        searchBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            DataStore.getInstance().getProducts().values().forEach(p -> {
                if (p.getSellerId().equals(seller.getId())
                        && Product.searchProduct(p, search.getText()))
                    sb.append(p).append("\n");
            });
            output.setText(sb.toString());
        });

        backBtn.setOnAction(e ->
                SellerDashboard.show(stage, seller));

        VBox root = new VBox(10,
                id, name, desc, price, stock,
                addBtn, editBtn, removeBtn,
                search, searchBtn, listBtn,
                output, backBtn
        );

        root.setPadding(new Insets(15));
        stage.setScene(new Scene(root, 600, 600));
        stage.setTitle("Manage Products");
        stage.show();
    }
}










//--------------SellerProductReports---------
public class SellerProductReports {

    public static void show(Stage stage, Seller seller) {

        TextField productId = new TextField();
        DatePicker from = new DatePicker();
        DatePicker to = new DatePicker();
        TextArea result = new TextArea();

        Button piecesBtn = new Button("Pieces Sold");
        Button bestBtn = new Button("Best Seller");
        Button revenueBtn = new Button("Most Revenue Product");
        Button backBtn = new Button("Back");

        piecesBtn.setOnAction(e -> {
            int sold = Product.getPiecesSold(
                    productId.getText(),
                    from.getValue().atStartOfDay(),
                    to.getValue().atStartOfDay()
            );
            result.setText("Pieces Sold: " + sold);
        });

        bestBtn.setOnAction(e ->
                result.setText(
                        String.valueOf(
                                Product.getBestSellerProduct(
                                        from.getValue().atStartOfDay(),
                                        to.getValue().atStartOfDay()
                                )
                        )
                ));

        revenueBtn.setOnAction(e ->
                result.setText(
                        String.valueOf(
                                Product.getMostRevenueProduct(
                                        from.getValue().atStartOfDay(),
                                        to.getValue().atStartOfDay()
                                )
                        )
                ));

        backBtn.setOnAction(e ->
                SellerDashboard.show(stage, seller));

        VBox root = new VBox(10,
                productId, from, to,
                piecesBtn, bestBtn, revenueBtn,
                result, backBtn
        );

        root.setPadding(new Insets(15));
        stage.setScene(new Scene(root, 500, 500));
        stage.setTitle("Product Reports");
        stage.show();
    }
}





    
    
 //-------- ---SellerOrderReports----------
    public class SellerOrderReports {

    public static void show(Stage stage, Seller seller) {

        DatePicker from = new DatePicker();
        DatePicker to = new DatePicker();
        TextArea result = new TextArea();

        Button totalBtn = new Button("Total Revenue");
        Button avgBtn = new Button("Average Revenue");
        Button backBtn = new Button("Back");

        totalBtn.setOnAction(e -> {
            double total = Order.totalRevenue(
                    from.getValue().atStartOfDay(),
                    to.getValue().atStartOfDay()
            );
            result.setText("Total Revenue: " + total);
        });

        avgBtn.setOnAction(e -> {
            double avg = Order.averageRevenue(
                    from.getValue().atStartOfDay(),
                    to.getValue().atStartOfDay()
            );
            result.setText("Average Revenue: " + avg);
        });

        backBtn.setOnAction(e ->
                SellerDashboard.show(stage, seller));

        VBox root = new VBox(10,
                from, to,
                totalBtn, avgBtn,
                result, backBtn
        );

        root.setPadding(new Insets(15));
        stage.setScene(new Scene(root, 450, 400));
        stage.setTitle("Order Reports");
        stage.show();
    }
}
    
    
    
    



}