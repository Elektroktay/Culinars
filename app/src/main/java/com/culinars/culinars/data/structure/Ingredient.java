package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("amount", amount);
        result.put("price", price);
        result.put("image_url", image_url);
        return result;
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
