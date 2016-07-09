package com.culinars.culinars.adapter.main;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.culinars.culinars.R;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.OnDataChangeListener;
import com.culinars.culinars.data.Reference;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Data;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RecommendationsAdapter extends RecipeAdapter {

    int resultCount;
    public ReferenceMultipleFromKeys<Recipe> data;
    public Map<String, Content> contents;
    public Map<String, Bitmap> images;

    public RecommendationsAdapter() {
        this(10);
    }

    public RecommendationsAdapter(int initResultCount) {
        resultCount = initResultCount;
        contents = new HashMap<>();
        images = new HashMap<>();
        refreshData();
    }

    @Override
    public Recipe getDataAtPos(int position) {
        if (data != null)
            return data.getValueAt(position);
        else
            return null;
    }

    @Override
    public Bitmap getImageAtPos(int position) {
        try {
            return images.get(getDataAtPos(position).uid);
        } catch (NullPointerException e) {
            Log.w("NULLL", ":(", e);
            return null;
        }
    }

    public int getPositionOfRecipe(Recipe recipe) {
        return data.getValues().indexOf(recipe);
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
        data.addOnDataChangeListener(getListener());
    }

    public OnDataChangeListener<Recipe> getListener() {
        return new OnDataChangeListener<Recipe>() {
            @Override
            public void onDataChange(final Recipe newRecipe, int event) {
                if (!contents.containsKey(newRecipe.uid)) {
                    final ReferenceMultipleFromKeys<Content> contentRef = DataManager.getInstance().getRecipeContent(newRecipe.uid);
                    contentRef.addOnDataChangeListener(new OnDataChangeListener<Content>() {
                        @Override
                        public void onDataChange(final Content newContent, int event) {
                            if (!contents.containsKey(newRecipe.uid)) {
                                contents.put(newRecipe.uid, newContent);
                                if (!images.containsKey(newRecipe.uid)) {
                                    DataManager.getInstance().downloadContent(newContent, new DataManager.OnDownloadFinishedListener() {
                                        @Override
                                        public void onDownloadFinished(Object result) {
                                            if (result instanceof Bitmap) {
                                                images.put(newRecipe.uid, (Bitmap) result);
                                                notifyItemChanged(getPositionOfRecipe(newRecipe));
                                            }
                                        }

                                        @Override
                                        public void onDownloadFailed(Exception e) {
                                            Log.w("RECOM_DOWNL", "Download failed :(", e);
                                        }
                                    });
                                }
                            }
                            contentRef.removeOnDataChangeListener(this);
                        }
                    });
                }
                //notifyItemInserted(getPositionOfRecipe(newRecipe));
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.getValues().size()+1;
        else
            return 1;
    }
}
