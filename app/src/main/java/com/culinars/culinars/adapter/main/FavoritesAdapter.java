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
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.structure.User;
import com.culinars.culinars.fragment.main.FavoritesFragment;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that fills a RecyclerView with a menu for navigating the user's favorites.
 * Contains options "All Favorites", "Drinks", "Deserts", "Dinners" and "Breakfasts"
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    Context context;
    List<Recipe> data;
    FavoritesFragment fragment;

    public FavoritesAdapter(FavoritesFragment fragment) {
        this.fragment = fragment;
        refreshData();
    }

    /**
     * Downloads new information from Firebase and updates UI accordingly.
     */
    private void refreshData() {
        User.current().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s1) {
                User res = User.from(s1);
                if (res != null)
                    res.getFavorites().getOnce().onComplete(new FB.CompleteListener() {
                    @Override
                    public void onComplete(List<DataSnapshot> results) {
                        data = new ArrayList<>();
                        for (DataSnapshot s : results) {
                            data.add(Recipe.from(s));
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * Loads (inflates) an item's appropriate xml into the RecyclerView
     * @param parent The parent ViewGroup
     * @param viewType Type of this item (not used because there's only 1 type of item)
     * @return A new ViewHolder containing the loaded xml.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_favorites_cards, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Sets up an item in the RecyclerView
     * @param holder ViewHolder holding the views of this item.
     * @param position Position of the item in the list.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 && data != null)
            holder.favorite_number.setText("" + data.size());
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

    /**
     * Gives the number of items in this adapter.
     * @return Number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 5;
    }

    /**
     * Contains the views of a single item.
     */
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
