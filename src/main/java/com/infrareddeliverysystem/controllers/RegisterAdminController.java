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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;


import java.io.IOException;

public class RegisterAdminController {
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
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Office Page");
        stage.show();
    }

    public void registerAdmin(ActionEvent event) throws IOException {
        String name = adminName.getText();
        String password = adminPassword.getText();
        String username = adminUsername.getText();

        System.out.println("Name: " + name);
        System.out.println("Password: " + password);
        System.out.println("Username: " + username);

        Admin admin = new Admin(name, password, username);
        Document adminDoc = admin.toDocument();

        MongodbConnection.getCollection("MainDB", "Admin").insertOne(adminDoc);

        System.out.println("Successfully registered Admin");
    }
}
