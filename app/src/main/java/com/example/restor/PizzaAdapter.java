package com.example.restor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// 1. Исправлено имя класса (Java любит CamelCase: PizzaAdapter)
public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {
    private List<pizza> pizzas;
    private List<pizza> cartItems = new ArrayList<>(); // <---- 1. Список для корзины

    public PizzaAdapter(List<pizza> pizzas) {
        this.pizzas = pizzas;
    }

    @NonNull
    @Override
    public PizzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PizzaViewHolder holder, int position) {
        final pizza pizza = pizzas.get(position);
        holder.bind(pizza);

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pizza.incrementCount(); // Увеличиваем счётчик нажатий
                addToCart(pizza);       // Добавляем пиццу в корзину <---- 3.
                notifyItemChanged(holder.getAdapterPosition());

                // Опционально: показываем тост о добавлении
                Toast.makeText(v.getContext(), "Пицца добавлена в корзину!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pizzas.size();
    }

    // Метод добавления пиццы в корзину <---- 2.
    private void addToCart(pizza pizza) {
        // Проверяем, есть ли уже такая пицца в корзине
        boolean isExists = false;
        for (pizza cartPizza : cartItems) {
            if (cartPizza.getName().equals(pizza.getName())) {
                cartPizza.incrementCount(); // Если есть — увеличиваем её счётчик
                isExists = true;
                break;
            }
        }
        if (!isExists) {
            // Если пиццы нет в корзине — добавляем её (со счётчиком 1)
            pizza.setCount(1); // Сброс счётчика для новой позиции в корзине
            cartItems.add(pizza);
        }
    }

    public List<pizza> getCartItems() {
        return cartItems;
    }

    // Опциональный метод для очистки корзины
    public void clearCart() {
        cartItems.clear();
    }

    class PizzaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPizza;
        TextView tvName, tvPrice, tvCount;
        ImageButton btnAdd;

        public PizzaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPizza = itemView.findViewById(R.id.imgPizza);
            tvName = itemView.findViewById(R.id.tvPizzaName);
            tvPrice = itemView.findViewById(R.id.tvPizzaPrice);
        }

        public void bind(pizza pizza) {
            imgPizza.setImageResource(pizza.getImageResId());
            tvName.setText(pizza.getName());
            tvPrice.setText("Цена: " + pizza.getPrice() + " руб.");
            tvCount.setText(String.valueOf(pizza.getCount()));
        }
    }
}
