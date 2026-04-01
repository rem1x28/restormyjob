package com.example.restor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MENUGOGOL extends AppCompatActivity {

    private LinearLayout pizzaContainer;
    private List<Pizza> pizzaList = new ArrayList<>();
    private PizzaDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        pizzaContainer = findViewById(R.id.pizzaContainer);
        dbHelper = new PizzaDatabaseHelper(this);

        // Загружаем данные из БД
        loadPizzasFromDatabase();

        displayPizzas();
    }

    private void loadPizzasFromDatabase() {
        pizzaList = dbHelper.getAllPizzas();

        // Если БД пуста, заполняем начальными данными
        if (pizzaList.isEmpty()) {
            initializeDefaultPizzas();
            pizzaList = dbHelper.getAllPizzas(); // Перезагружаем после сохранения
        }
    }

    private void initializeDefaultPizzas() {
        List<Pizza> defaultPizzas = new ArrayList<>();
        defaultPizzas.add(new Pizza("Паста Карбонара", 700, R.drawable.karbonara));
        defaultPizzas.add(new Pizza("Сырники", 450, R.drawable.sirniki));
        defaultPizzas.add(new Pizza("Фритата", 420, R.drawable.freetata));
        defaultPizzas.add(new Pizza("Песто", 500, R.drawable.pecto));
        defaultPizzas.add(new Pizza("Греческий салат", 400, R.drawable.grcheski));


        for (Pizza pizza : defaultPizzas) {
            dbHelper.addPizza(pizza); // Сохраняем каждую пиццу в БД
        }
    }

    private void displayPizzas() {
        pizzaContainer.removeAllViews(); // Очищаем контейнер перед обновлением

        for (Pizza pizza : pizzaList) {
            View pizzaItem = getLayoutInflater().inflate(R.layout.item_pizza, pizzaContainer, false);

            ImageButton imgPizza = pizzaItem.findViewById(R.id.imgPizza);
            TextView tvName = pizzaItem.findViewById(R.id.tvPizzaName);
            TextView tvPrice = pizzaItem.findViewById(R.id.tvPizzaPrice);

            imgPizza.setImageResource(pizza.getImageResId());
            tvName.setText(pizza.getName());
            tvPrice.setText("Цена: " + pizza.getPrice() + " руб.");

            imgPizza.setOnClickListener(v -> {
                Intent intent = new Intent(MENUGOGOL.this, gogolDetailActivity.class);
                intent.putExtra("pizza_name", pizza.getName());
                intent.putExtra("pizza_price", pizza.getPrice());
                intent.putExtra("pizza_image", pizza.getImageResId());
                startActivity(intent);
            });

            pizzaContainer.addView(pizzaItem); // Добавляем элемент в контейнер
        }
    }
}