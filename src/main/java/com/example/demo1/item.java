package com.example.demo1;

public class item {
    private double price;
    private String name;
    private String id;

    @Override
    public String toString() {
        return "item{" +
                "price=" + price +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
