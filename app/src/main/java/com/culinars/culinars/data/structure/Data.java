package com.culinars.culinars.data.structure;

import com.google.firebase.database.Exclude;

/**
 * Created by Oktayşen on 5/7/2016.
 */
public interface Data {

    @Exclude
    void setUid(String uid);
}
