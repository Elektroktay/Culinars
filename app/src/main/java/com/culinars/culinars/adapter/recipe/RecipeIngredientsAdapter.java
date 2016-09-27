package com.culinars.culinars.adapter.recipe;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.Rational;
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.Ingredient;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.structure.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * An adapter that fills a RecyclerView with the ingredients of a given recipe.
 */
public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {

    Context context;
    Recipe currentRecipe;
    ArrayList<String> ingredients;
    int servings;
    Map<String, Ingredient> ingredientMap;
    ArrayList<Integer> selectedValues;
    private double p;

    /**
     * Initializes the adapter with ingredients from the given recipe.
     * @param currentRecipe The recipe in question.
     */
    public RecipeIngredientsAdapter(Recipe currentRecipe) {
        servings = 1;
        this.currentRecipe = currentRecipe;
        selectedValues = new ArrayList<>();
        ingredients = new ArrayList<>();
        ingredientMap = new HashMap<>();
        if (currentRecipe.ingredients != null) {
            for (String ing : currentRecipe.ingredients.keySet()) {
                ingredients.add(ing);
                Ingredient.load(ing).getOnce().onGet(new FB.GetListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Ingredient ingredient = Ingredient.from(dataSnapshot);
                        if (ingredient != null) {
                            ingredientMap.put(ingredient.name, ingredient);
                            RecipeIngredientsAdapter.this.notifyItemRangeChanged(0, getItemCount()-1);
                            Log.i("Ingredient_add", ingredient.name);
                        }
                    }
                });
            }
        }
    }

    /**
     * Loads (inflates) an item's appropriate xml into the RecyclerView, depending on its viewType.
     * @param parent The parent ViewGroup
     * @param viewType Result of calling getItemViewType(position) on the current position.
     * @return A new ViewHolder containing the loaded xml.
     */
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

    /**
     * Shows which type of item should be contained in a given position
     * @param position Position to be checked
     * @return Type of item.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1)
            return 1;
        else
            return 0;
    }

    /**
     * Sets up an item in the RecyclerView
     * @param holder ViewHolder holding the views of this item.
     * @param position Position of the item in the list.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            holder.ingredients_title.setText(ingredients.get(position));

            String valueText;
            String rawText = currentRecipe.ingredients.get(ingredients.get(position));
            String[] valueSplit = rawText.split("\\s+");
            if (valueSplit.length > 0) {
                double value = Double.parseDouble(valueSplit[0]);
                value = value / currentRecipe.serves * servings;
                if (value % 1 > 0)
                    valueText = new Rational(value).toString();
                else
                    valueText = ((int)value) + "";
                if (valueSplit.length > 1) {
                    for (int i=1; i < valueSplit.length; i++) {
                        valueText += " " + valueSplit[i];
                    }
                }
            } else {
                valueText = currentRecipe.ingredients.get(ingredients.get(position));
            }

            final String finalValueText = valueText;
            User.current().onGet(new FB.GetListener() {
                @Override
                public void onDataChange(DataSnapshot s) {
                    User res = User.from(s);
                    if (res != null) {
                        if (res.hasIngredient(ingredients.get(holder.getAdapterPosition()))) {
                            holder.ingredients_title.setTextColor(Color.WHITE);
                            holder.ingredients_value_1.setTextColor(Color.WHITE);
                            holder.ingredients_value_2.setTextColor(Color.WHITE);

                            holder.ingredients_value_1.setText("");
                            holder.ingredients_value_2.setText(finalValueText);
                        } else {
                            holder.ingredients_title.setTextColor(Color.parseColor("#B91010"));
                            holder.ingredients_value_1.setTextColor(Color.parseColor("#B91010"));
                            holder.ingredients_value_2.setTextColor(Color.parseColor("#B91010"));

                            if (ingredientMap.containsKey(ingredients.get(holder.getAdapterPosition()))) {
                                double price = (double) (ingredientMap.get(ingredients.get(holder.getAdapterPosition())).price * servings) / 100;
                                Log.w("PRICE", price + "");
                                DecimalFormat df = new DecimalFormat("0.00");
                                holder.ingredients_value_1.setText("$" + df.format(price));
                            }
                            else
                                holder.ingredients_value_1.setText("");
                            holder.ingredients_value_2.setText(finalValueText); //TODO: DYNAMIC PRICE.
                        }
                    }
                }
            });

            if (position % 2 == 0) {
                holder.ingredients_card.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.ingredients_card.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
            }

            if (selectedValues.contains(position)) {
                holder.ingredients_check.setVisibility(View.VISIBLE);
            } else {
                holder.ingredients_check.setVisibility(View.INVISIBLE);
            }

            holder.ingredients_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedValues.contains(holder.getAdapterPosition())) {
                        selectedValues.remove(Integer.valueOf(holder.getAdapterPosition()));
                    } else {
                        selectedValues.add(holder.getAdapterPosition());
                    }
                    notifyItemChanged(holder.getAdapterPosition());
                    notifyItemChanged(getItemCount()-1);
                }
            });

        } else if (getItemViewType(position) == 1) {
            final String[] servings = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
            holder.servings.setMinValue(1);
            holder.servings.setDisplayedValues(servings);
            holder.servings.setMaxValue(20);
            if (currentRecipe.serves > 0)
                holder.servings.setValue(currentRecipe.serves);
            else
                holder.servings.setValue(1);
            holder.servings.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    RecipeIngredientsAdapter.this.servings = newVal;
                    RecipeIngredientsAdapter.this.notifyItemRangeChanged(0, getItemCount()-1);
                }
            });

            if (selectedValues.isEmpty()) {
                holder.buyButton.setVisibility(View.INVISIBLE);
                holder.fridgeButton.setVisibility(View.INVISIBLE);
                holder.bar1.setVisibility(View.INVISIBLE);
                holder.bar2.setVisibility(View.INVISIBLE);
            } else {
                holder.buyButton.setVisibility(View.VISIBLE);
                holder.fridgeButton.setVisibility(View.VISIBLE);
                holder.bar1.setVisibility(View.VISIBLE);
                holder.bar2.setVisibility(View.VISIBLE);
            }

            holder.fridgeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (final int pos : selectedValues) {
                        User.current().onGet(new FB.GetListener() {
                            @Override
                            public void onDataChange(DataSnapshot s) {
                                User res = User.from(s);
                                if (res != null) {
                                    res.setIngredient(ingredients.get(pos), true).onSet(new FB.SetListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            notifyItemChanged(pos);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    selectedValues.clear();
                    notifyItemChanged(getItemCount()-1);
                }
            });
        }
        //holder.ingredients_checkbox.setChecked(position%2==0);
    }

    /**
     * Gives the number of items in this adapter.
     * @return Number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (currentRecipe.ingredients != null)
            return currentRecipe.ingredients.size()+1;
        else
            return 0;
    }

    /**
     * Holds all the views of a single item in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView ingredients_card;
        ImageView ingredients_image, ingredients_check;
        TextView ingredients_title, ingredients_value_1, ingredients_value_2;
        AppCompatCheckBox ingredients_checkbox;
        FrameLayout ingredients_container;

        NumberPickerView servings;
        FrameLayout buyButton, fridgeButton;
        View bar1, bar2;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredients_card = (CardView) itemView.findViewById(R.id.recipe_ingredients_card);
            ingredients_image = (ImageView) itemView.findViewById(R.id.recipe_ingredients_image);
            ingredients_check = (ImageView) itemView.findViewById(R.id.recipe_ingredients_check);
            ingredients_title = (TextView) itemView.findViewById(R.id.recipe_ingredients_title);
            ingredients_value_1 = (TextView) itemView.findViewById(R.id.recipe_ingredients_value_1);
            ingredients_value_2 = (TextView) itemView.findViewById(R.id.recipe_ingredients_value_2);
            ingredients_checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.recipe_ingredients_checkbox);
            ingredients_container = (FrameLayout) itemView.findViewById(R.id.recipe_ingredients_container);


            servings = (NumberPickerView) itemView.findViewById(R.id.recipe_ingredients_servings);
            buyButton = (FrameLayout) itemView.findViewById(R.id.recipe_ingredients_buy_button);
            fridgeButton = (FrameLayout) itemView.findViewById(R.id.recipe_ingredients_fridge_button);
            bar1 = itemView.findViewById(R.id.recipe_ingredients_button_bar_1);
            bar2 = itemView.findViewById(R.id.recipe_ingredients_button_bar_2);
        }
    }
}
