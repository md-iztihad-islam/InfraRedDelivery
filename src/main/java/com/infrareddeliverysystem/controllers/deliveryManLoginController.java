package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.DeliveryMan;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;

public class deliveryManLoginController {
    @FXML
    private Label title_label;
    @FXML
    private TextField deliveryManUserName;
    @FXML
    private TextField deliveryManPassword;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = fxmlLoader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Delivery Man Page");
        stage.show();
    }

    public void deliveryManLogin(ActionEvent event) throws IOException {
        String userName = deliveryManUserName.getText();
        String password = deliveryManPassword.getText();

        if (userName.isEmpty() && password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in both username and password.");
            alert.showAndWait();
        } else {
            MongoDatabase db = MongodbConnection.getDatabase("MainDB");
            MongoCollection<Document> collection = db.getCollection("DeliveryMan");

            Document query = new Document("username", userName);
            Document data = collection.find(query).first();
            ObjectId deliveryManId = data.getObjectId("_id");


//            System.out.print("Querying for user: " + deliveryManId.toString());

            if (data != null) {
                String storedPasswordHash = data.getString("password");

                if (DeliveryMan.checkPassword(password, storedPasswordHash)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Welcome, " + userName + "!");
                    alert.showAndWait();

                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/deliveryList.fxml"));
                    root = fxmlLoader.load();

                    DeliveryListController deliveryListController = fxmlLoader.getController();
                    deliveryListController.setDeliveryManId(deliveryManId);


                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Delivery Man Page");
                    stage.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Error");
                alert.showAndWait();
            }
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