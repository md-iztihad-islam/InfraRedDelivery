package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;

public class TrackParcelCustomerController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String parcelID;

    @FXML
    private Label parDes;
    @FXML
    private Label dmName;
    @FXML
    private Label total;
    @FXML
    private Label paymentStatus;
    @FXML
    private Label deliveryDate;
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
        dmName.setText(deliveryManDetails.getString("name"));
        Object totalChargeObj = parcelDetails.get("totalCharge");
        if (totalChargeObj instanceof Integer) {
            total.setText(String.valueOf((Integer) totalChargeObj));  // Convert Integer to String
        } else if (totalChargeObj instanceof Double) {
            total.setText(String.valueOf((Double) totalChargeObj));  // Convert Double to String
        }
        deliveryDate.setText(parcelDetails.getString("estimatedDeliveryDate"));

        boolean isPaid = parcelDetails.getBoolean("isPaid", false);
        if (isPaid) {
            paymentStatus.setText("Paid");
        } else {
            paymentStatus.setText("Not Paid");
        }

        boolean isDelivered = parcelDetails.getBoolean("isDelivered", false);

        if (isDelivered) {
            atOurWareHouse.setFill(javafx.scene.paint.Color.GREEN);
            deliveryManAssigned.setFill(javafx.scene.paint.Color.GREEN);
            onYourWay.setFill(javafx.scene.paint.Color.GREEN);
            delivered.setFill(javafx.scene.paint.Color.GREEN);
            progressBar.setProgress(1);
        }

        String status = parcelDetails.getString("status");

        if(status.equals("At our Warehouse")) {
            atOurWareHouse.setFill(javafx.scene.paint.Color.GREEN);
            progressBar.setProgress(0.25);
        } else if (status.equals("Delivery Man Assigned")) {
            atOurWareHouse.setFill(javafx.scene.paint.Color.GREEN);
            deliveryManAssigned.setFill(javafx.scene.paint.Color.GREEN);
            progressBar.setProgress(0.50);
        } else if (status.equals("On the way")) {
            atOurWareHouse.setFill(javafx.scene.paint.Color.GREEN);
            deliveryManAssigned.setFill(javafx.scene.paint.Color.GREEN);
            onYourWay.setFill(javafx.scene.paint.Color.GREEN);
            progressBar.setProgress(0.75);
        } else if (status.equals("Delivered")) {
            atOurWareHouse.setFill(javafx.scene.paint.Color.GREEN);
            deliveryManAssigned.setFill(javafx.scene.paint.Color.GREEN);
            onYourWay.setFill(javafx.scene.paint.Color.GREEN);
            delivered.setFill(javafx.scene.paint.Color.GREEN);
            progressBar.setProgress(1);
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
