package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackParcelCustomerController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Timeline refreshTimeline;
    private String parcelID;
    private String deliveryManId;
    private boolean deliveredAnimationPlayed = false;
    @FXML private javafx.scene.image.ImageView pandaImage;
    @FXML private Label deliveredLabel;
    @FXML
    private Label parDes;
    @FXML
    private Label dmNameDelivery;
    @FXML
    private Label total;
    @FXML
    private Label paymentStatus;
    @FXML
    private Label deliveryDateParcel;
    @FXML
    private Circle atOurWareHouse;
    @FXML
    private Circle deliveryManAssigned;
    @FXML
    private Circle onYourWay;
    @FXML
    private Circle delivered;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button chat;
    @FXML
    private Button pay;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetCircleStyling();

        if (chat != null) {
            chat.setOnAction(event -> handleChatRequest());
        }

        if (pay != null) {
            pay.setOnAction(event -> handlePayment());
        }
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (parcelID != null) fetchParcelDetails();
                })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }
    public void stopPolling() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }
    @FXML
    private void handleChatRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/welcometochatcustomer.fxml"));
            Parent welcomeRoot = loader.load();


            com.infrareddeliverysystem.controllers.Welcometochatcustomer welcomeController = loader.getController();
            welcomeController.setIDs(parcelID, deliveryManId);

            Stage stage = (Stage) chat.getScene().getWindow();
            stage.setScene(new Scene(welcomeRoot));
            stage.setTitle("Welcome to Chat");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlePayment() {
        System.out.println("Payment requested for parcel: " + parcelID);

        if (parcelID != null) {
            try {
                MongoDatabase database = MongodbConnection.getDatabase("MainDB");
                MongoCollection<Document> parcels = database.getCollection("Parcels");
                parcels.updateOne(
                        new Document("_id", new ObjectId(parcelID)),
                        new Document("$set", new Document("isPaid", true))
                );

                String paymentUrl = "https://payment-system-for-infra-red.vercel.app/" + parcelID;
                openPaymentGateway(paymentUrl);

                paymentStatus.setText("Paid");
                if (pay != null) {
                    pay.setDisable(true);
                }
            } catch (Exception e) {
                System.err.println("Payment update failed: " + e.getMessage());
            }
        }
    }


    private void playDeliveredAnimation() {
        if (deliveredAnimationPlayed) return;
        deliveredAnimationPlayed = true;

        pandaImage.setVisible(true);
        pandaImage.setOpacity(0);
        pandaImage.setScaleX(1.2);
        pandaImage.setScaleY(1.2);
        pandaImage.setTranslateY(200);

        deliveredLabel.setVisible(false);
        deliveredLabel.setOpacity(0);
        deliveredLabel.setScaleX(1.0);
        deliveredLabel.setScaleY(1.0);

        javafx.animation.FadeTransition pandaFadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(900), pandaImage);
        pandaFadeIn.setFromValue(0);
        pandaFadeIn.setToValue(1);

        javafx.animation.TranslateTransition pandaUp = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(1200), pandaImage);
        pandaUp.setFromY(200);
        pandaUp.setToY(0);
        pandaUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.ScaleTransition pandaBounce = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(700), pandaImage);
        pandaBounce.setFromX(1.2); pandaBounce.setFromY(1.2);
        pandaBounce.setToX(1.0); pandaBounce.setToY(1.0);
        pandaBounce.setAutoReverse(true);
        pandaBounce.setCycleCount(2);

        javafx.animation.PauseTransition labelDelay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1000));
        javafx.animation.FadeTransition labelFadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), deliveredLabel);
        labelFadeIn.setFromValue(0);
        labelFadeIn.setToValue(1);

        javafx.animation.RotateTransition labelShake = new javafx.animation.RotateTransition(javafx.util.Duration.millis(700), deliveredLabel);
        labelShake.setFromAngle(-10); labelShake.setToAngle(10);
        labelShake.setCycleCount(4); labelShake.setAutoReverse(true);

        javafx.animation.PauseTransition hold = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));

        javafx.animation.FadeTransition pandaFadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), pandaImage);
        pandaFadeOut.setFromValue(1);
        pandaFadeOut.setToValue(0);

        javafx.animation.FadeTransition labelFadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), deliveredLabel);
        labelFadeOut.setFromValue(1);
        labelFadeOut.setToValue(0);

        javafx.animation.SequentialTransition seq = new javafx.animation.SequentialTransition(
                new javafx.animation.ParallelTransition(pandaFadeIn, pandaUp, pandaBounce),
                labelDelay,
                new javafx.animation.ParallelTransition(labelFadeIn, labelShake),
                hold,
                new javafx.animation.ParallelTransition(pandaFadeOut, labelFadeOut)
        );

        seq.setOnFinished(e -> {
            deliveredLabel.setVisible(false);
            deliveredLabel.setOpacity(1.0);
            deliveredLabel.setRotate(0);
            deliveredLabel.setScaleX(1.0);
            deliveredLabel.setScaleY(1.0);
            pandaImage.setVisible(false);
            pandaImage.setOpacity(1.0);
            pandaImage.setScaleX(1.0);
            pandaImage.setScaleY(1.0);
            pandaImage.setTranslateY(0);
        });

        deliveredLabel.setVisible(true);
        seq.play();
    }
    private void openPaymentGateway(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            } else {
                System.out.println("Desktop is not supported. Cannot open the browser.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetCircleStyling() {
        atOurWareHouse.getStyleClass().removeAll("active-circle");
        deliveryManAssigned.getStyleClass().removeAll("active-circle");
        onYourWay.getStyleClass().removeAll("active-circle");
        delivered.getStyleClass().removeAll("active-circle");
    }

    @FXML
    public void setParcelID(String parcelID) {
        this.parcelID = parcelID;

//        System.out.println("Parcel ID set: " + this.parcelID);
        fetchParcelDetails();
    }

    public void fetchParcelDetails() {
        ObjectId id = new ObjectId(parcelID);
        MongoDatabase database = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> parcel = database.getCollection("Parcels");
        Document parcelDetails = parcel.find(new Document("_id", id)).first();

        if (parcelDetails == null) {
            System.out.println("Parcel not found!");
            return;
        }
        String deliveryManIdStr = parcelDetails.getString("deliveryManId");
        this.deliveryManId = deliveryManIdStr;
//        System.out.println("Fetched Parcel Details: " + parcelDetails.toJson());
//
//        String deliveryManIdStr = parcelDetails.getString("deliveryManId");
        ObjectId deliveryManId = new ObjectId(deliveryManIdStr);

        MongoCollection<Document> deliveryManCollection = database.getCollection("DeliveryMan");
        Document deliveryManDetails = deliveryManCollection.find(new Document("_id", deliveryManId)).first();

        if (deliveryManDetails == null) {
            System.out.println("DeliveryMan not found!");
            return;
        }

        parDes.setText(parcelDetails.getString("parcelDescription"));
        dmNameDelivery.setText(deliveryManDetails.getString("name"));

        Object totalChargeObj = parcelDetails.get("totalCharge");
        if (totalChargeObj instanceof Integer) {
            total.setText(String.valueOf((Integer) totalChargeObj));
        } else if (totalChargeObj instanceof Double) {
            total.setText(String.valueOf((Double) totalChargeObj));
        }

        deliveryDateParcel.setText(parcelDetails.getString("estimatedDeliveryDate"));

        boolean isPaid = parcelDetails.getBoolean("isPaid", false);
        if (isPaid) {
            paymentStatus.setText("Paid");
            if (pay != null) pay.setDisable(true);
        } else {
            paymentStatus.setText("Not Paid");
            if (pay != null) pay.setDisable(false);
        }


        resetCircleStyling();

        boolean isDelivered = parcelDetails.getBoolean("isDelivered", false);
        if (isDelivered) {
            playDeliveredAnimation();
            atOurWareHouse.getStyleClass().add("active-circle");
            deliveryManAssigned.getStyleClass().add("active-circle");
            onYourWay.getStyleClass().add("active-circle");
            delivered.getStyleClass().add("active-circle");
            progressBar.setProgress(1);
        } else {
            String status = parcelDetails.getString("status");
            updateTrackingStatus(status);
        }
    }

    private void updateTrackingStatus(String status) {
        if (status == null) return;

        switch (status) {
            case "At our Warehouse":
                atOurWareHouse.getStyleClass().add("active-circle");
                progressBar.setProgress(0.25);
                break;

            case "Delivery Man Assigned":
                atOurWareHouse.getStyleClass().add("active-circle");
                deliveryManAssigned.getStyleClass().add("active-circle");
                progressBar.setProgress(0.50);
                break;

            case "On the way":
                atOurWareHouse.getStyleClass().add("active-circle");
                deliveryManAssigned.getStyleClass().add("active-circle");
                onYourWay.getStyleClass().add("active-circle");
                progressBar.setProgress(0.75);
                break;
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