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
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.OnDataChangeListener;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.fragment.main.FavoritesFragment;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    Context context;
    ReferenceMultipleFromKeys<Recipe> data;
    FavoritesFragment fragment;

    public FavoritesAdapter(FavoritesFragment fragment) {
        this.fragment = fragment;
        refreshData();
    }

    private void refreshData() {
        data = DataManager.getInstance().getFavorites();
        data.addOnDataChangeListener(new OnDataChangeListener<Recipe>() {

            @Override
            public void onDataChange(Recipe newValue, int event) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_favorites_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0)
            holder.favorite_number.setText("" + data.getValues().size());
        else
            holder.favorite_number.setText("0");
        switch (position) {
            case 0: holder.favorite_text.setText("All Favorites");
                holder.favorite_image.setImageResource(R.drawable.all_favorites);
                holder.favorite_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.setAdapter(new FavoritesAdapter2());
                    }
                });
                break;
            case 1: holder.favorite_text.setText("Drinks");
                holder.favorite_image.setImageResource(R.drawable.drinks);
                break;
            case 2: holder.favorite_text.setText("Deserts");
                holder.favorite_image.setImageResource(R.drawable.deserts);
                break;
            case 3: holder.favorite_text.setText("Dinners");
                holder.favorite_image.setImageResource(R.drawable.dinners);
                break;
            case 4: holder.favorite_text.setText("Breakfasts");
                holder.favorite_image.setImageResource(R.drawable.breakfasts);
                break;
            default: holder.favorite_text.setText("Stuff");
                holder.favorite_image.setImageResource(R.drawable.cooking_prep3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView favorite_container;
        ImageView favorite_image;
        TextView favorite_text, favorite_number;

        public ViewHolder(View itemView) {
            super(itemView);
            favorite_container = (CardView) itemView.findViewById(R.id.card_favorite_container);
            favorite_image = (ImageView) itemView.findViewById(R.id.card_favorite_image);
            favorite_text = (TextView) itemView.findViewById(R.id.card_favorite_text);
            favorite_number = (TextView) itemView.findViewById(R.id.card_favorite_number);
        }
    }
}
