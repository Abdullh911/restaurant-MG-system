package com.example.demo1;

import java.util.ArrayList;

public class Appointment {
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String Date;
    int Price;
    ArrayList<String>selectedServices;
    String Number;
    String Name;
    int tip;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "time='" + time + '\'' +
                ", Date='" + Date + '\'' +
                ", Price=" + Price +
                ", selectedServices=" + selectedServices +
                ", Number='" + Number + '\'' +
                ", Name='" + Name + '\'' +
                ", tip=" + tip +
                ", id=" + id +
                '}';
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public ArrayList<String> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(ArrayList<String> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
