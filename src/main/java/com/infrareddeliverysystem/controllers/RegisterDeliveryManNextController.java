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

public class RegisterDeliveryManNextController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private TextField dmDrivingLicense;
    @FXML private TextField dmCarNumber;
    @FXML private TextField dmSalary;

    private DeliveryMan deliveryMan; // To store the deliveryMan object passed from the first scene

    // This method is called to set the deliveryMan object from the first scene
    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
        // Debugging: Check if the deliveryMan object is correctly passed
        System.out.println("DeliveryMan object set: " + this.deliveryMan.getName());
    }

    @FXML
    public void deliveryManRegistration(ActionEvent event) {
        if (deliveryMan == null) {
            // Ensure deliveryMan is not null
            showAlert("Error", "No delivery man data available. Please go back and enter the basic details.");
            return;
        }

        // Collect data from the second form
        String drivingLicenseNo = dmDrivingLicense.getText();
        String carNumber = dmCarNumber.getText();
        String salary = dmSalary.getText();

        // Set the collected data into the deliveryMan object
        deliveryMan.setDrivingLicenseNo(drivingLicenseNo);
        deliveryMan.setCarNumber(carNumber);
        deliveryMan.setSalary(salary);

        // Debugging: Print the data for confirmation
        System.out.println("Name: " + deliveryMan.getName());
        System.out.println("Driving License No: " + deliveryMan.getDrivingLicenseNo());
        System.out.println("Car Number: " + deliveryMan.getCarNumber());
        System.out.println("Salary: " + deliveryMan.getSalary());

        // Convert the DeliveryMan object to a MongoDB Document
        Document deliveryManDoc = deliveryMan.toDocument();

        // Insert the document into the MongoDB database
        MongodbConnection.getCollection("MainDB", "DeliveryMan").insertOne(deliveryManDoc);

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Success");
        alert.setHeaderText("Registration Completed Successfully");
        alert.showAndWait();
    }

    @FXML
    public void switchToOffice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/office.fxml"));
        root = loader.load();
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

    // Method to show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
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
