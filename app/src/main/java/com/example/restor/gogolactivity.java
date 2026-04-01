package com.example.restor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Это важно для работы (View view)
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class gogolactivity extends AppCompatActivity {

    private TextView textView;
    private Button btnOrder;
    private ImageView imageView;
    private Button btnReserve;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gogolactivity);

        // Инициализируем элементы
        btnOrder = findViewById(R.id.btnOrder);
        imageView = findViewById(R.id.restaurantImage);
        btnReserve = findViewById(R.id.btnReserve);
        btnReserve.setOnClickListener(this::showBookingDialog);

    }
    // ВСТАВЛЯЙ МЕТОД СЮДА (после закрывающей скобки onCreate)
    public void goToOrderPage(View view) {
        Intent intent = new Intent(gogolactivity.this, zakazpizzaActivity.class);
        startActivity(intent);
    }

    public void showBookingDialog(View view) {
        // 1. Создаём строитель диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Бронирование столика");

        // 2. Подключаем нашу созданную разметку
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);

        // Находим поля внутри окна, чтобы потом прочитать из них текст
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etGuests = dialogView.findViewById(R.id.etGuests);
        EditText etTableInfo = dialogView.findViewById(R.id.etTableInfo);

        // Сохраняем диалог для доступа к нему в обработчике кнопки
        AlertDialog dialog = builder.create();

        // 3. Добавляем кнопки (без немедленного назначения обработчика)
        builder.setPositiveButton("Забронировать", null); // null — пока без обработчика
        builder.setNegativeButton("Отмена", (d, which) -> d.dismiss());

        // Устанавливаем обработчик после показа диалога
        dialog.setOnShowListener(d -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                String date = etDate.getText().toString().trim();
                String guestsStr = etGuests.getText().toString().trim();
                String tableInfo = etTableInfo.getText().toString().trim();

                // Проверка на пустые поля
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
                    Toast.makeText(this, "Пожалуйста, укажите дополнительную информацию о столике", Toast.LENGTH_SHORT).show();
                    etTableInfo.requestFocus();
                    return;
                }

                // Проверка и преобразование количества гостей
                int guestsCount;
                try {
                    guestsCount = Integer.parseInt(guestsStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Количество гостей должно быть числом", Toast.LENGTH_SHORT).show();
                    etGuests.requestFocus();
                    return;
                }

                // Ограничение на количество гостей (например, от 1 до 20)
                if (guestsCount < 1) {
                    Toast.makeText(this, "Количество гостей должно быть не менее 1", Toast.LENGTH_SHORT).show();
                    etGuests.requestFocus();
                    return;
                }

                if (guestsCount > 20) {
                    Toast.makeText(this, "Максимальное количество гостей — 20", Toast.LENGTH_SHORT).show();
                    etGuests.requestFocus();
                    return;
                }

                // Если все проверки пройдены — показываем сообщение об успешном бронировании
                String info = "Забронировано на: " + date +
                        "\nГостей: " + guestsCount +
                        "\nДополнительная информация: " + tableInfo;

                Toast.makeText(this, info, Toast.LENGTH_LONG).show();
                dialog.dismiss(); // Закрываем диалог после успешного бронирования
            });
        });

        // 4. Показываем окно
        dialog.show();
    }
}
