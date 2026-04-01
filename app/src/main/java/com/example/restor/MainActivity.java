package com.example.restor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Restaurant> allRestaurants = new ArrayList<>();
    private RestaurantAdapter adapter;
    private LinearLayout searchContainer;
    private EditText etGlobalSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Инициализация UI
        searchContainer = findViewById(R.id.searchContainer);
        etGlobalSearch = findViewById(R.id.etGlobalSearch);
        RecyclerView restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 1. Подготовка данных с блюдами
        allRestaurants.add(new Restaurant("DODO Pizza", 4.6, R.drawable.dodopizza, "0.5 км", "Пиццерия",
                Arrays.asList("Маргарита", "Пепперони", "Четыре сыра", "Мясная", "Вегетарианская")));
        
        allRestaurants.add(new Restaurant("Гоголь-Моголь", 5.0, R.drawable.gogol, "1.2 км", "Ресторан",
                Arrays.asList("Паста Карбонара", "Сырники", "Фритата", "Песто", "Греческий салат")));
        
        allRestaurants.add(new Restaurant("Арбат", 4.5, R.drawable.arbat, "2.4 км", "Ресторан",
                Arrays.asList("Стейк Рибай", "Цезарь", "Борщ", "Оливье")));
        
        allRestaurants.add(new Restaurant("Дядя ВАНЯ", 5.0, R.drawable.vanya, "2.7 км", "Ресторан",
                Arrays.asList("Салат Свежесть", "Морская закуска", "Black burger", "Шоколадный фондан")));

        // 2. Настройка RecyclerView
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, new ArrayList<>(allRestaurants));
        restaurantRecyclerView.setAdapter(adapter);

        // 3. Обработка системных отступов
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 4. Логика поиска
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
                    searchContainer.setVisibility(View.VISIBLE);
                    return true;
                } else {
                    searchContainer.setVisibility(View.GONE);
                    etGlobalSearch.setText(""); // Очищаем поиск при уходе с вкладки
                    return true;
                }
            });
        }

        if (etGlobalSearch != null) {
            etGlobalSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterRestaurants(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterRestaurants(String query) {
        if (query.isEmpty()) {
            adapter.updateData(new ArrayList<>(allRestaurants));
            return;
        }

        List<Restaurant> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (Restaurant r : allRestaurants) {
            // Ищем по названию ресторана
            if (r.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(r);
                continue;
            }
            
            // Ищем по блюдам в этом ресторане
            for (String dish : r.getDishes()) {
                if (dish.toLowerCase().contains(lowerQuery)) {
                    filteredList.add(r);
                    break;
                }
            }
        }
        adapter.updateData(filteredList);
    }
}
