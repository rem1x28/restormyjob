package com.example.restor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class zakazgogol extends AppCompatActivity {

    private LinearLayout PizzaItems;
    private Button btnOrder;

    private int[] pizzaImages = {
            R.drawable.pizza_margarita,
            R.drawable.pizza_pepperoni,
            R.drawable.pizza_four_cheese,
            R.drawable.meat_pizza,
            R.drawable.pizza_vegetarian
    };

    private int[] pizzaPrices = {450, 550, 600, 650, 400};

    private String[] pizzaIngredients = {
            "Свежие томаты, нежная моцарелла, итальянские травы, базилик, фирменный томатный соус.",
            "Пикантная пепперони, много моцареллы, хлопья острого перца, томатный соус.",
            "Сыр моцарелла, нежный дор-блю, пармезан, чеддер, сливочный соус альфредо.",
            "Сочное мясо говядины, ветчина, хрустящий бекон, охотничьи колбаски, моцарелла, соус барбекю.",
            "Шампиньоны, сладкий перец, маслины, свежие томаты, красный лук, моцарелла, базилик."
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazgogol);

        btnOrder = findViewById(R.id.btnOrder);
        PizzaItems = findViewById(R.id.gogolContainer);

        String[] pizzas = getResources().getStringArray(R.array.restor);

        if (PizzaItems != null) {
            for (int i = 0; i < pizzas.length; i++) {
                final String pizzaName = pizzas[i];
                final int imageResId = (i < pizzaImages.length) ? pizzaImages[i] : R.drawable.ic_launcher_background;
                final int price = (i < pizzaPrices.length) ? pizzaPrices[i] : 500;
                final String ingredients = (i < pizzaIngredients.length) ? pizzaIngredients[i] : "Стандартный состав";

                // Раздуваем карточку пиццы
                View pizzaCard = getLayoutInflater().inflate(R.layout.item_pizza, PizzaItems, false);

                // Инициализируем элементы карточки
                ImageView img = pizzaCard.findViewById(R.id.imgPizza);
                TextView title = pizzaCard.findViewById(R.id.tvPizzaName);

                // --- ВОТ ЭТО ИСПРАВЛЯЕТ ЦЕНУ НА ЭКРАНЕ МЕНЮ ---
                TextView tvPrice = pizzaCard.findViewById(R.id.tvPizzaPrice);
                if (tvPrice != null) {
                    tvPrice.setText("Цена: " + price + " руб.");
                }

                title.setText(pizzaName);
                img.setImageResource(imageResId);

                // Переход на экран подробностей
                pizzaCard.setOnClickListener(v -> {
                    Intent intent = new Intent(zakazgogol.this, PizzaDetailActivity.class);
                    // Передаем данные с правильными ключами
                    intent.putExtra("pizza_name", pizzaName);
                    intent.putExtra("pizza_image", imageResId);
                    intent.putExtra("pizza_price", price);
                    intent.putExtra("pizza_ingredients", ingredients);
                    startActivity(intent);
                });

                PizzaItems.addView(pizzaCard);
            }
        }

        btnOrder.setOnClickListener(v -> {
            showOrderDialog("Выберите пиццу из списка, чтобы настроить заказ!");
        });
    }

    private void showOrderDialog(String orderText) {
        new AlertDialog.Builder(this)
                .setTitle("Информация")
                .setMessage(orderText)
                .setPositiveButton("ОК", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
