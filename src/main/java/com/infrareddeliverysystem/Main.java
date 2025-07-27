package com.infrareddeliverysystem;

import com.infrareddeliverysystem.chatbot.TeleBotServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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

        startTelegramBot();
    }

    public static void main(String[] args) {
        launch();
    }

    private void startTelegramBot() {
        Thread botThread = new Thread(() -> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new TeleBotServer(
                        "i_n_f_r_a_r_e_d_bot",
                        "8410962165:AAGdxwx0hQyOF3tR6ryj_H3w4BJNEKFySIo"
                ));
            } catch (TelegramApiException e) {
                System.out.println("Error starting Telegram Bot: " + e.getMessage());
            }
        });
        botThread.setDaemon(true);
        botThread.start();
    }

}
