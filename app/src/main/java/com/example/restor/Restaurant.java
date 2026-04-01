package com.example.restor;

public class Restaurant {
    private String name;
    public String restor;
    private double rating;
    private String restaurantType;
    private int imageRes;
    private String distance;

    public Restaurant(String name, double rating, int imageRes, String distance, String restaurantType) {
        this.name = name;
        this.restaurantType = restaurantType;
        this.rating = rating;
        this.imageRes = imageRes;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getDistance() {
        return distance;
    }

    public String getRestaurantType() { return restaurantType;}
}