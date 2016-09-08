package com.culinars.culinars.data.structure;

import com.culinars.culinars.data.FB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static com.culinars.culinars.data.FB.fb;

@IgnoreExtraProperties
public class Ingredient implements Data{

    public String name, image_url;
    public int amount, price;

    public Ingredient() {

    }

    public Ingredient(String name, String image_url, int amount, int price) {
        this.name = name;
        this.image_url = image_url;
        this.amount = amount;
        this.price = price;
    }

    @Exclude
    public static Ingredient from(DataSnapshot s) {
        Ingredient i = s.getValue(Ingredient.class);
        if (i != null)
            i.name = s.getKey();
        return i;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("amount", amount);
        result.put("price", price);
        result.put("image_url", image_url);
        return result;
    }

    @Exclude
    public static FB.Request load(String uid) {
        return fb().ingredient().child(uid);
    }

    @Exclude
    public static FB.Request find(String completionText, int dataLimit) {
        return fb().withQuery(fb().ingredient().ref()
                .limitToFirst(dataLimit).orderByKey().startAt(completionText).endAt(completionText + "zzzzzzzzz"));
    }

    @Exclude
    @Override
    public boolean equals(Object o) {
        return o instanceof Ingredient && ((Ingredient) o).name.equals(name);
    }

    @Override
    public void setUid(String uid) {
        this.name = uid;
    }
}
