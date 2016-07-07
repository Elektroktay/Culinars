package com.culinars.culinars.data;


import com.culinars.culinars.data.structure.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ReferenceMultipleFromKeys<T extends Data> {

    private ArrayList<T> values;
    private List<OnDataChangeListener<T>> listeners;

    public ReferenceMultipleFromKeys(final DatabaseReference ref, final String parentPath, final Class<T> dataClass) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();

        ReferenceKeys keys = new ReferenceKeys(ref);
        keys.addOnDataChangeListener(getOnDataChangeListener(parentPath, dataClass));
        //ref.addChildEventListener(getChildEventListener(dataClass));
    }

    public ReferenceMultipleFromKeys(final Query query, String parentPath, final Class<T> dataClass) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();

        ReferenceKeys keys = new ReferenceKeys(query);
        keys.addOnDataChangeListener(getOnDataChangeListener(parentPath, dataClass));
        //query.addChildEventListener(getChildEventListener(dataClass));
    }

    public ReferenceMultipleFromKeys(ReferenceKeys keys, String parentPath, final Class<T> dataClass) {
        values = new ArrayList<>();
        listeners = new ArrayList<>();

        keys.addOnDataChangeListener(getOnDataChangeListener(parentPath, dataClass));
        //query.addChildEventListener(getChildEventListener(dataClass));
    }

    private ReferenceKeys.OnDataChangeListener getOnDataChangeListener(final String parentPath, final Class<T> dataClass) {
        return new ReferenceKeys.OnDataChangeListener() {
            @Override
            public void onDataChange(String newValue, final int event) {
                if (event == ReferenceKeys.DATA_ADDED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new Reference.OnDataReadyListener<T>() {
                        @Override
                        public void onDataReady(T value) {
                            if (value != null) {
                                values.add(value);

                                for (OnDataChangeListener listener : listeners) {
                                    listener.onDataChange(value, event);
                                }
                            }
                        }
                    });
                } else if (event == ReferenceKeys.DATA_CHANGED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new Reference.OnDataReadyListener<T>() {
                        @Override
                        public void onDataReady(T value) {
                            if (values.indexOf(value) > -1)
                                values.set(values.indexOf(value), value);

                            for (OnDataChangeListener listener : listeners) {
                                listener.onDataChange(value, event);
                            }
                        }
                    });
                } else if(event == ReferenceKeys.DATA_REMOVED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new Reference.OnDataReadyListener<T>() {
                        @Override
                        public void onDataReady(T value) {
                            values.remove(value);

                            for (OnDataChangeListener listener : listeners) {
                                listener.onDataChange(value, event);
                            }
                        }
                    });
                }
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

    public interface OnDataChangeListener<T> {
        void onDataChange(T newValue, int event);
    }
}
