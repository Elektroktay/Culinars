package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Comment implements Data{

    public String uid, user_id, recipe_id, title, text;
    public int stars;

    public Comment() {

    }

    public Comment(String uid, String user_id, String recipe_id, String title, String text, int stars) {
        this.uid = uid;
        this.user_id = user_id;
        this.recipe_id = recipe_id;
        this.title = title;
        this.text = text;
        this.stars = stars;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("user_id", user_id);
        result.put("recipe_id", recipe_id);
        result.put("title", title);
        result.put("text", text);
        result.put("stars", stars);
        return result;
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof Comment && ((Comment) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
