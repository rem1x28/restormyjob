package com.example.restor;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderFinishListener listener;

    public interface OnOrderFinishListener {
        void onFinish(Order order, int position);
    }

    public OrderAdapter(List<Order> orderList, OnOrderFinishListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvRestorName.setText(order.getRestaurantName());
        holder.tvType.setText(order.getType());
        holder.tvDetails.setText(order.getDetails());
        holder.tvDate.setText("Создано: " + order.getDateCreated());

        // Проверка времени выполнения
        if (isOrderExpired(order.getDetails())) {
            holder.tvType.setText("Завершено");
            holder.tvType.setBackgroundColor(Color.parseColor("#4CAF50")); // Зеленый для завершенных
            holder.tvType.setTextColor(Color.WHITE);
            holder.btnFinish.setText("Удалить");
        } else {
            holder.tvType.setText(order.getType());
            holder.tvType.setBackgroundResource(R.drawable.pink_input_bg);
            holder.tvType.setTextColor(Color.parseColor("#DB7093"));
            holder.btnFinish.setText("Удалить");
        }

        holder.btnFinish.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFinish(order, position);
            }
        });
    }

    private boolean isOrderExpired(String details) {
        try {
            // Ищем дату в формате dd.MM.yyyy HH:mm
            Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})");
            Matcher matcher = pattern.matcher(details);
            if (matcher.find()) {
                String dateStr = matcher.group(1);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                Date orderDate = sdf.parse(dateStr);
                
                if (orderDate != null) {
                    return orderDate.before(new Date());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateData(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestorName, tvType, tvDetails, tvDate;
        Button btnFinish;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestorName = itemView.findViewById(R.id.tvOrderRestorName);
            tvType = itemView.findViewById(R.id.tvOrderType);
            tvDetails = itemView.findViewById(R.id.tvOrderDetails);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            btnFinish = itemView.findViewById(R.id.btnFinishOrder);
        }
    }
}
