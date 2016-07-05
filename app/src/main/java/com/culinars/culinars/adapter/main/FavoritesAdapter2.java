package com.culinars.culinars.adapter.main;


import android.util.Log;
import android.widget.ImageView;

import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.Recipe;

public class FavoritesAdapter2 extends RecommendationsAdapter {

    @Override
    public void refreshData() {
        data = DataManager.getInstance().getFavorites();
        data.addOnDataChangeListener(new ReferenceMultipleFromKeys.OnDataChangeListener<Recipe>() {
            @Override
            public void onDataChange(Recipe newValue, int event) {
                notifyDataSetChanged();
            }
        });
    }
}
