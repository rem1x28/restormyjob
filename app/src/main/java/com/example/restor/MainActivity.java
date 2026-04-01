package com.example.restor;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.View;

import androidx.activity.EdgeToEdge;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);

        // 1. Инициализируем список
        restaurantList = new ArrayList<>();

        // 2. Заполняем данные
        restaurantList.add(new Restaurant("DODO Pizza", 4.6, R.drawable.dodopizza, "0.5 км", "Пиццерия"));
        restaurantList.add(new Restaurant("Гоголь-Моголь", 5.0, R.drawable.gogol, "1.2 км", "Ресторан"));
        restaurantList.add(new Restaurant("Арбат", 4.5, R.drawable.arbat, "2.4 км", "Ресторан"));
        restaurantList.add(new Restaurant("Дядя ВАНЯ", 5.0, R.drawable.vanya, "2.7 км", "Ресторан"));

        // 3. Находим RecyclerView
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. Создаём адаптер и привязываем к RecyclerView (ОДИН РАЗ)
        restaurantAdapter = new RestaurantAdapter(this, restaurantList);
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        // 5. Обработка системных отступов (Edge-to-Edge)
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 6. Отключаем shifting mode для BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        disableShiftMode(bottomNav); // Вызываем метод
    }

    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false); // Отключаем shifting mode
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Отдельный метод для работы с пиццами — вынесен из disableShiftMode
    private void setupPizzaRecyclerView() {
        List<pizza> pizzas = Arrays.asList(
                new pizza("Маргарита (30 см.)", 450, R.drawable.pizza_margarita),
                new pizza("Пепперони (30 см.)", 550, R.drawable.pizza_pepperoni),
                new pizza("Четыре сыра (30 см)", 600, R.drawable.pizza_four_cheese),
                new pizza("Мясная (30 см)", 650, R.drawable.pizza_meat),
                new pizza("Вегетарианская (30 см)", 400, R.drawable.pizza_vegetarian)
                // ... остальные пиццы
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        PizzaAdapter adapter = new PizzaAdapter(pizzas);
        recyclerView.setAdapter(adapter);
    }

    // Отдельный метод для расчёта общей стоимости
    private int calculateTotalPrice(List<pizza> pizzas) {
        int total = 0;
        for (pizza pizza : pizzas) {
            total += pizza.getPrice() * pizza.getCount();
        }
        return total;
    }
}