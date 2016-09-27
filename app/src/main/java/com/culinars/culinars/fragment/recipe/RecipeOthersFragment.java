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

/**
 * A fragment that holds a RecyclerView containing similar recipes to the given one, in a ViewPager.
 */
public class RecipeOthersFragment extends Fragment {
    private Recipe currentRecipe;

    public RecipeOthersFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new RecipeOthersFragment. Use this instead of the constructor.
     */
    public static RecipeOthersFragment newInstance(Recipe currentRecipe) {
        RecipeOthersFragment fragment = new RecipeOthersFragment();
        fragment.setCurrentRecipe(currentRecipe);
        return fragment;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    /**
     * This method is called before xml is loaded onto the screen (inflating).
     * Inflation must be done here.
     * @return The view that was created as a result.
     */
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
