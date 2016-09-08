package com.culinars.culinars.data.structure;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

public interface Data {

    @Exclude
    void setUid(String uid);
}
