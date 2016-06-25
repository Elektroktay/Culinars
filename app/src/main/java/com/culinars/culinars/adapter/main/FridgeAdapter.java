package com.culinars.culinars.adapter.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.culinars.culinars.R;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {

    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_fridge_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fridge_number.setText("" + (position * 5));
        switch (position) {
            case 0: holder.fridge_text.setText("All Favorites");
                holder.fridge_image.setImageResource(R.drawable.all_favorites);
                break;
            case 1: holder.fridge_text.setText("Drinks");
                holder.fridge_image.setImageResource(R.drawable.drinks);
                break;
            case 2: holder.fridge_text.setText("Deserts");
                holder.fridge_image.setImageResource(R.drawable.deserts);
                break;
            case 3: holder.fridge_text.setText("Dinners");
                holder.fridge_image.setImageResource(R.drawable.dinners);
                break;
            case 4: holder.fridge_text.setText("Breakfasts");
                holder.fridge_image.setImageResource(R.drawable.breakfasts);
                break;
            default: holder.fridge_text.setText("Stuff");
                holder.fridge_image.setImageResource(R.drawable.cooking_prep3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView fridge_container;
        ImageView fridge_image;
        TextView fridge_text, fridge_number;

        public ViewHolder(View itemView) {
            super(itemView);
            fridge_container = (CardView) itemView.findViewById(R.id.card_fridge_container);
            fridge_image = (ImageView) itemView.findViewById(R.id.card_fridge_image);
            fridge_text = (TextView) itemView.findViewById(R.id.card_fridge_text);
            fridge_number = (TextView) itemView.findViewById(R.id.card_fridge_number);
        }
    }
}
