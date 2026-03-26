package com.example.restor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<com.example.restor.Restaurant> restaurantList;
    private Context context;

    public RestaurantAdapter(Context context, List<com.example.restor.Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.example.restor.Restaurant restaurant = restaurantList.get(position);

        // Заполняем текстовые поля данными из модели
        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText("★ " + restaurant.getRating());
        holder.restaurantTypeTextView.setText(restaurant.getRestaurantType());
        holder.rasstTextView.setText(restaurant.getDistance());

        // Устанавливаем изображение
        holder.restaurantImageView.setImageResource(restaurant.getImageRes());

        // Обработка клика на всю карточку
        holder.itemView.setOnClickListener(v -> {
            String name = restaurant.getName();
            Intent intent = null;

            // Определяем, какую Activity запускать в зависимости от названия ресторана
            switch (name) {
                case "DODO Pizza":
                    intent = new Intent(context, dodoactivity.class);
                    break;
                case "Гоголь-Моголь":
                    intent = new Intent(context, gogolactivity.class);
                    break;
                case "Арбат":
                    intent = new Intent(context, Arbatactivity.class);
                    break;
                case "Дядя ВАНЯ":
                    intent = new Intent(context, VanyaActivity.class);
                    break;
            }

            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList != null ? restaurantList.size() : 0;
    }

    // Метод для обновления данных в адаптере
    public void updateData(List<com.example.restor.Restaurant> newList) {
        this.restaurantList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ratingTextView, restaurantTypeTextView, rasstTextView;
        ImageView restaurantImageView;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            nameTextView = itemView.findViewById(R.id.restaurantName);
            ratingTextView = itemView.findViewById(R.id.rating);
            restaurantTypeTextView = itemView.findViewById(R.id.restaurantType);
            rasstTextView = itemView.findViewById(R.id.distance);
            restaurantImageView = itemView.findViewById(R.id.restaurantImage);
        }
    }
}