package com.example.restor;

public class Order {
    private int id;
    private String restaurantName;
    private String type; // Бронирование или Предзаказ
    private String details;
    private String dateCreated;

    public Order(int id, String restaurantName, String type, String details, String dateCreated) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.type = type;
        this.details = details;
        this.dateCreated = dateCreated;
    }

    public int getId() { return id; }
    public String getRestaurantName() { return restaurantName; }
    public String getType() { return type; }
    public String getDetails() { return details; }
    public String getDateCreated() { return dateCreated; }
}
