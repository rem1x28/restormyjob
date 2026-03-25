package com.example.restor;

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

    public RestaurantAdapter(List<com.example.restor.Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.example.restor.Restaurant restaurant = restaurantList.get(position);

        // Заполняем текстовые поля данными из модели
        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText("★ " + restaurant.getRating()); // Добавил звездочку для красоты
        holder.restaurantImageView.setImageResource(restaurant.getImageRes());

        // ВЫВОДИМ РАССТОЯНИЕ И ТИП (то, что ты просил)
        holder.rasstTextView.setText(restaurant.getDistance());
        holder.restaurantTypeTextView.setText(restaurant.getRestaurantType());

        // Обработка клика на всю карточку
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = restaurant.getName();
                Intent intent;

                // ВНИМАНИЕ: Названия классов должны совпадать с твоими созданными Activity
                if (name.equals("DODO Pizza")) {
                    intent = new Intent(v.getContext(), dodoactivity.class);
                } else if (name.equals("Гоголь-Моголь")) {
                    intent = new Intent(v.getContext(), gogolactivity.class);
                } else if (name.equals("Арбат")) {
                    intent = new Intent(v.getContext(), Arbatactivity.class);
                } else if (name.equals("Дядя ВАНЯ")) {
                    intent = new Intent(v.getContext(), VanyaActivity.class);
                } else {
                    return;
                }

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ratingTextView, restaurantTypeTextView, rasstTextView;
        ImageView restaurantImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurantName);
            ratingTextView = itemView.findViewById(R.id.rating);
            restaurantImageView = itemView.findViewById(R.id.restaurantImage);
            restaurantTypeTextView = itemView.findViewById(R.id.restaurantType);
            rasstTextView = itemView.findViewById(R.id.distance);
        }
    }
}