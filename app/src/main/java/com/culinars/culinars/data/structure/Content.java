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
public class Content implements Data{

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    public String uid, url;
    public int type;

    public Content() {

    }

    public Content(String uid, String url, int type) {
        this.uid = uid;
        this.url = url;
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("url", url);
        result.put("type", type);
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
        return fb().child("content").child(uid);
    }

    @Exclude
    public FB.Result save() {
        if (uid == null || uid.length() == 0)
            uid = ((DatabaseReference)fb().content().ref()).push().getKey();
        return fb().content().child(uid).set(this);
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof Content && ((Content) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
