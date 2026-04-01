package com.example.restor;

public class pizza {
    private String name;
    private int price;
    private int imageResId;
    private int count; // количество пицц в корзине (по умолчанию 0)

    public pizza(String name, int price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.count = 0; // инициализируем счётчик
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public int getCount() { return count; } // геттер для count

    public void incrementCount() { // метод для увеличения количества
        count++;
    }

    // Добавляем метод для установки значения счётчика
    public void setCount(int count) {
        this.count = count;
    }
}