package com.infrareddeliverysystem.models;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import org.bson.Document;

import java.util.ArrayList;

public class DeliveryMan {
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String drivingLicenseNo;
    private String carNumber;
    private String salary;
    private ArrayList<String> deliveries;
    private int deliveryCompleted;
    private int deliveryFailed;

    public DeliveryMan(String name, String username, String password, String email, String phone, String drivingLicenseNo, String carNumber, String salary) {
        this.name = name;
        this.username = username;
        setPassword(password);
        this.email = email;
        this.phone = phone;
        this.drivingLicenseNo = drivingLicenseNo;
        this.carNumber = carNumber;
        this.salary = salary;
        this.deliveries = new ArrayList<String>();
        this.deliveryCompleted = 0;
        this.deliveryFailed = 0;
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("name", name);
        document.put("username", username);
        document.put("password", password);
        document.put("email", email);
        document.put("phone", phone);
        document.put("drivingLicenseNo", drivingLicenseNo);
        document.put("carNumber", carNumber);
        document.put("salary", salary);
        document.put("deliveries", deliveries);
        document.put("deliveryCompleted", deliveryCompleted);
        document.put("deliveryFailed", deliveryFailed);

        return document;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public ArrayList<String> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(ArrayList<String> deliveries) {
        this.deliveries = deliveries;
    }

    public int getDeliveryCompleted() {
        return deliveryCompleted;
    }

    public void setDeliveryCompleted(int deliveryCompleted) {
        this.deliveryCompleted = deliveryCompleted;
    }

    public int getDeliveryFailed() {
        return deliveryFailed;
    }

    public void setDeliveryFailed(int deliveryFailed) {
        this.deliveryFailed = deliveryFailed;
    }
}
