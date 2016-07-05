package com.culinars.culinars.data;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReferenceKeys {

    public static final int DATA_ADDED = 98720492;
    public static final int DATA_CHANGED = 2698768;
    public static final int DATA_REMOVED = 98437045;

    private ArrayList<String> values;
    private List<OnDataChangeListener> listeners;

    public ReferenceKeys(final DatabaseReference ref) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();
        ref.addChildEventListener(getChildEventListener());
    }
    public ReferenceKeys(final Query query) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();
        query.addChildEventListener(getChildEventListener());
    }

    private ChildEventListener getChildEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String data = dataSnapshot.getKey();
                values.add(data);
                for (OnDataChangeListener listener : listeners) {
                    listener.onDataChange(data, DATA_ADDED);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String data = dataSnapshot.getKey();
                int index = values.indexOf(data);
                if (index > -1)
                    values.set(index, data);
                for (OnDataChangeListener listener : listeners) {
                    listener.onDataChange(data, DATA_CHANGED);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getKey();
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

    public String getValueAt(int pos) {
        return values.get(pos);
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void addOnDataChangeListener(OnDataChangeListener listener) {
        listeners.add(listener);
    }

    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        listeners.remove(listener);
    }

    public interface OnDataChangeListener {
        void onDataChange(String newValue, int event);
    }
}
