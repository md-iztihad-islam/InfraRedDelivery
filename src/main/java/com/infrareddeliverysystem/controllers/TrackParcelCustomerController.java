package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TrackParcelCustomerController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String parcelID;

    @FXML
    private Label parDes;
    @FXML
    private Label dmNameDelivery;
    @FXML
    private Label total;
    @FXML
    private Label paymentStatus;
    @FXML
    private Label deliveryDateParcel;
    @FXML
    private Circle atOurWareHouse;
    @FXML
    private Circle deliveryManAssigned;
    @FXML
    private Circle onYourWay;
    @FXML
    private Circle delivered;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button chat;
    @FXML
    private Button pay;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetCircleStyling();

        if (chat != null) {
            chat.setOnAction(event -> handleChatRequest());
        }

        if (pay != null) {
            pay.setOnAction(event -> handlePayment());
        }
    }

    private void handleChatRequest() {
        System.out.println("Chat requested for parcel: " + parcelID);
        // Implement chat functionality here
    }

    @FXML
    private void handlePayment() {
        System.out.println("Payment requested for parcel: " + parcelID);

        if (parcelID != null) {
            try {
                MongoDatabase database = MongodbConnection.getDatabase("MainDB");
                MongoCollection<Document> parcels = database.getCollection("Parcels");
                parcels.updateOne(
                        new Document("_id", new ObjectId(parcelID)),
                        new Document("$set", new Document("isPaid", true))
                );

                String paymentUrl = "http://localhost:5173/" + parcelID;
                openPaymentGateway(paymentUrl);

                paymentStatus.setText("Paid");
                if (pay != null) {
                    pay.setDisable(true);
                }
            } catch (Exception e) {
                System.err.println("Payment update failed: " + e.getMessage());
            }
        }
    }

    private void openPaymentGateway(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            } else {
                System.out.println("Desktop is not supported. Cannot open the browser.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetCircleStyling() {
        atOurWareHouse.getStyleClass().removeAll("active-circle");
        deliveryManAssigned.getStyleClass().removeAll("active-circle");
        onYourWay.getStyleClass().removeAll("active-circle");
        delivered.getStyleClass().removeAll("active-circle");
    }

    @FXML
    public void setParcelID(String parcelID) {
        this.parcelID = parcelID;
        System.out.println("Parcel ID set: " + this.parcelID);
        fetchParcelDetails();
    }

    public void fetchParcelDetails() {
        ObjectId id = new ObjectId(parcelID);
        MongoDatabase database = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> parcel = database.getCollection("Parcels");
        Document parcelDetails = parcel.find(new Document("_id", id)).first();

        if (parcelDetails == null) {
            System.out.println("Parcel not found!");
            return;
        }

        System.out.println("Fetched Parcel Details: " + parcelDetails.toJson());

        String deliveryManIdStr = parcelDetails.getString("deliveryManId");
        ObjectId deliveryManId = new ObjectId(deliveryManIdStr);

        MongoCollection<Document> deliveryManCollection = database.getCollection("DeliveryMan");
        Document deliveryManDetails = deliveryManCollection.find(new Document("_id", deliveryManId)).first();

        if (deliveryManDetails == null) {
            System.out.println("DeliveryMan not found!");
            return;
        }

        parDes.setText(parcelDetails.getString("parcelDescription"));
        dmNameDelivery.setText(deliveryManDetails.getString("name"));

        Object totalChargeObj = parcelDetails.get("totalCharge");
        if (totalChargeObj instanceof Integer) {
            total.setText(String.valueOf((Integer) totalChargeObj));
        } else if (totalChargeObj instanceof Double) {
            total.setText(String.valueOf((Double) totalChargeObj));
        }

        deliveryDateParcel.setText(parcelDetails.getString("estimatedDeliveryDate"));

        boolean isPaid = parcelDetails.getBoolean("isPaid", false);
        if (isPaid) {
            paymentStatus.setText("Paid");
            if (pay != null) pay.setDisable(true);
        } else {
            paymentStatus.setText("Not Paid");
            if (pay != null) pay.setDisable(false);
        }


        resetCircleStyling();

        boolean isDelivered = parcelDetails.getBoolean("isDelivered", false);
        if (isDelivered) {
            atOurWareHouse.getStyleClass().add("active-circle");
            deliveryManAssigned.getStyleClass().add("active-circle");
            onYourWay.getStyleClass().add("active-circle");
            delivered.getStyleClass().add("active-circle");
            progressBar.setProgress(1);
        } else {
            String status = parcelDetails.getString("status");
            updateTrackingStatus(status);
        }
    }

    // Update tracking using CSS classes instead of direct color manipulation
    private void updateTrackingStatus(String status) {
        if (status == null) return;

        switch (status) {
            case "At our Warehouse":
                atOurWareHouse.getStyleClass().add("active-circle");
                progressBar.setProgress(0.25);
                break;

            case "Delivery Man Assigned":
                atOurWareHouse.getStyleClass().add("active-circle");
                deliveryManAssigned.getStyleClass().add("active-circle");
                progressBar.setProgress(0.50);
                break;

            case "On the way":
                atOurWareHouse.getStyleClass().add("active-circle");
                deliveryManAssigned.getStyleClass().add("active-circle");
                onYourWay.getStyleClass().add("active-circle");
                progressBar.setProgress(0.75);
                break;
        }
    }

    @FXML
    public void onHomeButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/home.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Home Page");
        stage.show();
    }

    public void onAdminButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/adminLogin.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Admin Login Page");
        stage.show();
    }

    public void onStaffButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/deliveryManLogin.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Delivery Man Login Page");
        stage.show();
    }
}