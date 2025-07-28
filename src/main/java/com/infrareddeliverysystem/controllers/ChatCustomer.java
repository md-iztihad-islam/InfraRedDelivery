package com.infrareddeliverysystem.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class ChatCustomer {
    @FXML private VBox chatVBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Button audioCallButton;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private BufferedReader in;
    private PrintWriter out;
    private String trackingId;
    private String deliveryManId;
    private Socket chatSocket;
    private Socket audioSocket;
    private ServerSocket audioServerSocket;
    private Thread audioThread;
    private TargetDataLine microphone;
    private SourceDataLine speakers;

    public void setIDs(String trackingId, String deliveryManId) {
        this.trackingId = trackingId;
        this.deliveryManId = deliveryManId;
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                chatSocket = new Socket("localhost", 5555);
                in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                out = new PrintWriter(chatSocket.getOutputStream(), true);
                out.println("CUSTOMER:" + trackingId + ":" + deliveryManId);

                String line;
                while ((line = in.readLine()) != null) {
                    String msg = line.trim();
                    if (msg.equals("CALL:REQUEST")) {
                        Platform.runLater(this::showCallRequestDialog);
                    } else if (msg.equals("CALL:ACCEPT")) {
                        Platform.runLater(this::startAudioCallAsClient);
                    } else if (msg.equals("CALL:DECLINE")) {
                        Platform.runLater(() -> showCallDialog("Call declined."));
                    } else if (msg.equals("CALL:DISCONNECT")) {
                        Platform.runLater(this::stopAudioCall);
                    } else {
                        Platform.runLater(() -> addMessage(msg, false));
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @FXML
    private void onSend() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty() && out != null) {
            out.println(msg);
            addMessage(msg, true);
            messageField.clear();
        }
    }

    @FXML
    private void onAudioCall(ActionEvent event) {
        if (out != null) {
            out.println("CALL:REQUEST");
            showCallDialog("Audio call requested. Waiting for response...");
        }
    }

    private void showCallDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Audio Call");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void showCallRequestDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Incoming Audio Call");
        alert.setHeaderText(null);
        alert.setContentText("Rider is requesting an audio call. Accept?");
        ButtonType accept = new ButtonType("Accept");
        ButtonType decline = new ButtonType("Decline");
        alert.getButtonTypes().setAll(accept, decline);
        alert.showAndWait().ifPresent(response -> {
            if (response == accept) {
                out.println("CALL:ACCEPT");
                startAudioCallAsServer();
            } else {
                out.println("CALL:DECLINE");
            }
        });
    }

    private void startAudioCallAsServer() {
        new Thread(() -> {
            try {
                audioServerSocket = new ServerSocket(6000);
                audioSocket = audioServerSocket.accept();
                startAudioStreaming();
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    private void startAudioCallAsClient() {
        new Thread(() -> {
            try {
                audioSocket = new Socket("localhost", 6000);
                startAudioStreaming();
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    private void startAudioStreaming() {
        audioThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                microphone = AudioSystem.getTargetDataLine(format);
                microphone.open(format);
                microphone.start();
                speakers = AudioSystem.getSourceDataLine(format);
                speakers.open(format);
                speakers.start();

                OutputStream audioOut = audioSocket.getOutputStream();
                InputStream audioIn = audioSocket.getInputStream();
                byte[] buffer = new byte[4096];

                Thread sendThread = new Thread(() -> {
                    try {
                        while (!audioSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                            int count = microphone.read(buffer, 0, buffer.length);
                            if (count > 0) audioOut.write(buffer, 0, count);
                        }
                    } catch (IOException ignored) {}
                });
                sendThread.setDaemon(true);
                sendThread.start();

                while (!audioSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    int count = audioIn.read(buffer, 0, buffer.length);
                    if (count > 0) speakers.write(buffer, 0, count);
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        audioThread.setDaemon(true);
        audioThread.start();
        Platform.runLater(this::showDisconnectButton);
    }

    private void showDisconnectButton() {
        removeDisconnectButton();
        Button disconnectBtn = new Button("Disconnect Call");
        disconnectBtn.setOnAction(e -> {
            out.println("CALL:DISCONNECT");
            stopAudioCall();
        });
        chatVBox.getChildren().add(disconnectBtn);
    }

    private void removeDisconnectButton() {
        chatVBox.getChildren().removeIf(node ->
                node instanceof Button && ((Button) node).getText().equals("Disconnect Call")
        );
    }

    private void stopAudioCall() {
        try {
            if (audioThread != null) {
                audioThread.interrupt();
                audioThread = null;
            }
            if (microphone != null) {
                microphone.stop();
                microphone.close();
                microphone = null;
            }
            if (speakers != null) {
                speakers.stop();
                speakers.close();
                speakers = null;
            }
            if (audioSocket != null && !audioSocket.isClosed()) {
                audioSocket.close();
                audioSocket = null;
            }
            if (audioServerSocket != null && !audioServerSocket.isClosed()) {
                audioServerSocket.close();
                audioServerSocket = null;
            }
        } catch (IOException ignored) {}
        Platform.runLater(() -> {
            showCallDialog("Audio call ended.");
            removeDisconnectButton();
        });
    }

    private void addMessage(String msg, boolean sentByMe) {
        HBox hbox = new HBox();
        hbox.setMaxWidth(chatVBox.getPrefWidth());
        hbox.setSpacing(10);
        Label label = new Label(msg);
        label.setWrapText(true);
        label.getStyleClass().add(sentByMe ? "sent-message" : "received-message");
        label.setMaxWidth(400);
        hbox.setAlignment(sentByMe ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);
        hbox.getChildren().add(label);
        chatVBox.getChildren().add(hbox);
        chatScrollPane.setVvalue(1.0);
    }

    public void onBackButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/infrareddeliverysystem/fxml/ParcelIDInput.fxml"));
        root = loader.load();
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rider");
        stage.show();
    }
}