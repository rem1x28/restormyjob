package com.example.restor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class gogolDetailActivity extends AppCompatActivity {

    private ImageView imgPizzaDetail;
    private TextView tvPizzaNameDetail, tvPizzaPriceDetail, tvPizzaIngredients;
    private Button btnAddToCartDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_detail);

        // Инициализируем элементы интерфейса
        imgPizzaDetail = findViewById(R.id.imgPizzaDetail);
        tvPizzaNameDetail = findViewById(R.id.tvPizzaNameDetail);
        tvPizzaPriceDetail = findViewById(R.id.tvPizzaPriceDetail);
        tvPizzaIngredients = findViewById(R.id.tvPizzaIngredients);
        btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail);

        // Получаем данные из Intent
        Intent intent = getIntent();
        String pizzaName = intent.getStringExtra("pizza_name");
        int pizzaPrice = intent.getIntExtra("pizza_price", 0);
        int pizzaImage = intent.getIntExtra("pizza_image", 0);

        // --- ВОТ ЭТА СТРОКА ДОСТАЕТ УНИКАЛЬНОЕ ОПИСАНИЕ ---
        String ingredients = intent.getStringExtra("pizza_ingredients");

        // Устанавливаем данные в элементы интерфейса
        tvPizzaNameDetail.setText(pizzaName);
        tvPizzaPriceDetail.setText("Цена: " + pizzaPrice + " руб.");
        imgPizzaDetail.setImageResource(pizzaImage);

        // --- УСТАНАВЛИВАЕМ ПРИШЕДШИЙ СОСТАВ ВМЕСТО СТАТИЧНОГО ТЕКСТА ---
        if (ingredients != null) {
            tvPizzaIngredients.setText("Ингредиенты: " + ingredients);
        } else {
            tvPizzaIngredients.setText("Состав: тесто, соус, сыр");
        }

        // Обработчик кнопки «Добавить в корзину»
        btnAddToCartDetail.setOnClickListener(v -> {
            Toast.makeText(this, "Пицца «" + pizzaName + "» добавлена в корзину!", Toast.LENGTH_SHORT).show();
        });
    }
}
