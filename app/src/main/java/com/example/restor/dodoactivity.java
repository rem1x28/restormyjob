package com.example.restor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button btnOrder;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Указываем XML-разметку

        // Инициализируем элементы интерфейса
        Button = findViewById(R.id.btnOrder);

        imageView = findViewById(R.id.imageView);

        // Устанавливаем обработчик нажатия на кнопку
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Кнопка нажата!", Toast.LENGTH_SHORT).show();
                textView.setText("Текст изменён!"); // Меняем текст TextView
            }
        });
    }
}