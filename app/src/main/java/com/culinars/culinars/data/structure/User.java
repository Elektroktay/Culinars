package com.culinars.culinars.data.structure;

import android.support.annotation.Nullable;

import com.culinars.culinars.data.FB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.culinars.culinars.data.FB.fb;

@IgnoreExtraProperties
public class User implements Data{

    public String uid, nickname, email;
    public Map<String, Boolean> history, favorites, ingredients, comments, recipes;
    public Map<String, Boolean> recommendations;

    @Exclude private static User currentUser;
    @Exclude private static FB.Result userResult;

    public User() {

    }

    public static void updateUser(User user) {
        currentUser = user;
    }

    public User(String uid, String nickname, String email) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("nickname", nickname);
        result.put("email", email);

        result.put("history", history);
        result.put("favorites", favorites);
        result.put("ingredients", ingredients);
        result.put("comments", comments);
        result.put("recipes", recipes);

        return result;
    }

    @Exclude
    public static User from(DataSnapshot s) {
        User u = s.getValue(User.class);
        if (u != null)
            u.uid = s.getKey();
        return u;
    }

    @Exclude
    public static FB.Request load(String uid) {
        return fb().user().child(uid);
    }

    @Exclude
    public FB.Result save() {
        if (uid != null)
            return fb().user().child(uid).set(this);
        else
            throw new IllegalStateException("User does not have a uid.");
    }

    @Exclude
    public static FB.Result loadCurrent() {
        if (userResult == null)
            userResult = fb().user().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
        return userResult;
    }

    @Exclude
    public FB.Request getFavorites() {
        return favorites==null?null:fb().recipe().children(new ArrayList<>(favorites.keySet()));
    }

    @Exclude
    public FB.Request getIngredients() {
        return ingredients==null?null:fb().ingredient().children(new ArrayList<>(ingredients.keySet()));
    }

    @Exclude
    public FB.Request getRecommendations(int from, int to) {
        if (recommendations == null || recommendations.size() <= from)
            return null;
        if (to <= from)
            throw new IllegalArgumentException("to must be greater than from. from:" + from + " to:" + to);
        if (recommendations.size() <= to)
            to = recommendations.size() - 1;

        String[] resultIds = new String[to - from];
        String[] recommendationIds = recommendations.keySet().toArray(new String[1]);
        System.arraycopy(recommendationIds, from, resultIds, 0, to-from);
        return fb().recipe().children(resultIds);
    }

    @Exclude
    public FB.Result setFavorite(String recipeId, boolean isFavorite) {
        if (isFavorite) {
            if (favorites == null) favorites = new HashMap<>();
            favorites.put(recipeId, true);
        } else {
            if (favorites != null)
                favorites.remove(recipeId);
        }
        return save();
    }

    @Exclude
    public FB.Result setIngredient(String ingredientId, boolean hasIngredient) {
        if (hasIngredient) {
            if (ingredients == null) ingredients = new HashMap<>();
            ingredients.put(ingredientId, true);
        } else {
            if (ingredients != null)
                ingredients.remove(ingredientId);
        }
        return save();
    }

    @Exclude
    public boolean hasIngredient(String ingredientId) {
        return ingredients != null && ingredients.containsKey(ingredientId);
    }

    @Exclude
    public boolean isFavorite(String recipeId) {
        return favorites != null && favorites.containsKey(recipeId);
    }

    @Exclude
    public int getExistingIngredientCount(Recipe recipe) {
        if (recipe.ingredients == null)
            return 0;
        int total = 0;
        for (String ingredient : recipe.ingredients.keySet()) {
            if (hasIngredient(ingredient))
                total++;
        }
        return total;
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).uid.equals(uid);
    }


    @Exclude
    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }




}
