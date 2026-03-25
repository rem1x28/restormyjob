package com.example.restor;

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
        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText(String.valueOf(restaurant.getRating()));


        holder.restaurantImageView.setImageResource(restaurant.getImageRes());

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ratingTextView, restaurantTypeTextView, rasstTextView; // Убедитесь, что имя совпадает
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

