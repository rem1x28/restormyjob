package com.example.restor;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final String name;
    private final double rating;
    private final String restaurantType;
    private final int imageRes;
    private final String distance;
    private final List<String> dishes; // Список блюд в этом ресторане

    public Restaurant(String name, double rating, int imageRes, String distance, String restaurantType, List<String> dishes) {
        this.name = name;
        this.restaurantType = restaurantType;
        this.rating = rating;
        this.imageRes = imageRes;
        this.distance = distance;
        this.dishes = dishes != null ? dishes : new ArrayList<>();
    }

    public String getName() { return name; }
    public double getRating() { return rating; }
    public int getImageRes() { return imageRes; }
    public String getDistance() { return distance; }
    public String getRestaurantType() { return restaurantType; }
    public List<String> getDishes() { return dishes; }
}
