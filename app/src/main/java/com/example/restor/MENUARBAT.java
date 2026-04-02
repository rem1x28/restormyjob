package com.example.restor;

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

public class MENUARBAT extends AppCompatActivity {

    private LinearLayout container;
    private List<pizza> dishList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private long existingOrderId = -1;
    private String bookingDetails = "";

    private String[] names = {
            "Борщ русский",
            "Обед сытный",
            "Селедка под шубой",
            "Шашлык из свинины",
            "Тарталетки с семгой"
    };

    private String[] descriptions = {
            "Традиционный наваристый борщ на говяжьем бульоне со свежей капустой и свеклой. Подается со сметаной и зеленью.",
            "Комплексный обед: сочная мясная котлета, гарнир из картофельного пюре и легкий сезонный салат.",
            "Классический слоеный салат с сельдью пряного посола, отварными овощами и домашним майонезом.",
            "Нежные кусочки свиной шейки, маринованные в специях и обжаренные на углях. Подается с маринованным луком.",
            "Миниатюрные песочные корзиночки с нежным сливочным сыром и ломтиками слабосоленой семги."
    };

    private int[] prices = {420, 550, 380, 680, 450};

    private int[] images = {
            R.drawable.borshrusskiy,
            R.drawable.obedsitniy,
            R.drawable.seledkapodshuboy,
            R.drawable.shashliksvinina,
            R.drawable.tortaletkissemgoy
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.pizzaContainer);
        Button btnOrder = findViewById(R.id.btnOrder);
        TextView tvTitle = findViewById(R.id.tvMenuTitle);

        if (tvTitle != null) {
            tvTitle.setText("Меню Арбат");
        }

        existingOrderId = getIntent().getLongExtra("existing_order_id", -1);
        bookingDetails = getIntent().getStringExtra("booking_details");

        dishList.clear();
        for (int i = 0; i < names.length; i++) {
            dishList.add(new pizza(names[i], prices[i], images[i]));
        }

        displayDishes();

        if (btnOrder != null) {
            btnOrder.setText(existingOrderId != -1 ? "Добавить к бронированию" : "Выбрать время и заказать");
            btnOrder.setOnClickListener(v -> handleOrderClick());
        }
    }

    private void handleOrderClick() {
        if (!hasSelectedItems()) {
            Toast.makeText(this, "Пожалуйста, выберите блюда", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingOrderId != -1) {
            finishOrder("");
        } else {
            showTimePicker();
        }
    }

    private boolean hasSelectedItems() {
        for (pizza item : dishList) {
            if (item.getCount() > 0) return true;
        }
        return false;
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            finishOrder(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void finishOrder(String time) {
        StringBuilder dishSummary = new StringBuilder();
        int total = 0;
        for (pizza item : dishList) {
            if (item.getCount() > 0) {
                dishSummary.append(item.getName()).append(" x").append(item.getCount()).append("\n");
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
            finalDetails = "Время получения: " + time + "\n\nБлюда:\n" + dishSummary.toString() + "\nИтого: " + total + " руб.";
            dbHelper.insertOrder(currentUser, "Арбат", "Предзаказ", finalDetails);
        }

        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_details", "Ваш заказ обновлен (Арбат):\n\n" + finalDetails);
        startActivity(intent);
        finish();
    }

    private void displayDishes() {
        if (container == null) return;
        container.removeAllViews();

        for (int i = 0; i < dishList.size(); i++) {
            pizza item = dishList.get(i);
            String desc = descriptions[i];
            
            View itemView = getLayoutInflater().inflate(R.layout.item_pizza, container, false);

            ImageButton img = itemView.findViewById(R.id.imgPizza);
            TextView tvName = itemView.findViewById(R.id.tvPizzaName);
            TextView tvPrice = itemView.findViewById(R.id.tvPizzaPrice);
            TextView tvCount = itemView.findViewById(R.id.tvCount);
            Button btnPlus = itemView.findViewById(R.id.btnPlus);
            Button btnMinus = itemView.findViewById(R.id.btnMinus);

            if (img != null) img.setImageResource(item.getImageResId());
            if (tvName != null) tvName.setText(item.getName());
            if (tvPrice != null) {
                tvPrice.setText("Цена: " + item.getPrice() + " руб.\n" + desc);
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

            container.addView(itemView);
        }
    }
}
