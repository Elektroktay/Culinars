package com.culinars.culinars;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oktayşen on 22/6/2016.
 */
public class Recipe {
    public String uid, title;
    public int stars, calories, minutes, ingredientCount;

    public Recipe() {

    }

    public Recipe(String uid, String title, int stars, int calories, int minutes, int ingredientCount) {
        this.uid = uid;
        this.title = title;
        this.stars = stars;
        this.calories = calories;
        this.minutes = minutes;
        this.ingredientCount = ingredientCount;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("stars", stars);
        result.put("calories", calories);
        result.put("minutes", minutes);
        result.put("ingredientCount", ingredientCount);

        return result;
    }
}
