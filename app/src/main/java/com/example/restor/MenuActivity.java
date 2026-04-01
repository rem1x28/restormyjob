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

public class MenuActivity extends AppCompatActivity {

    private LinearLayout pizzaContainer;
    private List<pizza> pizzaList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        pizzaContainer = findViewById(R.id.pizzaContainer);

        // Заполняем список пицц
        pizzaList.add(new pizza("Паста Карбонара", 450, R.drawable.pizza_margarita));
        pizzaList.add(new pizza("Пепперони", 450, R.drawable.pizza_pepperoni));
        pizzaList.add(new pizza("Четыре сыра", 420, R.drawable.pizza_four_cheese));
        pizzaList.add(new pizza("Мясная", 500, R.drawable.pizza_meat));
        pizzaList.add(new pizza("Вегетарианская", 400, R.drawable.pizza_vegetarian));

        displayPizzas();
    }

    private void displayPizzas() {
        for (pizza pizza : pizzaList) {
            View pizzaItem = getLayoutInflater().inflate(R.layout.item_pizza, null);

            ImageButton imgPizza = pizzaItem.findViewById(R.id.imgPizza);
            TextView tvName = pizzaItem.findViewById(R.id.tvPizzaName);
            TextView tvPrice = pizzaItem.findViewById(R.id.tvPizzaPrice);


            // Устанавливаем данные
            imgPizza.setImageResource(pizza.getImageResId());
            tvName.setText(pizza.getName());
            tvPrice.setText("Цена: " + pizza.getPrice() + " руб.");

            // Обработчик нажатия на изображение пиццы
            imgPizza.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, gogolDetailActivity.class);
                intent.putExtra("pizza_name", pizza.getName());
                intent.putExtra("pizza_price", pizza.getPrice());
                intent.putExtra("pizza_image", pizza.getImageResId());
                startActivity(intent);
            });
        }
    }
}

            // Обработчик кнопки «В корзину»
