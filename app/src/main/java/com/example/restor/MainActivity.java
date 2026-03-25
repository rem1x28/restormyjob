package com.example.restor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);


        // 1. Инициализируем список СРАЗУ
        restaurantList = new ArrayList<>();

        // 2. Находим RecyclerView
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Заполняем данные
        restaurantList.add(new Restaurant("DODO Pizza", 4.6, R.drawable.dodopizza, "0.5 км", "Пиццерия"));
        restaurantList.add(new Restaurant("Гоголь-Моголь", 5.0, R.drawable.gogol, "1.2 км", "Ресторан"));
        restaurantList.add(new Restaurant("Арбат", 4.5, R.drawable.arbat, "2.4 км", "Ресторан"));
        restaurantList.add(new Restaurant("Дядя ВАНЯ", 5.0, R.drawable.vanya,"2.7км", "Ресторан"));
        // 4. Устанавливаем адаптер
        restaurantAdapter = new RestaurantAdapter(restaurantList);
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        // 5. Обработка системных отступов (Edge-to-Edge)
        // ВАЖНО: Убедитесь, что в activity_main.xml у корневого Layout стоит android:id="@+id/main"
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}