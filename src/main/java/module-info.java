module com.infrareddeliverysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires jbcrypt;
    requires java.net.http;
    requires telegrambots.meta;
    requires telegrambots;
    requires java.logging;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.databind;


    opens com.infrareddeliverysystem.controllers to javafx.fxml;
    opens com.infrareddeliverysystem.models to javafx.fxml;

    exports com.infrareddeliverysystem;

}