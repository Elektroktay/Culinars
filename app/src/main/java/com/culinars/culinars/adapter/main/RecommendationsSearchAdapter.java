package com.culinars.culinars.adapter.main;


import com.culinars.culinars.data.DM;
import com.culinars.culinars.data.structure.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsSearchAdapter extends RecommendationsAdapter {

    private String searchQuery, searchCuisine;
    private int searchMaxTime, searchMaxCalories;
    ArrayList<String> searchIngredients;
    boolean searchOnlyCurrentIngredients;

    int resultCount = 10;
    List<Recipe> data;

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

    @Override
    public Recipe getDataAtPos(int position) {
        if (data != null)
            return data.get(position);
        else
            return null;
    }

    @Override
    public int getPositionOfRecipe(Recipe recipe) {
        return data.indexOf(recipe);
    }

    @Override
    public void refreshData() {
        DM.getInstance().searchRecipes(
                searchQuery,
                searchMaxTime,
                searchMaxCalories,
                searchIngredients,
                searchCuisine,
                searchOnlyCurrentIngredients,
                resultCount,
                getListener());
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size()+1;
        else
            return 1;
    }
}
