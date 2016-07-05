package com.culinars.culinars.data;

import android.util.Log;

import com.culinars.culinars.data.structure.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Reference<T extends Data> {

    private T value;
    private ArrayList<OnDataReadyListener<T>> listeners;

    public Reference(final DatabaseReference ref, final Class<T> dataClass) {
        listeners = new ArrayList<>();
        addListenerTo(ref, dataClass);
    }
    public Reference(final Query query, final Class<T> dataClass) {
        listeners = new ArrayList<>();
        addListenerTo(query, dataClass);
    }

    void addListenerTo(DatabaseReference ref, Class<T> dataClass) {
        ref.addListenerForSingleValueEvent(getValueEventListener(dataClass));
    }

    void addListenerTo(Query query, Class<T> dataClass) {
        query.addListenerForSingleValueEvent(getValueEventListener(dataClass));
    }


    ValueEventListener getValueEventListener(final Class<T> dataClass) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    value = dataSnapshot.getValue(dataClass);
                }catch (Exception e) {
                    Log.e("REFERENCE_FAIL", dataClass.toString(), e);
                    return;
                }
                value.setUid(dataSnapshot.getKey());
                for (OnDataReadyListener<T> listener : listeners) {
                    listener.onDataReady(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("REF_SINGLE", "CANCELED", databaseError.toException());
            }
        };
    }

    public T getValue() {
        return value;
    }

    public void addOnDataReadyListener(OnDataReadyListener<T> listener) {
        listeners.add(listener);
    }

    public void removeOnDataReadyListener(OnDataReadyListener<T> listener) {
        listeners.remove(listener);
    }

    public interface OnDataReadyListener<T> {
        void onDataReady(T value);
    }
}