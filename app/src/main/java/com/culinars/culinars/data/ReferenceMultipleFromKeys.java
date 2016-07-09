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

    private OnDataChangeListener<String> getOnDataChangeListener(final String parentPath, final Class<T> dataClass) {
        return new OnDataChangeListener<String>() {

            @Override
            public void onDataChange(String newValue, final int event) {
                if (event == ReferenceKeys.DATA_ADDED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new OnDataChangeListener<T>() {
                        @Override
                        public void onDataChange(T newValue, int event) {
                            if (newValue != null) {
                                values.add(newValue);

                                for (OnDataChangeListener listener : listeners) {
                                    listener.onDataChange(newValue, event);
                                }
                            }
                        }
                    });
                } else if (event == ReferenceKeys.DATA_CHANGED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new OnDataChangeListener<T>() {
                        @Override
                        public void onDataChange(T newValue, int event) {
                            if (values.indexOf(newValue) > -1)
                                values.set(values.indexOf(newValue), newValue);

                            for (OnDataChangeListener listener : listeners) {
                                listener.onDataChange(newValue, event);
                            }
                        }
                    });
                } else if(event == ReferenceKeys.DATA_REMOVED) {
                    Reference<T> reference = DataManager.getInstance().getSingleData(parentPath, newValue, dataClass);
                    reference.addOnDataReadyListener(new OnDataChangeListener<T>() {
                        @Override
                        public void onDataChange(T newValue, int event) {
                            values.remove(newValue);

                            for (OnDataChangeListener listener : listeners) {
                                listener.onDataChange(newValue, event);
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
}
