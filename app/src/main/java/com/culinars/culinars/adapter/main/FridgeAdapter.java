package com.culinars.culinars.adapter.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.culinars.culinars.R;
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.Ingredient;
import com.culinars.culinars.data.structure.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that performs a search of ingredients from Firebase and fills a RecyclerView with the results.
 */
public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {

    Context context;
    int dataLimit;
    private List<Ingredient> data = new ArrayList<>();


    public FridgeAdapter() {
        dataLimit = 100;
        refreshData("");
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
                .inflate(R.layout.fragment_fridge_cards, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Performs a search with the given string and updates UI with the results.
     * @param completionText Search query text.
     */
    private void refreshData(String completionText) {
        Ingredient.find(completionText, dataLimit).getOnce().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                data.clear();
                for (DataSnapshot s1 : s.getChildren()) {
                    Ingredient i = Ingredient.from(s1);
                    if (i != null)
                        data.add(i);
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets up an item in the RecyclerView
     * @param holder ViewHolder holding the views of this item.
     * @param position Position of the item in the list.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Ingredient current = data.get(position);
        if (current == null)
            return;
        User.current().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                User res = User.from(s);
                if (res != null) {
                    holder.fridge_check.setImageResource(res.hasIngredient(current.name) ? R.drawable.check_green : R.drawable.check_white);
                }
            }
        });

        holder.fridge_text.setText(current.name);
        holder.fridge_image.setImageResource(R.drawable.cooking_prep3);
        holder.fridge_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ing = current.name;
                if (ing != null && ing.length() > 0) {
                    User.current().onGet(new FB.GetListener() {
                        @Override
                        public void onDataChange(DataSnapshot s) {
                            User res = User.from(s);
                            if (res != null) {
                                res.setIngredient(ing, !res.hasIngredient(ing)).onSet(new FB.SetListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Log.d("FridgeAdapter", "pos: " + holder.getAdapterPosition());
                                        notifyItemChanged(holder.getAdapterPosition());
                                    }
                                });
                            }
                        }
                    });
                } else
                    Toast.makeText(FridgeAdapter.this.context, "Ingredient name is null.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gives the number of items in this adapter.
     * @return Number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Performs a search with the given query.
     * @param searchQuery Query text.
     */
    public void updateSearchParams(String searchQuery) {
        //First letter of an ingredient should be uppercase.
        if (searchQuery.length() > 1) {
            String firstLetter = searchQuery.substring(0, 1);
            String rest = searchQuery.substring(1);
            refreshData(firstLetter.toUpperCase() + rest);
        } else {
            refreshData(searchQuery.toUpperCase());
        }
    }

    /**
     * Contains the views of a single item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView fridge_container;
        ImageView fridge_image, fridge_check;
        TextView fridge_text;

        public ViewHolder(View itemView) {
            super(itemView);
            fridge_container = (CardView) itemView.findViewById(R.id.card_fridge_container);
            fridge_image = (ImageView) itemView.findViewById(R.id.card_fridge_image);
            fridge_text = (TextView) itemView.findViewById(R.id.card_fridge_text);
            fridge_check = (ImageView) itemView.findViewById(R.id.card_fridge_check);
        }
    }
}
