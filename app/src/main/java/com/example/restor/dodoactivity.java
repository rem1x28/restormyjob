package com.example.restor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class dodoactivity extends AppCompatActivity {

    private Button btnOrder;
    private ImageView imageView;
    private Button btnReserve;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodoactivity2);

        // Инициализируем элементы
        btnOrder = findViewById(R.id.btnOrder);
        imageView = findViewById(R.id.restaurantImage);
        btnReserve = findViewById(R.id.btnReserve);

        // Слушатель для кнопки бронирования
        btnReserve.setOnClickListener(this::showBookingDialog);

        // Слушатель для кнопки перехода к заказу
        btnOrder.setOnClickListener(this::goToOrderPage);
    }

    public void goToOrderPage(View view) {
        Intent intent = new Intent(dodoactivity.this, zakazpizzaActivity.class);
        startActivity(intent);
    }

    public void showBookingDialog(View view) {
        // 1. Создаём строитель диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Бронирование столика");

        // 2. Подключаем разметку
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);

        // Находим поля внутри окна
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etGuests = dialogView.findViewById(R.id.etGuests);
        EditText etTableInfo = dialogView.findViewById(R.id.etTableInfo);

        // 3. Добавляем кнопки (PositiveButton без обработчика, чтобы настроить его позже)
        builder.setPositiveButton("Забронировать", null);
        builder.setNegativeButton("Отмена", (d, which) -> d.dismiss());

        // Создаём и показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();

        // Назначаем обработчик после show(), чтобы диалог не закрывался при ошибках валидации
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String guestsStr = etGuests.getText().toString().trim();
            String tableInfo = etTableInfo.getText().toString().trim();

            if (date.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите дату бронирования", Toast.LENGTH_SHORT).show();
                etDate.requestFocus();
                return;
            }

            if (guestsStr.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, укажите количество гостей", Toast.LENGTH_SHORT).show();
                etGuests.requestFocus();
                return;
            }

            if (tableInfo.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, укажите информацию о столике", Toast.LENGTH_SHORT).show();
                etTableInfo.requestFocus();
                return;
            }

            try {
                int guestsCount = Integer.parseInt(guestsStr);
                if (guestsCount < 1 || guestsCount > 20) {
                    Toast.makeText(this, "Количество гостей должно быть от 1 до 20", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Успешное завершение бронирования
                dialog.dismiss();
                showPreOrderPrompt();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введите корректное число гостей", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPreOrderPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Бронирование успешно!")
                .setMessage("Желаете сделать предзаказ блюд?")
                .setPositiveButton("Да, выбрать блюда", (dialog, which) -> {
                    Intent intent = new Intent(dodoactivity.this, zakazpizzaActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Нет, спасибо", (dialog, which) -> {
                    Toast.makeText(this, "Ждем вас!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
