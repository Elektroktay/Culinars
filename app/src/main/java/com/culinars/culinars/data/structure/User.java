package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Data{

    public String uid, nickname, email;
    public Map<String, Boolean> history, favorites, ingredients, comments, recipes;

    public User() {

    }

    public User(String uid, String nickname, String email,
                Map<String, Boolean> history, Map<String, Boolean> favorites, Map<String, Boolean> ingredients,
                Map<String, Boolean> comments, Map<String, Boolean> recipes) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;

        this.history = history;
        this.favorites = favorites;
        this.ingredients = ingredients;
        this.comments = comments;
        this.recipes = recipes;
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
    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
