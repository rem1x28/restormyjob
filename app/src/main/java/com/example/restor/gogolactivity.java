package com.example.restor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class gogolactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gogolactivity);

        Button btnOrder = findViewById(R.id.btnOrder);
        Button btnReserve = findViewById(R.id.btnReserve);

        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {
                Intent intent = new Intent(gogolactivity.this, MENUGOGOL.class);
                startActivity(intent);
            });
        }

        if (btnReserve != null) {
            btnReserve.setOnClickListener(this::showBookingDialog);
        }
    }

    public void showBookingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Бронирование столика");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);

        EditText etDate = dialogView.findViewById(R.id.etDate);

        builder.setPositiveButton("Забронировать", (dialog, which) -> {
            if (!etDate.getText().toString().isEmpty()) {
                showPreOrderPrompt();
            } else {
                Toast.makeText(this, "Введите дату", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showPreOrderPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Бронирование успешно!")
                .setMessage("Желаете сделать предзаказ блюд?")
                .setPositiveButton("Да, выбрать блюда", (dialog, which) -> {
                    Intent intent = new Intent(gogolactivity.this, MENUGOGOL.class);
                    startActivity(intent);
                })
                .setNegativeButton("Нет, спасибо", (dialog, which) -> {
                    Toast.makeText(this, "Ждем вас!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
