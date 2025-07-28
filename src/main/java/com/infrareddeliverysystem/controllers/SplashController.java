package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;

public class SplashController {
    @FXML private ImageView truckImage;
    @FXML private ImageView smokeImage;
    @FXML private Label titleLabel;
    @FXML private Button exploreButton;

    @FXML
    public void initialize() {
        double startX = -truckImage.getFitWidth();
        double endX = 800;

        truckImage.setLayoutX(startX);
        smokeImage.setLayoutX(startX + 60);

        TranslateTransition truckMove = new TranslateTransition(Duration.seconds(3), truckImage);
        truckMove.setFromX(0);
        truckMove.setToX(endX - startX);

        Timeline smokeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> smokeImage.setOpacity(0.7)),
                new KeyFrame(Duration.seconds(2), e -> smokeImage.setOpacity(0.3)),
                new KeyFrame(Duration.seconds(2.5), e -> smokeImage.setOpacity(0))
        );

        truckMove.setOnFinished(e -> {
            truckImage.setVisible(false);
            smokeImage.setVisible(false);


            titleLabel.setScaleX(0.7);
            titleLabel.setScaleY(0.7);
            FadeTransition fade = new FadeTransition(Duration.seconds(1.7), titleLabel);
            fade.setFromValue(0);
            fade.setToValue(1);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(1.7), titleLabel);
            scale.setFromX(0.7);
            scale.setFromY(0.7);
            scale.setToX(1.0);
            scale.setToY(1.0);

            ParallelTransition popIn = new ParallelTransition(fade, scale);
            popIn.setOnFinished(ev -> {
                FadeTransition btnFade = new FadeTransition(Duration.seconds(1.0), exploreButton);
                btnFade.setFromValue(0);
                btnFade.setToValue(1);
                btnFade.play();
            });
            popIn.play();
        });

        truckMove.play();
        smokeTimeline.play();

        exploreButton.setOnAction(this::goToHome);
    }

    private void goToHome(ActionEvent event) {
        try {
            Parent home = FXMLLoader.load(Main.class.getResource("/com/infrareddeliverysystem/fxml/home.fxml"));
            Stage stage = (Stage) exploreButton.getScene().getWindow();
            stage.setScene(new Scene(home));
            stage.setTitle("Home Page");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}