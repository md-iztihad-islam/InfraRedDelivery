package com.infrareddeliverysystem.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class ChatRider {
    @FXML private VBox chatVBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private BufferedReader in;
    private PrintWriter out;
    private String deliveryManId;
    private String trackingId;


    public void setIDs(String trackingId, String deliveryManId) {
        this.trackingId = trackingId;
        this.deliveryManId = deliveryManId;
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 5555);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("RIDER:" + deliveryManId + ":" + trackingId);

                String line;
                while ((line = in.readLine()) != null) {
                    String msg = line.trim();
                    Platform.runLater(() -> addMessage(msg, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onSend() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            addMessage(msg, true);
            messageField.clear();
        }
    }
    @FXML
    public void onBackButton(ActionEvent event) throws IOException {
        //ekhane add delivery.fxml e niye jais. with appropriate trackingID
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/infrareddeliverysystem/fxml/delivery.fxml"));
        root = loader.load();

        DeliveryController deliveryController = loader.getController();
        deliveryController.setParcelID(trackingId);


        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rider");
        stage.show();

        
        
    }

    private void addMessage(String msg, boolean sentByMe) {
        HBox hbox = new HBox();
        hbox.setMaxWidth(chatVBox.getPrefWidth());
        hbox.setSpacing(10);

        Label label = new Label(msg);
        label.setWrapText(true);
        label.getStyleClass().add(sentByMe ? "sent-message" : "received-message");
        label.setMaxWidth(400);

        if (sentByMe) {
            hbox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            hbox.getChildren().add(label);
        } else {
            hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            hbox.getChildren().add(label);
        }
        chatVBox.getChildren().add(hbox);
        chatScrollPane.setVvalue(1.0);
    }


}