package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.models.DeliveryMan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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


    private final DeliveryMan deliveryMan = new DeliveryMan();

    @FXML
    public void goDeliveryManNextPage(ActionEvent event) throws IOException {
        String name = dmName.getText();
        String username = dmUsername.getText();
        String password = dmPassword.getText();
        String email = dmEmail.getText();
        String phone = dmPhone.getText();

//        System.out.println("User entered " + name + " " + username + " " + password + " " + email + " " + phone);

        deliveryMan.setName(name);
        deliveryMan.setUsername(username);
        deliveryMan.setPassword(password);
        deliveryMan.setEmail(email);
        deliveryMan.setPhone(phone);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/registerDeliveryManNext.fxml"));
        root = loader.load();


        RegisterDeliveryManNextController nextController = loader.getController();
        nextController.setDeliveryMan(deliveryMan);


        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Register Last Page");
        stage.show();
    }


    @FXML
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