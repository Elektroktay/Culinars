package com.culinars.culinars.adapter.main;


import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.User;
import com.google.firebase.database.DataSnapshot;

/**
 * A RecipeAdapter containing the favorite recipes of the current user.
 */
public class FavoritesAdapter2 extends RecommendationsAdapter {

    /**
     * Downloads new data from Firebase and updates UI accordingly.
     */
    @Override
    public void refreshData() {
        User.current().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                User res = User.from(s);
                if (res != null)
                    res.getFavorites().getOnce().onComplete(getListener());
            }
        });
    }

}
