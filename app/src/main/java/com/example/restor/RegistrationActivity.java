package com.example.restor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etRegName);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnDoRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(pass)) {
                Toast.makeText(this, "Пароль должен быть не менее 8 символов, содержать заглавную букву, цифру и специальный символ", Toast.LENGTH_LONG).show();
                return;
            }

            if (dbHelper.checkUserExists(name)) {
                Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = dbHelper.insertUser(name, pass);
                if (isInserted) {
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    finish(); // Возвращаемся на экран входа
                } else {
                    Toast.makeText(this, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        return hasUppercase && hasDigit && hasSpecial;
    }
}
