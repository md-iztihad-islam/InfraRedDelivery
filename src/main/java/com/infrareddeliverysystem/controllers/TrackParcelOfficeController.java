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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.infrareddeliverysystem.models.Parcel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackParcelOfficeController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String parcelID;
    private Document parcelDetails;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetCircleStyling();
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
        parcelDetails = parcel.find(new Document("_id", id)).first();

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
        } else {
            paymentStatus.setText("Not Paid");
        }

        resetCircleStyling();

        boolean isDelivered = parcelDetails.getBoolean("isDelivered", false);
        if (isDelivered) {
            atOurWareHouse.getStyleClass().add("active-circle");
            deliveryManAssigned.getStyleClass().add("active-circle");
            onYourWay.getStyleClass().add("active-circle");
            delivered.getStyleClass().add("active-circle");
            progressBar.setProgress(1);
        }

        String status = parcelDetails.getString("status");
        if(status.equals("At our Warehouse")) {
            atOurWareHouse.getStyleClass().add("active-circle");
            progressBar.setProgress(0.25);
        } else if (status.equals("Delivery Man Assigned")) {
            atOurWareHouse.getStyleClass().add("active-circle");
            deliveryManAssigned.getStyleClass().add("active-circle");
            progressBar.setProgress(0.50);
        } else if (status.equals("On the way")) {
            atOurWareHouse.getStyleClass().add("active-circle");
            deliveryManAssigned.getStyleClass().add("active-circle");
            onYourWay.getStyleClass().add("active-circle");
            progressBar.setProgress(0.75);
        }
    }

    private void updateParcelStatus(String status) {
        MongoDatabase database = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> parcelCollection = database.getCollection("Parcels");

        Document updateDoc = new Document("$set", new Document("status", status));
        parcelCollection.updateOne(new Document("_id", new ObjectId(parcelID)), updateDoc);

        System.out.println("Parcel status updated to: " + status);
    }

    public void atOurWareHouse(){
        resetCircleStyling();
        atOurWareHouse.getStyleClass().add("active-circle");
        progressBar.setProgress(0.25);

        updateParcelStatus("At our Warehouse");
    }

    public void deliveryManAssigned(){
        resetCircleStyling();
        atOurWareHouse.getStyleClass().add("active-circle");
        deliveryManAssigned.getStyleClass().add("active-circle");
        progressBar.setProgress(0.50);

        updateParcelStatus("Delivery Man Assigned");
    }

    public void onYourWay(){
        resetCircleStyling();
        atOurWareHouse.getStyleClass().add("active-circle");
        deliveryManAssigned.getStyleClass().add("active-circle");
        onYourWay.getStyleClass().add("active-circle");
        progressBar.setProgress(0.75);

        updateParcelStatus("On the way");
    }

    public void delivered(){
        resetCircleStyling();
        atOurWareHouse.getStyleClass().add("active-circle");
        deliveryManAssigned.getStyleClass().add("active-circle");
        onYourWay.getStyleClass().add("active-circle");
        delivered.getStyleClass().add("active-circle");
        progressBar.setProgress(1);

        MongoDatabase database = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> parcelCollection = database.getCollection("Parcels");

        Document updateDoc = new Document("$set", new Document("isDelivered", true));

        parcelCollection.updateOne(new Document("_id", new ObjectId(parcelID)), updateDoc);

        updateParcelStatus("Delivered");
    }

    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = fxmlLoader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Office Page");
        stage.show();
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