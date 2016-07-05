package com.culinars.culinars.adapter.main;

import android.widget.ImageView;

import com.culinars.culinars.R;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.Reference;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.User;


public class RecommendationsAdapter extends RecipeAdapter {

    int resultCount;
    public ReferenceMultipleFromKeys<Recipe> data;

    public RecommendationsAdapter() {
        this(10);
    }

    public RecommendationsAdapter(int initResultCount) {
        resultCount = initResultCount;
        refreshData();
    }

    @Override
    public Recipe getDataAtPos(int position) {
        return data.getValueAt(position);
    }

    @Override
    public void onFavoriteClick(final int position, final ImageView cardFavorite) {
        if (getDataAtPos(position).isFavorite()) {
            DataManager.getInstance().setFavorite(getDataAtPos(position).uid, false);
            cardFavorite.setImageResource(R.drawable.heart_outline);
        } else {
            DataManager.getInstance().setFavorite(getDataAtPos(position).uid, true);
            cardFavorite.setImageResource(R.drawable.heart);
        }

/*        DataManager.getInstance().getUser().addOnDataReadyListener(new Reference.OnDataReadyListener<User>() {
            @Override
            public void onDataReady(User value) {
                if (value.favorites != null && value.favorites.containsKey(getDataAtPos(position).uid)) {
                    DataManager.getInstance().setFavorite(getDataAtPos(position).uid, false);
                    cardFavorite.setImageResource(R.drawable.heart_outline);
                } else {
                    DataManager.getInstance().setFavorite(getDataAtPos(position).uid, true);
                    cardFavorite.setImageResource(R.drawable.heart);
                }
            }
        });*/
    }

    public void refreshData() {
        data = DataManager.getInstance().getRecommendations(DataManager.getInstance().getCurrentUser().getUid(), resultCount);
        data.addOnDataChangeListener(new ReferenceMultipleFromKeys.OnDataChangeListener<Recipe>() {
            @Override
            public void onDataChange(Recipe newValue, int event) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.getValues().size()+1;
        else
            return 1;
    }
}
