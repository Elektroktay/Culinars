package com.culinars.culinars.data.structure;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.culinars.culinars.data.DataManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Recipe implements Serializable, Data{
    public String uid, title, owner_id, description, cuisine;
    public int difficulty_scale, time, calories, fat;
    public Map<String, Boolean> instructions, content;
    public Map<String, Integer> ingredients, comments;

    public Recipe() {

    }

    public Recipe(String uid, String title, String owner_id, String description, String cuisine,
                  int difficulty_scale, int time, int calories, int fat,
                  Map<String, Boolean> instructions,
                  Map<String, Integer> ingredients,
                  Map<String, Integer> comments,
                  Map<String, Boolean> content) {
        this.uid = uid;
        this.title = title;
        this.owner_id = owner_id;
        this.description = description;
        this.cuisine = cuisine;

        this.difficulty_scale = difficulty_scale;
        this.time = time;
        this.calories = calories;
        this.fat = fat;

        this.instructions = instructions;
        this.ingredients = ingredients;
        this.comments = comments;
        this.content = content;
    }

    @Exclude
    public int getStarsAverage() {
        if (comments != null) {
            int total = 0;
            for (int i : comments.values()) {
                total += i;
            }
            return total / comments.size();
        } else {
            return 0;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("owner_id", owner_id);
        result.put("description", description);
        result.put("cuisine", cuisine);

        result.put("difficulty_scale", difficulty_scale);
        result.put("time", time);
        result.put("calories", calories);
        result.put("fat", fat);

        result.put("instructions", instructions);
        result.put("ingredients", ingredients);
        result.put("comments", comments);
        result.put("content", content);

        return result;
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof Recipe && ((Recipe) o).uid.equals(uid);
    }

    @Exclude
    public boolean isFavorite() {
        User user = DataManager.getInstance().getUser().getValue();
        return user != null && user.favorites != null && user.favorites.containsKey(uid);
    }

    @Exclude
    public int getExistingIngredients() {
        if (ingredients == null)
            return 0;
        int total = 0;
        for (String ingredient : ingredients.keySet()) {
            if (DataManager.getInstance().hasIngredient(ingredient))
                total++;
        }
        return total;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}

