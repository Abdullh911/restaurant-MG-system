package com.example.demo1;

import java.util.ArrayList;

public class ClientCard {
    String Name;
    String Number;
    ArrayList<Appointment>visits;


    @Override
    public String toString() {
        return "ClientCard{" +
                "Name='" + Name + '\'' +
                ", Number='" + Number + '\'' +
                ", visits=" + visits +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public ArrayList<Appointment> getVisits() {
        return visits;
    }

    public void setVisits(ArrayList<Appointment> visits) {
        this.visits = visits;
    }
}
