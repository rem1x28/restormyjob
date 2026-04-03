package com.example.restor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MENUGOGOL extends AppCompatActivity {

    private LinearLayout pizzaContainer;
    private List<pizza> dishList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private long existingOrderId = -1;
    private String bookingDetails = "";

    private String[] names = {
            "Котлета по-киевски",
            "Хрустящие баклажаны",
            "Овощи на гриле",
            "Тирамису фирменный"
    };

    private String[] descriptions = {
            "Золотистая панировка, нежное куриное филе и ароматное сливочное масло с зеленью внутри. Подается с воздушным картофельным пюре.",
            "Обжаренные до хрустящей корочки ломтики баклажанов в пикантном соусе с кинзой и нежным сливочным сыром.",
            "Цукини, болгарский перец, баклажаны и томаты, обжаренные на открытом огне с ароматными травами.",
            "Воздушный итальянский десерт на основе маскарпоне с кофейной пропиткой и щедрым слоем шоколадной крошки."
    };

    private int[] prices = {650, 520, 480, 390};

    private int[] images = {
            R.drawable.kotletapokievskispyre,
            R.drawable.hrustyashiebaklazhani,
            R.drawable.ovoshigril,
            R.drawable.tiramisu
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        dbHelper = new DatabaseHelper(this);
        pizzaContainer = findViewById(R.id.pizzaContainer);
        Button btnOrder = findViewById(R.id.btnOrder);

        // Получаем ID существующей брони, если она есть
        existingOrderId = getIntent().getLongExtra("existing_order_id", -1);
        bookingDetails = getIntent().getStringExtra("booking_details");

        for (int i = 0; i < names.length; i++) {
            dishList.add(new pizza(names[i], prices[i], images[i]));
        }

        displayDishes();

        if (btnOrder != null) {
            btnOrder.setText(existingOrderId != -1 ? "Добавить к бронированию" : "Выбрать дату и заказать");
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
            finishOrder("");
        } else {
            // Если просто предзаказ, спрашиваем дату и время
            showDateTimePicker();
        }
    }

    private boolean hasSelectedItems() {
        for (pizza item : dishList) {
            if (item.getCount() > 0) return true;
        }
        return false;
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth, hourOfDay, minute);

                if (hourOfDay < 10 || hourOfDay >= 23) {
                    Toast.makeText(this, "Заказы принимаются только с 10:00 до 23:00", Toast.LENGTH_LONG).show();
                } else if (selected.before(Calendar.getInstance())) {
                    Toast.makeText(this, "Нельзя выбрать прошедшее время", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedDateTime = String.format(Locale.getDefault(), "%02d.%02d.%d %02d:%02d", 
                            dayOfMonth, month + 1, year, hourOfDay, minute);
                    finishOrder(selectedDateTime);
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            timePickerDialog.show();
            
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void finishOrder(String dateTime) {
        StringBuilder dishSummary = new StringBuilder();
        int total = 0;

        for (pizza item : dishList) {
            if (item.getCount() > 0) {
                dishSummary.append(item.getName())
                        .append(" x")
                        .append(item.getCount())
                        .append("\n");
                total += item.getPrice() * item.getCount();
            }
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("username", "Unknown");
        
        String finalDetails;
        if (existingOrderId != -1) {
            finalDetails = (bookingDetails != null ? bookingDetails : "") + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого за еду: " + total + " руб.";
            dbHelper.updateOrderDetails(existingOrderId, "Бронь + Предзаказ", finalDetails);
        } else {
            finalDetails = "Дата и время получения: " + dateTime + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого: " + total + " руб.";
            dbHelper.insertOrder(currentUser, "Гоголь-Моголь", "Предзаказ", finalDetails);
        }

        Intent intent = new Intent(MENUGOGOL.this, ThankYouActivity.class);
        intent.putExtra("order_details", "Ваш заказ (Гоголь-Моголь):\n\n" + finalDetails);
        startActivity(intent);
        finish();
    }

    private void displayDishes() {
        if (pizzaContainer == null) return;
        pizzaContainer.removeAllViews();

        for (int i = 0; i < dishList.size(); i++) {
            pizza item = dishList.get(i);
            String description = descriptions[i];
            
            View itemView = getLayoutInflater().inflate(R.layout.item_pizza, pizzaContainer, false);

            ImageButton imgDish = itemView.findViewById(R.id.imgPizza);
            TextView tvName = itemView.findViewById(R.id.tvPizzaName);
            TextView tvPrice = itemView.findViewById(R.id.tvPizzaPrice);
            TextView tvCount = itemView.findViewById(R.id.tvCount);
            Button btnPlus = itemView.findViewById(R.id.btnPlus);
            Button btnMinus = itemView.findViewById(R.id.btnMinus);

            if (imgDish != null) imgDish.setImageResource(item.getImageResId());
            if (tvName != null) tvName.setText(item.getName());
            if (tvPrice != null) {
                tvPrice.setText("Цена: " + item.getPrice() + " руб.\n" + description);
            }
            if (tvCount != null) tvCount.setText(String.valueOf(item.getCount()));

            if (btnPlus != null) {
                btnPlus.setOnClickListener(v -> {
                    item.incrementCount();
                    tvCount.setText(String.valueOf(item.getCount()));
                });
            }

            if (btnMinus != null) {
                btnMinus.setOnClickListener(v -> {
                    item.decrementCount();
                    tvCount.setText(String.valueOf(item.getCount()));
                });
            }

            pizzaContainer.addView(itemView);
        }
    }
}
