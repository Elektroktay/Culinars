package com.culinars.culinars.data.structure;

import com.culinars.culinars.data.FB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static com.culinars.culinars.data.FB.fb;

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
    public static Content from(DataSnapshot s) {
        Content c = s.getValue(Content.class);
        if (c != null)
            c.uid = s.getKey();
        return c;
    }

    @Exclude
    public static FB.Request load(String uid) {
        return fb().comment().child(uid);
    }

    @Exclude
    public FB.Result[] save() {
        if (uid == null || uid.length() == 0)
            uid = ((DatabaseReference)fb().comment().ref()).push().getKey();
        FB.Result request1 = fb().comment().child(uid).set(this);
        User.current().onGet(new FB.GetListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                User res = User.from(s);
                if (res != null) {
                    res.comments.put(uid, true);
                    res.save();
                }
            }
        });
        FB.Result request2 = fb().recipe().child(recipe_id).comment().child(uid).set(stars);
        return new FB.Result[]{request1, request2};
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
