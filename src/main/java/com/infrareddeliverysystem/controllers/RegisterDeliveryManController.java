package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.DeliveryMan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;

public class RegisterDeliveryManController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField dmName;
    @FXML
    private TextField dmUsername;
    @FXML
    private TextField dmPassword;
    @FXML
    private TextField dmEmail;
    @FXML
    private TextField dmPhone;
    @FXML
    private TextField dmDrivingLicense;
    @FXML
    private TextField dmCarNumber;
    @FXML
    private TextField dmSalary;

    public void deliveryManRegistration(ActionEvent event){
        String name = dmName.getText();
        String username = dmUsername.getText();
        String password = dmPassword.getText();
        String email = dmEmail.getText();
        String phone = dmPhone.getText();
        String drivingLicenseNo = dmDrivingLicense.getText();
        String carNumber = dmCarNumber.getText();
        String salary = dmSalary.getText();

        DeliveryMan deliveryMan = new DeliveryMan(name, username, password, email, phone, drivingLicenseNo, carNumber, salary);
        Document deliveryManDoc = deliveryMan.toDocument();

        MongodbConnection.getCollection("MainDB", "DeliveryMan").insertOne(deliveryManDoc);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Success");
        alert.setHeaderText("Registration Success");
        alert.showAndWait();
    }

    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Office Page");
        stage.show();
    }
}
