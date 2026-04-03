package com.example.restor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class zakazvanya extends AppCompatActivity {

    private LinearLayout container;
    private Button btnOrder;
    private EditText etSearch;
    private List<pizza> allItems = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private long existingOrderId = -1;
    private String bookingDetails = "";

    private String[] names = {
            "Салат Свежесть",
            "Морская закуска",
            "Black burger",
            "Шоколадный фондан"
    };

    private String[] descriptions = {
            "Легкий витаминный салат из свежих огурцов, томатов и зелени с оливковым маслом.",
            "Ассорти из морепродуктов: тигровые креветки, кальмары в хрустящей панировке в чесночном соусе.",
            "Сочная говяжья котлета, бекон, сыр чеддер и фирменный соус на черной булке с кунжутом.",
            "Изысканный десерт с тающей шоколадной сердцевиной. Подается с шариком ванильного мороженого."
    };

    private int[] prices = {250, 480, 550, 350};

    private int[] images = {
            R.drawable.salatsvexhest,
            R.drawable.morskayazakuska,
            R.drawable.blacburger,
            R.drawable.fondan
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.pizzaContainer);
        btnOrder = findViewById(R.id.btnOrder);
        etSearch = findViewById(R.id.etSearch);

        existingOrderId = getIntent().getLongExtra("existing_order_id", -1);
        bookingDetails = getIntent().getStringExtra("booking_details");

        for (int i = 0; i < names.length; i++) {
            allItems.add(new pizza(names[i], prices[i], images[i]));
        }

        updateList("");

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateList(s.toString()); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        if (btnOrder != null) {
            btnOrder.setText(existingOrderId != -1 ? "Добавить к бронированию" : "Выбрать дату и заказать");
            btnOrder.setOnClickListener(v -> handleOrderClick());
        }
    }

    private void handleOrderClick() {
        if (!hasSelectedItems()) {
            Toast.makeText(this, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingOrderId != -1) {
            finishOrder("");
        } else {
            showDateTimePicker();
        }
    }

    private boolean hasSelectedItems() {
        for (pizza p : allItems) {
            if (p.getCount() > 0) return true;
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

    private void updateList(String query) {
        if (container == null) return;
        container.removeAllViews();
        for (int i = 0; i < allItems.size(); i++) {
            pizza item = allItems.get(i);
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                addItemToLayout(item, descriptions[i]);
            }
        }
    }

    private void addItemToLayout(pizza item, String description) {
        View card = getLayoutInflater().inflate(R.layout.item_pizza, container, false);
        ImageView img = card.findViewById(R.id.imgPizza);
        TextView title = card.findViewById(R.id.tvPizzaName);
        TextView tvPrice = card.findViewById(R.id.tvPizzaPrice);
        TextView tvCount = card.findViewById(R.id.tvCount);
        Button btnPlus = card.findViewById(R.id.btnPlus);
        Button btnMinus = card.findViewById(R.id.btnMinus);

        img.setImageResource(item.getImageResId());
        title.setText(item.getName());
        tvPrice.setText("Цена: " + item.getPrice() + " руб.\n" + description);
        tvCount.setText(String.valueOf(item.getCount()));

        btnPlus.setOnClickListener(v -> { item.incrementCount(); tvCount.setText(String.valueOf(item.getCount())); });
        btnMinus.setOnClickListener(v -> { item.decrementCount(); tvCount.setText(String.valueOf(item.getCount())); });
        container.addView(card);
    }

    private void finishOrder(String time) {
        StringBuilder dishSummary = new StringBuilder();
        int total = 0;
        for (pizza p : allItems) {
            if (p.getCount() > 0) {
                dishSummary.append(p.getName()).append(" x ").append(p.getCount()).append("\n");
                total += p.getPrice() * p.getCount();
            }
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("username", "Unknown");
        String finalDetails;

        if (existingOrderId != -1) {
            finalDetails = (bookingDetails != null ? bookingDetails : "") + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого за еду: " + total + " руб.";
            dbHelper.updateOrderDetails(existingOrderId, "Бронь + Предзаказ", finalDetails);
        } else {
            finalDetails = "Дата и время получения: " + time + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого: " + total + " руб.";
            dbHelper.insertOrder(currentUser, "Дядя ВАНЯ", "Предзаказ", finalDetails);
        }

        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_details", "Ваш заказ обновлен (Дядя ВАНЯ):\n\n" + finalDetails);
        startActivity(intent);
        finish();
    }
}
