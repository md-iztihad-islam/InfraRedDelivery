package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class Welcometochatcustomer {
    @FXML private Label welcomeLabel;
    @FXML private Button chatNowButton;
    private String parcelID;
    private String deliveryManId;

    @FXML
    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), welcomeLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        fadeIn.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(ev -> {
                chatNowButton.setOpacity(1);
                ScaleTransition pop = new ScaleTransition(Duration.seconds(0.3), chatNowButton);
                pop.setFromX(0.7);
                pop.setFromY(0.7);
                pop.setToX(1);
                pop.setToY(1);
                pop.play();
            });
            pause.play();
        });
    }
    public void setIDs(String parcelID, String deliveryManId) {
        this.parcelID = parcelID;
        this.deliveryManId = deliveryManId;
    }
    @FXML
    private void onChatNow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/chatCustomer.fxml"));
            Parent chatRoot = loader.load();


            com.infrareddeliverysystem.controllers.ChatCustomer chatController = loader.getController();
            chatController.setIDs(parcelID, deliveryManId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(chatRoot));
            stage.setTitle("Customer Chat");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}