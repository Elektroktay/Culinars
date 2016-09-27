package com.culinars.culinars.adapter.main;


import android.util.Log;

import com.culinars.culinars.data.DM;
import com.culinars.culinars.data.structure.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A RecipeAdapter containing search results from a query.
 */
public class RecommendationsSearchAdapter extends RecommendationsAdapter {

    private String searchQuery, searchCuisine;
    private int searchMaxTime, searchMaxCalories;
    ArrayList<String> searchIngredients;
    boolean searchOnlyCurrentIngredients;

    int resultCount = 10;

    /**
     * Updates the search parameters and performs a new search.
     * @param searchQuery Query text to be searched for. Set to null or "" if not a parameter.
     * @param searchMaxTime Maximum amount of time the recipes can take in minutes. -1 if not a parameter.
     * @param searchMaxCalories Maximum amount of calories recipes can have. -1 if not a parameter.
     * @param searchIngredients List of ingredients recipes should contain. null or empty list if not a parameter. (Not implemented)
     * @param searchCuisine The cuisine recipes should be a part of. null or "" if not a parameter.
     * @param searchOnlyCurrentIngredients true if recipes should only contain ingredients that are in the fridge. (Not implemented.)
     */
    public void updateSearchParams(String searchQuery, int searchMaxTime, int searchMaxCalories, ArrayList<String> searchIngredients, String searchCuisine, boolean searchOnlyCurrentIngredients) {
        if (searchQuery.length() > 1) {
            String firstLetter = searchQuery.substring(0, 1);
            String rest = searchQuery.substring(1);
            this.searchQuery = firstLetter.toUpperCase() + rest;
        } else {
            this.searchQuery = searchQuery;
        }
        this.searchMaxTime = searchMaxTime;
        this.searchMaxCalories = searchMaxCalories;
        this.searchIngredients = searchIngredients;
        this.searchCuisine = searchCuisine;
        this.searchOnlyCurrentIngredients = searchOnlyCurrentIngredients;
        refreshData();
    }

    public RecommendationsSearchAdapter() {
        this(10);
    }

    public RecommendationsSearchAdapter(int initResultCount) {
        super(initResultCount);
        resultCount = initResultCount;
        updateSearchParams("", -1, -1, new ArrayList<String>(), "", false);
    }

    /**
     * Returns a Recipe for the given position.
     * @param position Position of the recipe
     * @return The recipe, or null if data isn't ready yet.
     */
    @Override
    public Recipe getDataAtPos(int position) {
        if (data != null)
            return data.get(position);
        else
            return null;
    }

    /**
     * Gives the position of the given recipe
     * @param recipe Recipe to be checked
     * @return Position of the recipe.
     */
    @Override
    public int getPositionOfRecipe(Recipe recipe) {
        return data.indexOf(recipe);
    }

    /**
     * Performs the search, and updates UI accordingly.
     */
    @Override
    public void refreshData() {
        DM.searchRecipes(
                searchQuery,
                searchMaxTime,
                searchMaxCalories,
                searchIngredients,
                searchCuisine,
                searchOnlyCurrentIngredients,
                resultCount,
                getListener());
    }

    /**
     * Gives the number of items in this adapter.
     * @return Number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (data != null)
            return data.size() + 1;
        else
            return 1;
    }
}
