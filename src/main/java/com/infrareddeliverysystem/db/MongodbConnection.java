package com.infrareddeliverysystem.db;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongodbConnection {
    private static final String connectionString = "mongodb+srv://infrared:infrared123456789@cluster0.xzxbezj.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    private static MongoClient mongoClient;

    private static MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
            .build();

    public static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(settings);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public static MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    public static boolean pingServer() {
        try (MongoClient client = getClient()) {
            client.getDatabase("admin").runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            System.err.println("Ping failed: " + e.getMessage());
            return false;
        }
    }

}
