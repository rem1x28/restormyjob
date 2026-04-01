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
    private List<pizza> pizzaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        pizzaContainer = findViewById(R.id.pizzaContainer);

        // Загружаем данные (вместо БД используем список, так как PizzaDatabaseHelper отсутствует)
        loadPizzas();

        displayPizzas();
    }

    private void loadPizzas() {
        pizzaList.clear();
        pizzaList.add(new pizza("Паста Карбонара", 700, R.drawable.karbonara));
        pizzaList.add(new pizza("Сырники", 450, R.drawable.sirniki));
        pizzaList.add(new pizza("Фритата", 420, R.drawable.freetata));
        pizzaList.add(new pizza("Песто", 500, R.drawable.pecto));
        pizzaList.add(new pizza("Греческий салат", 400, R.drawable.grcheski));
    }

    private void displayPizzas() {
        if (pizzaContainer == null) return;
        pizzaContainer.removeAllViews(); // Очищаем контейнер перед обновлением

        for (pizza item : pizzaList) {
            View pizzaItemView = getLayoutInflater().inflate(R.layout.item_pizza, pizzaContainer, false);

            ImageButton imgPizza = pizzaItemView.findViewById(R.id.imgPizza);
            TextView tvName = pizzaItemView.findViewById(R.id.tvPizzaName);
            TextView tvPrice = pizzaItemView.findViewById(R.id.tvPizzaPrice);

            if (imgPizza != null) imgPizza.setImageResource(item.getImageResId());
            if (tvName != null) tvName.setText(item.getName());
            if (tvPrice != null) tvPrice.setText("Цена: " + item.getPrice() + " руб.");

            if (imgPizza != null) {
                imgPizza.setOnClickListener(v -> {
                    Intent intent = new Intent(MENUGOGOL.this, gogolDetailActivity.class);
                    intent.putExtra("pizza_name", item.getName());
                    intent.putExtra("pizza_price", item.getPrice());
                    intent.putExtra("pizza_image", item.getImageResId());
                    startActivity(intent);
                });
            }

            pizzaContainer.addView(pizzaItemView); // Добавляем элемент в контейнер
        }
    }
}
