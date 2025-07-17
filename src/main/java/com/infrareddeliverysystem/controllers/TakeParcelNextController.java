package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.DeliveryMan;
import com.infrareddeliverysystem.models.Parcel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class TakeParcelNextController {
    @FXML
    private TextField price;
    @FXML
    private TextField deliveryCharge;
    @FXML
    private DatePicker deliveryDate;
    @FXML
    private Label total;
    @FXML
    private ListView<String> deliveryManList;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public MongoDatabase database = MongodbConnection.getDatabase("MainDB");

    private Parcel parcel;

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public void initialize() {
        loadDeliveryMen();
    }


    private void loadDeliveryMen() {
        MongoCollection<Document> deliveryManCollection = database.getCollection("DeliveryMan");
        List<Document> deliveryMen = deliveryManCollection.find().into(new ArrayList<>());
        ObservableList<String> deliveryManNames = FXCollections.observableArrayList();

        for (Document doc : deliveryMen) {
            String name = doc.getString("name");
            String id = doc.getObjectId("_id").toString();
            deliveryManNames.add(id);
        }
        deliveryManList.setItems(deliveryManNames);
    }

    public void onRegister(ActionEvent event) throws IOException {
        int parcelPrice = parseInt(price.getText());
        int parcelDeliveryCharge = parseInt(deliveryCharge.getText());
        LocalDate dateValue = deliveryDate.getValue();
        String parcelDeliveryDate = dateValue != null ? dateValue.toString() : "";
        String selectedDeliveryMan = deliveryManList.getSelectionModel().getSelectedItem();
        
        int parcelTotalCharge = parcelPrice +  parcelDeliveryCharge;
        total.setText(Integer.toString(parcelTotalCharge));
        
        parcel.setPrice(parcelPrice);
        parcel.setDeliveryCharge(parcelDeliveryCharge);
        parcel.setEstimatedDeliveryDate(parcelDeliveryDate);
        parcel.setDeliveryManId(selectedDeliveryMan);
        parcel.setTotalCharge(parcelTotalCharge);

        Document parcelDocument = parcel.toDocument();

        MongoCollection<Document> parcels = database.getCollection("Parcels");
        InsertOneResult res = parcels.insertOne(parcelDocument);
        ObjectId parcelId = Objects.requireNonNull(res.getInsertedId()).asObjectId().getValue();


        MongoCollection<Document> men = database.getCollection("DeliveryMan");
        ObjectId dmId = new ObjectId(selectedDeliveryMan);
        men.updateOne(
                Filters.eq("_id", dmId),
                Updates.combine(
                        Updates.inc("pendingDeliveries", 1),          // ++pendingDeliveries
                        Updates.push("deliveries", parcelId.toHexString()) // add parcelId to deliveries[]
                )
        );

        Document dmDoc = men.find(Filters.eq("_id", dmId)).first();
        String deliveryManName;
        if (dmDoc != null && dmDoc.getString("name") != null) {
            deliveryManName = dmDoc.getString("name");
        } else {
            deliveryManName = "Unknown";
        }

        try {
            String message = String.format(
                    "Your parcel %s has been assigned to %s. ETA: %s.",
                    parcelId.toHexString(),
                    deliveryManName,
                    parcel.getEstimatedDeliveryDate()
            );

            // Prepare the SMS API request
            String apiKey = "1B2DvxXzsprXTkUQEsEr"; // Replace with your actual API key
            String senderId = "8809617611758"; // Replace with your sender ID
            String phoneNumber = parcel.getReceiverPhone(); // Make sure this field exists in Parcel class

            // Create form parameters
            String formData = String.format(
                    "api_key=%s&type=text&number=%s&senderid=%s&message=%s",
                    URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                    URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8),
                    URLEncoder.encode(senderId, StandardCharsets.UTF_8),
                    URLEncoder.encode(message, StandardCharsets.UTF_8)
            );

            // Create and send HTTP request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://bulksmsbd.net/api/smsapi"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response
            System.out.println("SMS API Response: " + response.body());

            if (response.statusCode() != 200) {
                System.err.println("SMS sending failed with status: " + response.statusCode());
                // You might want to show a warning to the user here
            }

        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            // Optionally show a warning to the user
            Alert smsAlert = new Alert(Alert.AlertType.WARNING);
            smsAlert.setTitle("SMS Notification Failed");
            smsAlert.setHeaderText("Parcel registered successfully");
            smsAlert.setContentText("However, the SMS notification could not be sent.");
            smsAlert.showAndWait();
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Success");
        alert.setContentText("Success");
        alert.showAndWait();
    }

    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = fxmlLoader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Admin Login Page");
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

