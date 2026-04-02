package com.example.restor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etLoginName);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnDoLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvError = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (dbHelper.validateUser(name, pass)) {
                // Сохраняем имя вошедшего пользователя
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit().putString("username", name).apply();
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                tvError.setVisibility(View.VISIBLE);
            }
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
    }
}
