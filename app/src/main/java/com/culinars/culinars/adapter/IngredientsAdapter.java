package com.culinars.culinars.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.culinars.culinars.R;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    Context context;
    RecyclerView parentRecyclerView;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_ingredient_cards, parent, false);

        parentRecyclerView = (RecyclerView) parent.findViewById(R.id.card_ingredients_view);
        StatsAdapter.setUpTapToScroll(parentRecyclerView, v, 4);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ingredient_text.setText("Tomato " + position);
        holder.ingredient_image.setImageResource(R.drawable.tomato);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView ingredient_container, ingredient_text_container;
        ImageView ingredient_image;
        TextView ingredient_text;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredient_container = (CardView) itemView.findViewById(R.id.card_ingredient_container);
            ingredient_text_container = (CardView) itemView.findViewById(R.id.card_ingredient_text_container);
            ingredient_image = (ImageView) itemView.findViewById(R.id.card_ingredient_image);
            ingredient_text = (TextView) itemView.findViewById(R.id.card_ingredient_text);
        }
    }
}
