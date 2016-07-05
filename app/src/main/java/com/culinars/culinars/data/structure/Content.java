package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Content implements Data{

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
    @Override
    public boolean equals(Object o) {
        return o instanceof Content && ((Content) o).uid.equals(uid);
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
