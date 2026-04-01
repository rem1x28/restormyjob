package com.example.restor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class zakazpizzaActivity extends AppCompatActivity {

    private LinearLayout pizzaContainer;
    private Button btnOrder;
    private List<pizza> selectedPizzas = new ArrayList<>();

    private int[] pizzaImages = {
            R.drawable.pizza_margarita,
            R.drawable.pizza_pepperoni,
            R.drawable.pizza_four_cheese,
            R.drawable.meat_pizza,
            R.drawable.pizza_vegetarian
    };

    private int[] pizzaPrices = {450, 550, 600, 650, 400};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazpizza);

        btnOrder = findViewById(R.id.btnOrder);
        pizzaContainer = findViewById(R.id.pizzaContainer);

        String[] pizzaNames = getResources().getStringArray(R.array.restor);

        if (pizzaContainer != null) {
            for (int i = 0; i < pizzaNames.length; i++) {
                final String name = pizzaNames[i];
                final int imageResId = (i < pizzaImages.length) ? pizzaImages[i] : R.drawable.ic_launcher_background;
                final int price = (i < pizzaPrices.length) ? pizzaPrices[i] : 500;

                pizza item = new pizza(name, price, imageResId);
                selectedPizzas.add(item);

                addPizzaToLayout(item);
            }
        }

        if (btnOrder != null) {
            btnOrder.setText("Оформить предзаказ");
            btnOrder.setOnClickListener(v -> finishPreOrder());
        }
    }

    private void addPizzaToLayout(pizza item) {
        View pizzaCard = getLayoutInflater().inflate(R.layout.item_pizza, pizzaContainer, false);

        ImageView img = pizzaCard.findViewById(R.id.imgPizza);
        TextView title = pizzaCard.findViewById(R.id.tvPizzaName);
        TextView tvPrice = pizzaCard.findViewById(R.id.tvPizzaPrice);
        TextView tvCount = pizzaCard.findViewById(R.id.tvCount);
        Button btnPlus = pizzaCard.findViewById(R.id.btnPlus);
        Button btnMinus = pizzaCard.findViewById(R.id.btnMinus);

        img.setImageResource(item.getImageResId());
        title.setText(item.getName());
        tvPrice.setText("Цена: " + item.getPrice() + " руб.");
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

    private void finishPreOrder() {
        StringBuilder summary = new StringBuilder("Ваш предзаказ:\n");
        boolean hasItems = false;
        int total = 0;

        for (pizza p : selectedPizzas) {
            if (p.getCount() > 0) {
                summary.append(p.getName()).append(" x ").append(p.getCount()).append("\n");
                total += p.getPrice() * p.getCount();
                hasItems = true;
            }
        }

        if (!hasItems) {
            Toast.makeText(this, "Выберите хотя бы одно блюдо", Toast.LENGTH_SHORT).show();
            return;
        }

        summary.append("\nИтого: ").append(total).append(" руб.");

        // Переходим на экран благодарности
        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_details", summary.toString());
        startActivity(intent);
        finish();
    }
}
