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
public class Instruction implements Data{

    public String uid, text, content_id, recipe_id;
    public int position;

    public Instruction() {

    }

    public Instruction(String uid, String text, String content_id, String recipe_id, int position) {
        this.uid = uid;
        this.text = text;
        this.content_id = content_id;
        this.recipe_id = recipe_id;
        this.position = position;
    }

    @Exclude
    public static Instruction from(DataSnapshot s) {
        Instruction i = s.getValue(Instruction.class);
        if (i != null)
            i.uid = s.getKey();
        return i;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("text", text);
        result.put("content_id", content_id);
        result.put("recipe_id", recipe_id);
        result.put("position", position);
        return result;
    }

    @Exclude
    public static FB.Request load(String uid) {
        return fb().instruction().child(uid);
    }

    @Exclude
    public FB.Result[] save() {
        if (uid == null || uid.length() == 0)
            uid = ((DatabaseReference)fb().instruction().ref()).push().getKey();
        FB.Result request1 = fb().instruction().child(uid).set(this);
        FB.Result request2 = fb().recipe().child(recipe_id).instruction().child(uid).set(true);
        return new FB.Result[]{request1, request2};
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof Instruction && ((Instruction) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
