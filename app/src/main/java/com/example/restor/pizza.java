package com.example.restor;

public class pizza {
    private String name;
    private int price;
    private int imageResId;
    private String ingredients;
    private int count;

    public pizza(String name, int price, int imageResId) {
        this(name, price, imageResId, "Классический рецепт от шеф-повара.");
    }

    public pizza(String name, int price, int imageResId, String ingredients) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.ingredients = ingredients;
        this.count = 0;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getIngredients() { return ingredients; }
    public int getCount() { return count; }

    public void incrementCount() {
        count++;
    }

    public void decrementCount() {
        if (count > 0) {
            count--;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }
}
