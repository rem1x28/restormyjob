package com.example.restor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
    private LinearLayout profileContainer;
    private LinearLayout ordersContainer;
    private EditText etGlobalSearch;
    private TextView tvUserStatus, tvUserSubStatus, tvEmptyOrders;
    private Button btnRegister, btnEditProfile, btnLogout;
    private ImageView ivUserAvatar;
    private DatabaseHelper dbHelper;
    private int selectedAvatarResId = R.drawable.ic_profile;
    private String selectedAvatarUri = null;

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri.toString();
                    selectedAvatarResId = -1;
                    Toast.makeText(this, "Фото выбрано", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Инициализация UI
        searchContainer = findViewById(R.id.searchContainer);
        profileContainer = findViewById(R.id.profileContainer);
        ordersContainer = findViewById(R.id.ordersContainer);
        etGlobalSearch = findViewById(R.id.etGlobalSearch);
        tvUserStatus = findViewById(R.id.tvUserStatus);
        tvUserSubStatus = findViewById(R.id.tvUserSubStatus);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        btnRegister = findViewById(R.id.btnRegister);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        
        RecyclerView restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        rvOrders = findViewById(R.id.rvOrders);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Настройка RecyclerView для заказов
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(new ArrayList<>(), (order, position) -> {
            // Удаляем из базы данных
            if (dbHelper.deleteOrder(order.getId())) {
                Toast.makeText(this, "Заказ завершен", Toast.LENGTH_SHORT).show();
                loadUserOrders(); // Перезагружаем список
            }
        });
        rvOrders.setAdapter(orderAdapter);

        // Обработка системных отступов
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); 
                return insets;
            });
        }

        setupRestaurants(restaurantRecyclerView);

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) showSearch();
                else if (id == R.id.nav_profile) showProfile();
                else if (id == R.id.nav_orders) showOrders();
                else showRestaurants();
                return true;
            });
        }

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                checkLoginStatus();
                Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            });
        }

        setupSearchLogic();
        checkLoginStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
        if (ordersContainer != null && ordersContainer.getVisibility() == View.VISIBLE) {
            loadUserOrders();
        }
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        int avatarRes = prefs.getInt("avatar", R.drawable.ic_profile);
        String avatarUriStr = prefs.getString("avatar_uri", null);
        
        if (username != null) {
            if (tvUserStatus != null) tvUserStatus.setText(username);
            if (tvUserSubStatus != null) tvUserSubStatus.setText("Активный гурман");
            
            if (ivUserAvatar != null) {
                if (avatarUriStr != null) {
                    ivUserAvatar.setImageURI(Uri.parse(avatarUriStr));
                } else {
                    ivUserAvatar.setImageResource(avatarRes);
                }
            }
            
            if (btnRegister != null) btnRegister.setVisibility(View.GONE);
            if (btnEditProfile != null) btnEditProfile.setVisibility(View.VISIBLE);
            if (btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
        } else {
            if (tvUserStatus != null) tvUserStatus.setText("Вы не авторизованы");
            if (tvUserSubStatus != null) tvUserSubStatus.setText("Войдите, чтобы сохранять заказы");
            if (ivUserAvatar != null) ivUserAvatar.setImageResource(R.drawable.ic_profile);
            if (btnRegister != null) btnRegister.setVisibility(View.VISIBLE);
            if (btnEditProfile != null) btnEditProfile.setVisibility(View.GONE);
            if (btnLogout != null) btnLogout.setVisibility(View.GONE);
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etNewName = dialogView.findViewById(R.id.etEditName);
        ImageView pick1 = dialogView.findViewById(R.id.ivPick1);
        ImageView pick2 = dialogView.findViewById(R.id.ivPick2);
        ImageView pick3 = dialogView.findViewById(R.id.ivPick3);
        ImageView pick4 = dialogView.findViewById(R.id.ivPick4);
        ImageView pick5 = dialogView.findViewById(R.id.ivPick5);
        ImageView pick6 = dialogView.findViewById(R.id.ivPick6);
        ImageView pick7 = dialogView.findViewById(R.id.ivPick7);
        ImageView pick8 = dialogView.findViewById(R.id.ivPick8);
        ImageView pick9 = dialogView.findViewById(R.id.ivPick9);
        ImageView pick10 = dialogView.findViewById(R.id.ivPick10);
        Button btnPickGallery = dialogView.findViewById(R.id.btnPickGallery);
        Button btnSave = dialogView.findViewById(R.id.btnEditSave);
        Button btnCancel = dialogView.findViewById(R.id.btnEditCancel);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String oldName = prefs.getString("username", "");
        etNewName.setText(oldName);
        
        selectedAvatarResId = prefs.getInt("avatar", R.drawable.ic_profile);
        selectedAvatarUri = prefs.getString("avatar_uri", null);

        View.OnClickListener pickListener = v -> {
            pick1.setAlpha(0.5f); pick2.setAlpha(0.5f); pick3.setAlpha(0.5f);
            pick4.setAlpha(0.5f); pick5.setAlpha(0.5f); pick6.setAlpha(0.5f);
            pick7.setAlpha(0.5f); pick8.setAlpha(0.5f); pick9.setAlpha(0.5f);
            pick10.setAlpha(0.5f);
            v.setAlpha(1.0f);
            selectedAvatarUri = null; 
            int id = v.getId();
            if (id == R.id.ivPick1) selectedAvatarResId = R.drawable.avatarka1;
            else if (id == R.id.ivPick2) selectedAvatarResId = R.drawable.avatarka2;
            else if (id == R.id.ivPick3) selectedAvatarResId = R.drawable.avatarka3;
            else if (id == R.id.ivPick4) selectedAvatarResId = R.drawable.avatarka4;
            else if (id == R.id.ivPick5) selectedAvatarResId = R.drawable.avatarka5;
            else if (id == R.id.ivPick6) selectedAvatarResId = R.drawable.avatarka6;
            else if (id == R.id.ivPick7) selectedAvatarResId = R.drawable.avatarka7;
            else if (id == R.id.ivPick8) selectedAvatarResId = R.drawable.avatarka8;
            else if (id == R.id.ivPick9) selectedAvatarResId = R.drawable.avatarka9;
            else if (id == R.id.ivPick10) selectedAvatarResId = R.drawable.avatarka10;
        };

        pick1.setOnClickListener(pickListener);
        pick2.setOnClickListener(pickListener);
        pick3.setOnClickListener(pickListener);
        pick4.setOnClickListener(pickListener);
        pick5.setOnClickListener(pickListener);
        pick6.setOnClickListener(pickListener);
        pick7.setOnClickListener(pickListener);
        pick8.setOnClickListener(pickListener);
        pick9.setOnClickListener(pickListener);
        pick10.setOnClickListener(pickListener);

        if (btnPickGallery != null) {
            btnPickGallery.setOnClickListener(v -> mGetContent.launch("image/*"));
        }

        btnSave.setOnClickListener(v -> {
            String newName = etNewName.getText().toString().trim();
            if (!newName.isEmpty()) {
                if (!newName.equals(oldName)) {
                    dbHelper.updateUserName(oldName, newName);
                }
                
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", newName);
                if (selectedAvatarUri != null) {
                    editor.putString("avatar_uri", selectedAvatarUri);
                    editor.putInt("avatar", -1);
                } else {
                    editor.putInt("avatar", selectedAvatarResId);
                    editor.remove("avatar_uri");
                }
                editor.apply();
                
                checkLoginStatus();
                dialog.dismiss();
                Toast.makeText(this, "Профиль обновлен", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSearch() {
        searchContainer.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);
        ordersContainer.setVisibility(View.GONE);
        findViewById(R.id.restaurantRecyclerView).setVisibility(View.VISIBLE);
    }

    private void showProfile() {
        searchContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.VISIBLE);
        ordersContainer.setVisibility(View.GONE);
        findViewById(R.id.restaurantRecyclerView).setVisibility(View.GONE);
        checkLoginStatus();
    }

    private void showOrders() {
        searchContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        ordersContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.restaurantRecyclerView).setVisibility(View.GONE);
        loadUserOrders();
    }

    private void showRestaurants() {
        searchContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        ordersContainer.setVisibility(View.GONE);
        findViewById(R.id.restaurantRecyclerView).setVisibility(View.VISIBLE);
    }

    private void loadUserOrders() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("username", null);

        if (currentUser == null) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("Войдите в профиль, чтобы увидеть свои заказы");
            rvOrders.setVisibility(View.GONE);
            return;
        }

        List<Order> userOrders = new ArrayList<>();
        Cursor cursor = dbHelper.getUserOrders(currentUser);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
                String restor = cursor.getString(cursor.getColumnIndexOrThrow("RESTAURANT_NAME"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("TYPE"));
                String details = cursor.getString(cursor.getColumnIndexOrThrow("DETAILS"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE_CREATED"));
                userOrders.add(new Order(id, restor, type, details, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (userOrders.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("У вас пока нет активных заказов");
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            orderAdapter.updateData(userOrders);
        }
    }

    private void setupRestaurants(RecyclerView rv) {
        allRestaurants.clear();
        allRestaurants.add(new Restaurant("DODO Pizza", 4.6, R.drawable.dodopizza, "0.5 км", "Пиццерия", 
                Arrays.asList("Маргарита", "Пепперони", "Четыре сыра")));
        allRestaurants.add(new Restaurant("Гоголь-Моголь", 5.0, R.drawable.gogol, "1.2 км", "Ресторан", 
                Arrays.asList("Котлета по-киевски", "Баклажаны", "Тирамису")));
        allRestaurants.add(new Restaurant("Арбат", 4.5, R.drawable.arbat, "2.4 км", "Ресторан", 
                Arrays.asList("Борщ", "Обед сытный", "Шашлык")));
        allRestaurants.add(new Restaurant("Дядя ВАНЯ", 5.0, R.drawable.vanya, "2.7 км", "Ресторан", 
                Arrays.asList("Бургер", "Фондан", "Морская закуска")));

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, new ArrayList<>(allRestaurants));
        rv.setAdapter(adapter);
    }

    private void setupSearchLogic() {
        if (etGlobalSearch != null) {
            etGlobalSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterRestaurants(s.toString()); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterRestaurants(String query) {
        List<Restaurant> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (Restaurant r : allRestaurants) {
            if (r.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(r);
                continue;
            }
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
