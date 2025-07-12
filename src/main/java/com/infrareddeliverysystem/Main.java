package com.infrareddeliverysystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        String css = Objects.requireNonNull(getClass().getResource("/com/infrareddeliverysystem/fxml/style.css")
        ).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Home Page");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
