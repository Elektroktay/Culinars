package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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
    @Override
    public boolean equals(Object o) {
        return o instanceof Instruction && ((Instruction) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
