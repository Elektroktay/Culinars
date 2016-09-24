package com.culinars.culinars.adapter.main;


import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.User;
import com.google.firebase.database.DataSnapshot;

public class FavoritesAdapter2 extends RecommendationsAdapter {

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
