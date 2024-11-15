package com.example.demo1;

import java.util.HashMap;

public class Appointment {
    private String payment;
    private String tableNumber;
    private String date;
    private String address;
    private String nameCustomer;
    private double price;
    private HashMap<String, Number> selectedServices = new HashMap<>();
    private String number;
    private String name;
    private String id;
    private String time;
    private String comment;
    private String type;

    public Appointment() {
        this.comment = ""; // Default value for the comment field
    }

    // Getters and Setters

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public HashMap<String, Number> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(HashMap<String, Number> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNameCustomer() {
        return nameCustomer;
    }

    public void setNameCustomer(String nameCustomer) {
        this.nameCustomer = nameCustomer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "payment='" + payment + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                ", date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", nameCustomer='" + nameCustomer + '\'' +
                ", price=" + price +
                ", selectedServices=" + selectedServices +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
