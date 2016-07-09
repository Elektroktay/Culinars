package com.culinars.culinars.fragment.recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.recipe.RecipeFactsAdapter;
import com.culinars.culinars.data.structure.Recipe;

import org.w3c.dom.Text;

import java.util.Random;


public class RecipeFactsFragment extends Fragment {
    private Recipe currentRecipe;

    public RecipeFactsFragment() {
        // Required empty public constructor
    }

    public static RecipeFactsFragment newInstance(Recipe currentRecipe) {
        RecipeFactsFragment fragment = new RecipeFactsFragment();
        fragment.setCurrentRecipe(currentRecipe);
        return fragment;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_facts, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_facts_recyclerview);
        if (recyclerView == null)
            return rootView;
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecipeFactsAdapter(currentRecipe));


        TextView description = (TextView) rootView.findViewById(R.id.recipe_facts_description);
        description.setText(currentRecipe.description);

        TextView caloriesValue = (TextView) rootView.findViewById(R.id.card_calories_value);
        TextView caloriesUnit = (TextView) rootView.findViewById(R.id.card_calories_unit);
        caloriesValue.setText("" + currentRecipe.calories);

        TextView fatValue = (TextView) rootView.findViewById(R.id.card_fat_value);
        TextView fatUnit = (TextView) rootView.findViewById(R.id.card_fat_unit);
        fatValue.setText("" + currentRecipe.fat);

        TextView timeValue = (TextView) rootView.findViewById(R.id.card_time_value);
        TextView timeUnit = (TextView) rootView.findViewById(R.id.card_time_unit);
        int value = currentRecipe.time;
        if (value > 59) {
            if (value%60 < 10)
                timeValue.setText(((int) Math.floor(value / 60)) + ":0" + (value % 60));
            else
                timeValue.setText(((int) Math.floor(value / 60)) + ":" + (value % 60));
            timeUnit.setText("hrs");
        } else {
            timeValue.setText("" + value);
            timeUnit.setText("mins");
        }

        Random rand = new Random(currentRecipe.calories);

        TextView sodiumValue = (TextView) rootView.findViewById(R.id.card_sodium_value);
        TextView sodiumUnit = (TextView) rootView.findViewById(R.id.card_sodium_unit);
        int sodVal = rand.nextInt(50) + 10;
        sodiumValue.setText("" + sodVal);

        TextView carbohydrateValue = (TextView) rootView.findViewById(R.id.card_carbohydrate_value);
        TextView carbohydrateUnit = (TextView) rootView.findViewById(R.id.card_carbohydrate_unit);
        int carbVal = rand.nextInt(450) + 60;
        carbohydrateValue.setText("" + carbVal);

        TextView proteinValue = (TextView) rootView.findViewById(R.id.card_protein_value);
        TextView proteinUnit = (TextView) rootView.findViewById(R.id.card_protein_unit);
        int proVal = rand.nextInt(250) + 60;
        proteinValue.setText("" + proVal);

        return rootView;
    }
}
