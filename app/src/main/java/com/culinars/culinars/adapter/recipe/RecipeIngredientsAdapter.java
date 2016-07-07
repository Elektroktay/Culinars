package com.culinars.culinars.adapter.recipe;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.structure.Ingredient;
import com.culinars.culinars.data.structure.Recipe;

import java.util.ArrayList;
import java.util.Map;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {

    Context context;
    Recipe currentRecipe;
    ArrayList<String> ingredients;
    int servings;

    public RecipeIngredientsAdapter(Recipe currentRecipe) {
        servings = 1;
        this.currentRecipe = currentRecipe;
        if (currentRecipe.ingredients != null) {
            ingredients = new ArrayList<>();
            for (String ing : currentRecipe.ingredients.keySet()) {
                ingredients.add(ing);
                Log.i("Ingredient_add", ing);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        context = parent.getContext();
        if (viewType==0) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_recipe_ingredients_card, parent, false);
        } else {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_recipe_ingredients_buttons, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1)
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            holder.ingredients_title.setText(ingredients.get(position));
            holder.ingredients_amount.setText(currentRecipe.ingredients.get(ingredients.get(position)) + " gr");

            if (DataManager.getInstance().hasIngredient(ingredients.get(position))) {
                holder.ingredients_title.setTextColor(Color.WHITE);
                holder.ingredients_amount.setTextColor(Color.WHITE);
            } else {
                holder.ingredients_title.setTextColor(Color.parseColor("#B91010"));
                holder.ingredients_amount.setTextColor(Color.parseColor("#B91010"));
            }

            if (position % 2 == 0) {
                holder.ingredients_card.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.ingredients_card.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
            }
        } else if (getItemViewType(position) == 1) {
            String[] servings = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
            holder.servings.setAdapter(new ArrayAdapter<>(context, R.layout.fragment_servings_spinner, servings));
        }
        //holder.ingredients_checkbox.setChecked(position%2==0);
    }

    @Override
    public int getItemCount() {
        if (currentRecipe.ingredients != null)
            return currentRecipe.ingredients.size()+1;
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView ingredients_card;
        ImageView ingredients_image;
        TextView ingredients_title, ingredients_amount;
        AppCompatCheckBox ingredients_checkbox;
        FrameLayout ingredients_container;

        AppCompatSpinner servings;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredients_card = (CardView) itemView.findViewById(R.id.recipe_ingredients_card);
            ingredients_image = (ImageView) itemView.findViewById(R.id.recipe_ingredients_image);
            ingredients_title = (TextView) itemView.findViewById(R.id.recipe_ingredients_title);
            ingredients_amount = (TextView) itemView.findViewById(R.id.recipe_ingredients_amount);
            ingredients_checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.recipe_ingredients_checkbox);
            ingredients_container = (FrameLayout) itemView.findViewById(R.id.recipe_ingredients_container);

            servings = (AppCompatSpinner) itemView.findViewById(R.id.recipe_ingredients_servings);
        }
    }
}
