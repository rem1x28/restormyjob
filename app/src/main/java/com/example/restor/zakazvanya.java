package com.example.restor;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.List;

public class zakazvanya extends AppCompatActivity {

    private LinearLayout container;
    private Button btnOrder;
    private EditText etSearch;
    private List<pizza> allItems = new ArrayList<>();

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

        container = findViewById(R.id.pizzaContainer);
        btnOrder = findViewById(R.id.btnOrder);
        etSearch = findViewById(R.id.etSearch);

        // Инициализируем список всех блюд
        for (int i = 0; i < names.length; i++) {
            allItems.add(new pizza(names[i], prices[i], images[i]));
        }

        // Первоначальное отображение всех блюд
        updateList("");

        // Слушатель для поиска
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateList(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnOrder != null) {
            btnOrder.setText("Оформить предзаказ");
            btnOrder.setOnClickListener(v -> finishOrder());
        }
    }

    private void updateList(String query) {
        if (container == null) return;
        container.removeAllViews();

        for (int i = 0; i < allItems.size(); i++) {
            pizza item = allItems.get(i);
            // Если название блюда содержит текст из поиска (игнорируя регистр)
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

        btnPlus.setOnClickListener(v -> {
            item.incrementCount();
            tvCount.setText(String.valueOf(item.getCount()));
        });

        btnMinus.setOnClickListener(v -> {
            item.decrementCount();
            tvCount.setText(String.valueOf(item.getCount()));
        });

        container.addView(card);
    }

    private void finishOrder() {
        StringBuilder sb = new StringBuilder("Предзаказ (Дядя ВАНЯ):\n");
        int total = 0;
        boolean hasItems = false;

        for (pizza p : allItems) {
            if (p.getCount() > 0) {
                sb.append(p.getName()).append(" x ").append(p.getCount()).append("\n");
                total += p.getPrice() * p.getCount();
                hasItems = true;
            }
        }

        if (!hasItems) {
            Toast.makeText(this, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
            return;
        }

        sb.append("\nИтого: ").append(total).append(" руб.");

        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_details", sb.toString());
        startActivity(intent);
        finish();
    }
}
