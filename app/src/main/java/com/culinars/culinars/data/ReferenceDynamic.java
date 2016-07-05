package com.culinars.culinars.data;

import com.culinars.culinars.data.structure.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Oktayşen on 5/7/2016.
 */
public class ReferenceDynamic<T extends Data> extends Reference<T> {
    public ReferenceDynamic(DatabaseReference ref, Class<T> dataClass) {
        super(ref, dataClass);
    }

    public ReferenceDynamic(Query query, Class<T> dataClass) {
        super(query, dataClass);
    }

    @Override
    void addListenerTo(Query query, Class<T> dataClass) {
        query.addValueEventListener(getValueEventListener(dataClass));
    }

    @Override
    void addListenerTo(DatabaseReference ref, Class<T> dataClass) {
        ref.addValueEventListener(getValueEventListener(dataClass));
    }
}
