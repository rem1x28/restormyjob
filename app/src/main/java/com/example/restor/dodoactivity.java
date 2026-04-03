package com.example.restor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class dodoactivity extends AppCompatActivity {

    private Button btnOrder;
    private ImageView imageView;
    private Button btnReserve;
    private DatabaseHelper dbHelper;
    private long lastOrderId = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodoactivity2);

        dbHelper = new DatabaseHelper(this);

        // Инициализируем элементы
        btnOrder = findViewById(R.id.btnOrder);
        imageView = findViewById(R.id.restaurantImage);
        btnReserve = findViewById(R.id.btnReserve);

        // Слушатель для кнопки бронирования
        btnReserve.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                showBookingDialog(v);
            } else {
                showLoginPrompt();
            }
        });

        // Слушатель для кнопки перехода к заказу
        btnOrder.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                goToOrderPage(v);
            } else {
                showLoginPrompt();
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("username", null) != null;
    }

    private void showLoginPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Требуется авторизация")
                .setMessage("Чтобы забронировать столик или сделать предзаказ, пожалуйста, войдите в свой профиль.")
                .setPositiveButton("Войти", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void goToOrderPage(View view) {
        Intent intent = new Intent(dodoactivity.this, zakazpizzaActivity.class);
        startActivity(intent);
    }

    public void showBookingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etGuests = dialogView.findViewById(R.id.etGuests);
        EditText etTableInfo = dialogView.findViewById(R.id.etTableInfo);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view2, hourOfDay, minute) -> {
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute);
                    
                    if (hourOfDay < 10 || hourOfDay >= 23) {
                        Toast.makeText(this, "Бронирование доступно только с 10:00 до 23:00", Toast.LENGTH_LONG).show();
                    } else if (selectedDateTime.before(Calendar.getInstance())) {
                        Toast.makeText(this, "Нельзя выбрать прошедшую дату и время", Toast.LENGTH_SHORT).show();
                    } else {
                        String formatted = String.format(Locale.getDefault(), "%02d.%02d.%d %02d:%02d", 
                                dayOfMonth, month + 1, year, hourOfDay, minute);
                        etDate.setText(formatted);
                    }
                }, 12, 0, true);
                timePickerDialog.show();
                
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        btnConfirm.setOnClickListener(v -> {
            String dateText = etDate.getText().toString().trim();
            String guestsStr = etGuests.getText().toString().trim();
            String tableInfo = etTableInfo.getText().toString().trim();

            if (dateText.isEmpty()) {
                Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show();
                return;
            }

            if (guestsStr.isEmpty()) {
                Toast.makeText(this, "Укажите количество гостей", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int guestsCount = Integer.parseInt(guestsStr);
                if (guestsCount < 1 || guestsCount > 15) {
                    Toast.makeText(this, "Количество гостей должно быть от 1 до 15", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String currentUser = prefs.getString("username", "Unknown");
                
                String details = "Дата: " + dateText + ", Гостей: " + guestsCount;
                if (!tableInfo.isEmpty()) details += ", Пожелания: " + tableInfo;

                lastOrderId = dbHelper.insertOrder(currentUser, "DODO Pizza", "Бронирование", details);

                dialog.dismiss();
                showPreOrderPrompt(details);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введите корректное число гостей", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showPreOrderPrompt(String bookingDetails) {
        new AlertDialog.Builder(this)
                .setTitle("Бронирование успешно!")
                .setMessage("Желаете добавить к бронированию предзаказ блюд?")
                .setPositiveButton("Да, выбрать блюда", (dialog, which) -> {
                    Intent intent = new Intent(dodoactivity.this, zakazpizzaActivity.class);
                    intent.putExtra("existing_order_id", lastOrderId);
                    intent.putExtra("booking_details", bookingDetails);
                    startActivity(intent);
                })
                .setNegativeButton("Нет, спасибо", (dialog, which) -> {
                    Toast.makeText(this, "Ждем вас!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
