package com.culinars.culinars.data.structure;

import com.culinars.culinars.data.FB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.culinars.culinars.data.FB.fb;

@IgnoreExtraProperties
public class Recipe implements Serializable, Data{
    public String uid, title, owner_id, description, cuisine;
    public int difficulty_scale, time, calories, fat, serves;
    public Map<String, Boolean> tags;
    public Map<String, Boolean> instructions, content;
    public Map<String, Integer> comments;
    public Map<String, String> ingredients;

    public Recipe() {

    }

    public Recipe(String uid, String title, String owner_id, String description, String cuisine,
                  int difficulty_scale, int time, int calories, int fat, int serves,
                  Map<String, Boolean> instructions,
                  Map<String, String> ingredients,
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
        this.serves = serves;

        this.instructions = instructions;
        this.ingredients = ingredients;
        this.comments = comments;
        this.content = content;
    }

    @Exclude
    public static Recipe from(DataSnapshot s) {
        Recipe r = s.getValue(Recipe.class);
        if (r != null)
            r.uid = s.getKey();
        return r;
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
        result.put("serves", serves);

        result.put("instructions", instructions);
        result.put("ingredients", ingredients);
        result.put("comments", comments);
        result.put("content", content);

        return result;
    }

    public static FB.Request load(String uid) {
        return fb().recipe().child(uid);
    }

    public FB.Result save() {
        if (uid == null || uid.length() == 0)
            uid = ((DatabaseReference)fb().recipe().ref()).push().getKey();
        User.current().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                User res = User.from(s);
                if (res != null) {
                    res.recipes.put(uid, true);
                    res.save();
                }
            }
        });
        return fb().recipe().child(uid).set(this);
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
    @Override
    public boolean equals(Object o) {
        return o instanceof Recipe && ((Recipe) o).uid.equals(uid);
    }

    @Exclude
    public FB.Request getIngredients() {
        return ingredients==null?null:fb().ingredient().children(new ArrayList<>(ingredients.keySet()));
    }

    @Exclude
    public FB.Request getInstructions() {
        return instructions==null?null:fb().withQuery(((DatabaseReference)fb().instruction().children(new ArrayList<>(instructions.keySet())).ref()).orderByChild("position"));
    }

    @Exclude
    public FB.Request getSimilar() {
        return fb().child("similar_recipes").child(uid).then().recipe();
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}

