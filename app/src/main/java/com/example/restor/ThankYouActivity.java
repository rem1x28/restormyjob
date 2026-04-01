package com.example.restor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        TextView tvDetails = findViewById(R.id.tvOrderDetails);
        Button btnBack = findViewById(R.id.btnBackToMain);

        String orderDetails = getIntent().getStringExtra("order_details");
        if (orderDetails != null) {
            tvDetails.setText(orderDetails);
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
