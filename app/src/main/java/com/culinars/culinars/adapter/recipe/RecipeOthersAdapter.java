package com.culinars.culinars.adapter.recipe;

import com.culinars.culinars.adapter.main.RecommendationsAdapter;
import com.culinars.culinars.data.structure.Recipe;

/**
 * A RecipeAdapter that displays recipes similar to the given recipe.
 */
public class RecipeOthersAdapter extends RecommendationsAdapter {

    Recipe currentRecipe;

    public RecipeOthersAdapter(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
        refreshData();
    }

    /**
     * Downloads the similar recipes from Firebase and updates the UI accordingly.
     */
    @Override
    public void refreshData() {
        if (currentRecipe != null) {
            currentRecipe.getSimilar().getOnce().onComplete(getListener());
        }
    }
}
