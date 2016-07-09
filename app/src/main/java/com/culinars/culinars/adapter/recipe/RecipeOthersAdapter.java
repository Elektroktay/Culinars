package com.culinars.culinars.adapter.recipe;

import com.culinars.culinars.adapter.main.RecommendationsAdapter;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.OnDataChangeListener;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.Recipe;


public class RecipeOthersAdapter extends RecommendationsAdapter {

    Recipe currentRecipe;

    public RecipeOthersAdapter(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
        refreshData();
    }

    @Override
    public void refreshData() {
        if (currentRecipe != null) {
            data = DataManager.getInstance().getSimilarRecipes(currentRecipe.uid);
            data.addOnDataChangeListener(getListener());
        }
    }
}
