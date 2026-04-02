package com.example.restor;

import android.app.TimePickerDialog;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class zakazpizzaActivity extends AppCompatActivity {

    private LinearLayout pizzaContainer;
    private Button btnOrder;
    private List<pizza> selectedPizzas = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private long existingOrderId = -1;
    private String bookingDetails = "";

    private int[] pizzaImages = {
            R.drawable.pizza_margarita,
            R.drawable.pizza_pepperoni,
            R.drawable.pizza_four_cheese,
            R.drawable.meat_pizza,
            R.drawable.pizza_vegetarian
    };

    private int[] pizzaPrices = {450, 550, 600, 650, 400};

    private String[] pizzaDescriptions = {
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
        setContentView(R.layout.activity_zakazpizza);

        dbHelper = new DatabaseHelper(this);
        btnOrder = findViewById(R.id.btnOrder);
        pizzaContainer = findViewById(R.id.pizzaContainer);

        // Получаем данные о существующей брони
        existingOrderId = getIntent().getLongExtra("existing_order_id", -1);
        bookingDetails = getIntent().getStringExtra("booking_details");

        String[] pizzaNames = getResources().getStringArray(R.array.restor);

        if (pizzaContainer != null) {
            for (int i = 0; i < pizzaNames.length; i++) {
                final String name = pizzaNames[i];
                final int imageResId = (i < pizzaImages.length) ? pizzaImages[i] : R.drawable.ic_launcher_background;
                final int price = (i < pizzaPrices.length) ? pizzaPrices[i] : 500;
                final String description = (i < pizzaDescriptions.length) ? pizzaDescriptions[i] : "Классическая пицца.";

                pizza item = new pizza(name, price, imageResId);
                selectedPizzas.add(item);

                addPizzaToLayout(item, description);
            }
        }

        if (btnOrder != null) {
            btnOrder.setText(existingOrderId != -1 ? "Добавить к бронированию" : "Выбрать время и заказать");
            btnOrder.setOnClickListener(v -> handleOrderClick());
        }
    }

    private void handleOrderClick() {
        if (!hasSelectedItems()) {
            Toast.makeText(this, "Пожалуйста, выберите хотя бы одно блюдо", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingOrderId != -1) {
            // Если уже есть бронь, время не спрашиваем (оно в брони)
            finishPreOrder("");
        } else {
            // Если просто предзаказ, спрашиваем время
            showTimePicker();
        }
    }

    private boolean hasSelectedItems() {
        for (pizza p : selectedPizzas) {
            if (p.getCount() > 0) return true;
        }
        return false;
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            finishPreOrder(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void addPizzaToLayout(pizza item, String description) {
        View pizzaCard = getLayoutInflater().inflate(R.layout.item_pizza, pizzaContainer, false);

        ImageView img = pizzaCard.findViewById(R.id.imgPizza);
        TextView title = pizzaCard.findViewById(R.id.tvPizzaName);
        TextView tvPrice = pizzaCard.findViewById(R.id.tvPizzaPrice);
        TextView tvCount = pizzaCard.findViewById(R.id.tvCount);
        Button btnPlus = pizzaCard.findViewById(R.id.btnPlus);
        Button btnMinus = pizzaCard.findViewById(R.id.btnMinus);

        img.setImageResource(item.getImageResId());
        title.setText(item.getName());
        tvPrice.setText("Цена: " + item.getPrice() + " руб.\n" + description);
        tvCount.setText(String.valueOf(item.getCount()));

        btnPlus.setOnClickListener(v -> {
            item.incrementCount();
            tvCount.setText(String.valueOf(item.getCount()));
        });

        btnMinus.setOnClickListener(v -> {
            item.decrementCount();
            tvCount.setText(String.valueOf(item.getCount()));
        });

        pizzaContainer.addView(pizzaCard);
    }

    private void finishPreOrder(String time) {
        StringBuilder dishSummary = new StringBuilder();
        int total = 0;

        for (pizza p : selectedPizzas) {
            if (p.getCount() > 0) {
                dishSummary.append(p.getName()).append(" x ").append(p.getCount()).append("\n");
                total += p.getPrice() * p.getCount();
            }
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("username", "Unknown");

        String finalDetails;
        if (existingOrderId != -1) {
            // ОБЪЕДИНЯЕМ: Бронь + Блюда
            finalDetails = (bookingDetails != null ? bookingDetails : "") + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого за еду: " + total + " руб.";
            dbHelper.updateOrderDetails(existingOrderId, "Бронь + Предзаказ", finalDetails);
        } else {
            // Обычный заказ со временем
            finalDetails = "Время получения: " + time + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого: " + total + " руб.";
            dbHelper.insertOrder(currentUser, "DODO Pizza", "Предзаказ", finalDetails);
        }

        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_details", "Ваш заказ (DODO Pizza):\n\n" + finalDetails);
        startActivity(intent);
        finish();
    }
}
