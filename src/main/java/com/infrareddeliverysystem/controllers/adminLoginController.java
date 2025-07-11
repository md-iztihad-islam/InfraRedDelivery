package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.Admin;
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

import java.io.IOException;

public class adminLoginController {
    public Label title_label;
    @FXML
    private TextField adminLoginUserName;
    @FXML
    private TextField adminLoginPassword;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Admin Login Page");
        stage.show();
    }

    public void adminLogin(ActionEvent event) throws IOException {
        String userName = adminLoginUserName.getText();
        String password = adminLoginPassword.getText();
        System.out.println("userName: " + userName);
        System.out.println("password: " + password);
        if (userName.isEmpty() && password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in both username and password.");
            alert.showAndWait();
        }else {
            MongoDatabase db = MongodbConnection.getDatabase("MainDB");
            MongoCollection < Document > collection = db.getCollection("Admin");

            Document query = new Document("username", userName);
            Document data = collection.find(query).first();
            System.out.println("username: " + data.get("username"));
            System.out.println("password: " + data.get("password"));

            if(data != null){
                String storedPasswordHash =  data.getString("password");

                if(Admin.checkPassword(password, storedPasswordHash)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Welcome, " + userName + "!");
                    alert.showAndWait();

                    switchToOffice(event);

                }

            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Error");
                alert.showAndWait();
            }
        }
    }
}
