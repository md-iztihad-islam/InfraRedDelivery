package com.infrareddeliverysystem.chatbot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Logger;

public class TeleBotServer extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(TeleBotServer.class.getName());

    private final String botUsername;
    private final String botToken;

    public TeleBotServer(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));

        if (text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("start")) {
            response.setText("Welcome to the InfraRed bot! How can I assist you today?\n" +
                    "You can write /help for assistance.");
        } else if(text.equalsIgnoreCase("help")){
            response.setText("Here are some commands you can use:\n" +
                    "start - Start the bot\n" +
                    "help - Get help information\n" +
                    "contact - Contact support\n" +
                    "delivery charges - Get information about delivery charges\n" +
                    "payment - Get information about payment methods\n" +
                    "delivery - Get information about contacting deliveryman\n" +
                    "about us - Learn more about InfraRed\n"
            );
        } else if(text.equalsIgnoreCase("contact")) {
            response.setText("You can contact our support team at infrared@gmail.com or call us at +8801781032582.");

        }else if(text.equalsIgnoreCase("payment")) {
            response.setText("We accept various payment methods including:\n" +
                    "1. Cash on Delivery\n" +
                    "2. Mobile Banking (bKash, Nagad, etc.)\n" +
                    "3. Bank Transfer\n" +
                    "4. Credit/Debit Cards\n" +
                    "Please choose the method that suits you best.");

        } else if(text.equalsIgnoreCase("delivery charges")) {
            response.setText("Our delivery charges are:\n" +
                            "Inside Dhaka: 70tk.\n" +
                            "Outside Dhaka: 120tk.\n");

        }else if(text.equalsIgnoreCase("delivery")){
            response.setText("To contact your deliveryman, please call the number provided in your order confirmation message or you can chat with him directly through our software. " +
                    "If you have any issues, feel free to reach out to our support team.");

        } else if(text.equalsIgnoreCase("about us")) {
            response.setText("InfraRed is a leading delivery service provider in Bangladesh, dedicated to providing fast and reliable delivery solutions. " +
                    "We are committed to customer satisfaction and strive to make your delivery experience seamless.");

        } else if(text.equalsIgnoreCase("hello") || text.equalsIgnoreCase("hi")) {
            response.setText("Hello! How can I assist you today? Please type 'help' for a list of commands.");
        } else {
            response.setText("Sorry, I didn't understand that. Please type 'help' for a list of commands or ask me something else. Or you can call our support team at +8801781032582.");
        }

        try {
            execute(response);
        } catch (TelegramApiException e) {
            System.out.println( "Failed to send message to chat " + chatId + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}