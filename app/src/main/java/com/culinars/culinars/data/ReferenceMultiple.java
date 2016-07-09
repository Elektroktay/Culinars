package com.culinars.culinars.data;


import android.util.Log;

import com.culinars.culinars.data.structure.Data;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReferenceMultiple<T extends Data> {

    public static final int DATA_ADDED = 98720492;
    public static final int DATA_CHANGED = 2698768;
    public static final int DATA_REMOVED = 98437045;

    private ArrayList<T> values;
    private List<OnDataChangeListener<T>> listeners;

    public ReferenceMultiple(final DatabaseReference ref, final Class<T> dataClass) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();
        ref.addChildEventListener(getChildEventListener(dataClass));
    }
    public ReferenceMultiple(final Query query, final Class<T> dataClass) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();
        query.addChildEventListener(getChildEventListener(dataClass));
    }

    private ChildEventListener getChildEventListener(final Class<T> dataClass) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.getValue() != null) {
                    T data = dataSnapshot.getValue(dataClass);
                    data.setUid(dataSnapshot.getKey());
                    values.add(data);
                    for (OnDataChangeListener listener : listeners) {
                        listener.onDataChange(data, DATA_ADDED);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                T data = dataSnapshot.getValue(dataClass);
                data.setUid(dataSnapshot.getKey());
                int index = values.indexOf(data);
                if (index > -1)
                    values.set(index, data);
                for (OnDataChangeListener listener : listeners) {
                    listener.onDataChange(data, DATA_CHANGED);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                T data = dataSnapshot.getValue(dataClass);
                data.setUid(dataSnapshot.getKey());
                values.remove(data);
                for (OnDataChangeListener listener : listeners) {
                    listener.onDataChange(data, DATA_REMOVED);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("REF_MULTIPLE", previousChildName + " moved.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("REF_MULTIPLE", "CANCELED", databaseError.toException());
            }
        };
    }

    public T getValueAt(int pos) {
        return values.get(pos);
    }

    public ArrayList<T> getValues() {
        return values;
    }

    public void addOnDataChangeListener(OnDataChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeOnDataChangeListener(OnDataChangeListener<T> listener) {
        listeners.remove(listener);
    }
}
