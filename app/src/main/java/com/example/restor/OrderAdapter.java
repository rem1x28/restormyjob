package com.example.restor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        holder.tvDate.setText(order.getDateCreated());

        holder.btnFinish.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFinish(order, position);
            }
        });
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
