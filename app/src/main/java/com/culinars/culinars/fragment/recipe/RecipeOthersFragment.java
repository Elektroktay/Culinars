package com.culinars.culinars.fragment.recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.recipe.RecipeOthersAdapter;
import com.culinars.culinars.data.structure.Recipe;


public class RecipeOthersFragment extends Fragment {
    private Recipe currentRecipe;

    public RecipeOthersFragment() {
        // Required empty public constructor
    }

    public static RecipeOthersFragment newInstance(Recipe currentRecipe) {
        RecipeOthersFragment fragment = new RecipeOthersFragment();
        fragment.setCurrentRecipe(currentRecipe);
        return fragment;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_others, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_others_recyclerview);
        if (recyclerView == null)
            return rootView;
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecipeOthersAdapter(currentRecipe));


        return rootView;
    }
}
