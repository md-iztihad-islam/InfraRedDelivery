module com.infrareddeliverysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires jbcrypt;


    opens com.infrareddeliverysystem.controllers to javafx.fxml;
    opens com.infrareddeliverysystem.models to javafx.fxml;

    exports com.infrareddeliverysystem;

}