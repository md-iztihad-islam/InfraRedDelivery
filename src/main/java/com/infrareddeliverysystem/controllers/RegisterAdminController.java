package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;


import java.io.IOException;

public class RegisterAdminController {
    public Label title_label;
    public AnchorPane top_bar;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField adminName;
    @FXML
    private TextField adminUsername;
    @FXML
    private TextField adminPassword;


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

    public void registerAdmin(ActionEvent event) throws IOException {
        if (adminName.getText().isEmpty() || adminUsername.getText().isEmpty() || adminPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();

            return;
        }
        String name = adminName.getText();
        String password = adminPassword.getText();
        String username = adminUsername.getText();


        Admin admin = new Admin(name, password, username);
        Document adminDoc = admin.toDocument();

        MongodbConnection.getCollection("MainDB", "Admin").insertOne(adminDoc);

        System.out.println("Successfully registered Admin");
    }
}
