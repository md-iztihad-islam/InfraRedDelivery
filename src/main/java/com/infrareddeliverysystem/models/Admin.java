package com.infrareddeliverysystem.models;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

public class Admin {
    private String username;
    private String name;
    private String password;

    public Admin(String name, String password, String username) {
        this.username = username;
        this.name = name;
        setPassword(password);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password,  String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public Document toDocument(){
        return new Document("username",username)
                .append("name",name)
                .append("password",password);
    }
}
