package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.Parcel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class TakeParcelController {
    @FXML
    private TextField description;
    @FXML
    private TextField type;
    @FXML
    private TextField weight;
    @FXML
    private TextField senderName;
    @FXML
    private TextField senderPhone;
    @FXML
    private TextField receiverName;
    @FXML
    private TextField receiverPhone;
    @FXML
    private TextField receiverAddress;
    @FXML
    private TextField price;
    @FXML
    private TextField deliveryCharge;
    @FXML
    private TextField deliveryDate;
    @FXML
    private Label total;
    @FXML
    private ListView<String> deliveryManList;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public MongoDatabase database = MongodbConnection.getDatabase("MainDB");

    public void initialize() {
        loadDeliveryMen();
    }

    public void goTakeParcelNextPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/takeParcelNext.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Parcel Registration Next Page");
        stage.show();

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
        String parcelDescription = description.getText();
        String parcelType = type.getText();
        double parcelWeight = parseDouble(weight.getText());
        String parcelSenderName = senderName.getText();
        String parcelSenderPhone = senderPhone.getText();
        String parcelReceiverName = receiverName.getText();
        String parcelReceiverPhone = receiverPhone.getText();
        String parcelReceiverAddress = receiverAddress.getText();
        int parcelPrice = parseInt(price.getText());
        int parcelDeliveryCharge = parseInt(deliveryCharge.getText());
        String parcelDeliveryDate = deliveryDate.getText();
        String selectedDeliveryMan = deliveryManList.getSelectionModel().getSelectedItem();


        Parcel parcel = new Parcel(parcelDescription, parcelType, parcelWeight, parcelSenderName, parcelSenderPhone,
                parcelReceiverName, parcelReceiverPhone, parcelReceiverAddress, selectedDeliveryMan, parcelPrice, parcelDeliveryCharge, parcelDeliveryDate);

        Document parcelDocument = parcel.toDocument();


        MongodbConnection.getCollection("MainDB", "Parcels").insertOne(parcelDocument);

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

