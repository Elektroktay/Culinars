package com.culinars.culinars.fragment.recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.recipe.RecipeIngredientsAdapter;
import com.culinars.culinars.data.structure.Recipe;


public class RecipeIngredientsFragment extends Fragment {
    Recipe currentRecipe;

    public RecipeIngredientsFragment() {
        // Required empty public constructor
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    /**
     * Creates a new RecipeIngredientsFragment. Use this instead of the constructor.
     */
    public static RecipeIngredientsFragment newInstance(Recipe currentRecipe) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        fragment.setCurrentRecipe(currentRecipe);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is called before xml is loaded onto the screen (inflating).
     * Inflation must be done here.
     * @return The view that was created as a result.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_ingredients_recyclerview);
        if (recyclerView == null)
            return rootView;
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecipeIngredientsAdapter(currentRecipe));

        return rootView;
    }
}
