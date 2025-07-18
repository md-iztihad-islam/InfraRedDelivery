package com.infrareddeliverysystem.controllers;

import com.infrareddeliverysystem.Main;
import com.infrareddeliverysystem.db.MongodbConnection;
import com.infrareddeliverysystem.models.DeliveryMan;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryListController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private ObjectId deliveryManId;
    private String deliveryId;

    @FXML
    private ListView<String> parcelList;


    private void loadPendingParcels() {
        MongoDatabase database = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> parcelCollection = database.getCollection("Parcels");
        System.out.println("Number of parcels in collection: " + parcelCollection.countDocuments());
        Document query = new Document("deliveryManId", deliveryManId.toString());
        System.out.println("Query" + query.toJson());
        List<Document> parcels = parcelCollection.find(query).into(new ArrayList<>());
        System.out.println("Number of parcels found: " + parcels.size());

        ObservableList<String> parcelDetailsList = FXCollections.observableArrayList();


        for (Document parcel : parcels) {
//            String parcelDetails = "Parcel ID: " + parcel.getObjectId("_id").toString() +
//                    ", Address: " + parcel.getString("receiverAddress") +
//                    ", Description: " + parcel.getString("parcelDescription");
            String parcelId = parcel.getObjectId("_id").toString();
            parcelDetailsList.add(parcelId);

            System.out.println("Parcel Details: " + parcelId);
        }
        parcelList.setItems(parcelDetailsList);


        parcelList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // When an item is selected, store the selected parcel ID
            deliveryId = newValue; // The selected parcel ID
            System.out.println("Selected Parcel ID: " + deliveryId);
            try {
                // Trigger the switch to the delivery screen
                switchToDelivery(null); // Call the method to switch scene
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void switchToDelivery(ActionEvent event) throws IOException {
        String deliveryIdString = parcelList.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/infrareddeliverysystem/fxml/delivery.fxml"));
        Parent root = loader.load();

        DeliveryController deliveryController = loader.getController();
        deliveryController.setParcelID(deliveryIdString);



        Stage stage = (Stage) parcelList.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Delivery List");
        stage.show();

        if (deliveryManId != null) {
            loadPendingParcels();
        } else {
            System.out.println("DeliveryMan ID is not set.");
        }
    }

    public void setDeliveryManId(ObjectId deliveryManId) {
        this.deliveryManId = deliveryManId;
        System.out.println("DeliveryMan set: " + this.deliveryManId.toString());
        MongoDatabase db = MongodbConnection.getDatabase("MainDB");
        MongoCollection<Document> collection = db.getCollection("DeliveryMan");
        Document query = new Document("_id", deliveryManId);
        Document data = collection.find(query).first();

        if (data != null) {
            String deliveryManUsername = data.getString("username");
            System.out.println("Querying for user: " + deliveryManUsername);

            loadPendingParcels();


        }
    }


}
