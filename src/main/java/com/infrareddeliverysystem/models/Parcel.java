package com.infrareddeliverysystem.models;

import org.bson.Document;

import java.util.Date;

public class Parcel {

    private String parcelDescription;

    private String parcelType;
    private double weight;

    private String senderName;
    private String senderPhone;


    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private String deliveryManId;

    private String status;
    private int price;
    private int deliveryCharge;
    private int totalCharge;
    private String whereAt;
    private String estimatedDeliveryDate;
    private boolean isPaid;
    private boolean isDelivered;


    public Parcel() {
        this.status = "At our Warehouse";
        this.whereAt = "At Our Warehouse";
        this.isPaid = false;
        this.isDelivered = false;
    }

    public Document toDocument() {
        Document parcelDocument = new Document();
        parcelDocument.put("parcelDescription", parcelDescription);
        parcelDocument.put("parcelType", parcelType);
        parcelDocument.put("weight", weight);
        parcelDocument.put("senderName", senderName);
        parcelDocument.put("senderPhone", senderPhone);
        parcelDocument.put("receiverName", receiverName);
        parcelDocument.put("receiverPhone", receiverPhone);
        parcelDocument.put("receiverAddress", receiverAddress);
        parcelDocument.put("deliveryManId", deliveryManId);
        parcelDocument.put("status", status);
        parcelDocument.put("price", price);
        parcelDocument.put("deliveryCharge", deliveryCharge);
        parcelDocument.put("totalCharge", totalCharge);
        parcelDocument.put("whereAt", whereAt);
        parcelDocument.put("estimatedDeliveryDate", estimatedDeliveryDate);
        return parcelDocument;
    }


    public String getParcelDescription() {
        return parcelDescription;
    }

    public void setParcelDescription(String parcelDescription) {
        this.parcelDescription = parcelDescription;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getParcelType() {
        return parcelType;
    }

    public void setParcelType(String parcelType) {
        this.parcelType = parcelType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getDeliveryManId() {
        return deliveryManId;
    }

    public void setDeliveryManId(String deliveryManId) {
        this.deliveryManId = deliveryManId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getWhereAt() {
        return whereAt;
    }

    public void setWhereAt(String whereAt) {
        this.whereAt = whereAt;
    }

    public String getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public int getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(int totalCharge) {
        this.totalCharge = totalCharge;
    }
}