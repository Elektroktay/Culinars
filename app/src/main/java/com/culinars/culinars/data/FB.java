package com.culinars.culinars.data;


import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Firebase Database wrapper.
 */
public class FB {

    public static Request fb() {
        return new Request();
    }

    public static class Request {
        Query ref = FirebaseDatabase.getInstance().getReference();

        private List<String> keys = new ArrayList<>();
        private boolean keyMode = false;
        private int mode;
        private Request then;

        private static final int GET = 692813;
        private static final int GETONCE = 875785;

        private Result result;

        private Request() {}

        private Request(boolean keyMode) {this.keyMode = keyMode;}

        private void processKeys() {
            if (keys.size() == 0 || result == null)
                return;
            result.setResultCount(keys.size());
            final List<DataSnapshot> res = new ArrayList<>();
            while (keys.size() > 0) {
                ValueEventListener ls = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        res.add(dataSnapshot);
                        result.doGet(res);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase Manager", "GETONCE failed.", databaseError.toException());
                    }
                };
                if (mode == GET)
                    ref.getRef().child(keys.get(0)).addValueEventListener(ls);
                else if (mode == GETONCE)
                    ref.getRef().child(keys.get(0)).addListenerForSingleValueEvent(ls);
                keys.remove(0);
            }
        }

        public Request user() {
            ref = ref.getRef().child("users");
            return this;
        }

        public Request recipe() {
            ref = ref.getRef().child("recipes");
            return this;
        }

        public Request instruction() {
            ref = ref.getRef().child("instructions");
            return this;
        }

        public Request ingredient() {
            ref = ref.getRef().child("ingredients");
            return this;
        }

        public Request comment() {
            ref = ref.getRef().child("comments");
            return this;
        }


        public Request content() {
            ref = ref.getRef().child("content");
            return this;
        }

        public Request child(String id) {
            ref = ref.getRef().child(id);
            return this;
        }

        public Request children(String[] ids) {
            for (String id : ids) {
                keys.add(id);
            }
            keyMode = true;
            return this;
        }

        public Request children(List<String> ids) {
            return children(ids.toArray(new String[1]));
        }

        public Query ref() {
            return ref;
        }

        public Result get() {
            result = new Result();
            if (!keyMode) {
                result.setResultCount(1);
                ValueEventListener ls = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<DataSnapshot> res = new ArrayList<>();
                        res.add(dataSnapshot);
                        result.doGet(res);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase Manager", "GET failed.", databaseError.toException());
                    }
                };
                ref.addValueEventListener(ls);
            } else {
                mode = GET;
                processKeys();
            }
            return result;
        }

        public Request withQuery(Query query) {
            ref = query;
            return this;
        }

        public Result getOnce() {
            result = new Result();
            if (!keyMode) {
                result.setResultCount(1);
                ValueEventListener ls = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<DataSnapshot> res = new ArrayList<>();
                        res.add(dataSnapshot);
                        result.doGet(res);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase Manager", "GETONCE failed.", databaseError.toException());
                    }
                };
                ref.addListenerForSingleValueEvent(ls);
            } else {
                mode = GETONCE;
                processKeys();
            }
            return result;
        }

        public Result set(@Nullable Object value) {
            result = new Result();
            result.setResultCount(1);
            DatabaseReference.CompletionListener ls = new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) Log.w("Firebase Manager", "SET failed.", databaseError.toException());
                    result.doSet(databaseError, databaseReference);
                }
            };

            ref.getRef().setValue(value, ls);
            return result;
        }

        public Request then() {
            then = new Request(true);

            ValueEventListener ls = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        then.keys.add(ds.getKey());
                    }
                    then.processKeys();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Firebase Manager", "THEN failed.", databaseError.toException());
                }
            };
            ref.addValueEventListener(ls);
            return then;
        }

        public Result remove() {
            result = new Result();
            result.setResultCount(1);
            DatabaseReference.CompletionListener ls = new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) Log.w("Firebase Manager", "REMOVE failed.", databaseError.toException());
                    result.doSet(databaseError, databaseReference);
                }
            };
            ref.getRef().removeValue(ls);
            return result;
        }
    }

    public static class Result {
        private List<GetListener> getListeners = new ArrayList<>();
        private List<SetListener> setListeners = new ArrayList<>();
        private List<CompleteListener> completeListeners = new ArrayList<>();
        private List<GetListener> changeListeners = new ArrayList<>();

        private List<DataSnapshot> results = new ArrayList<>();
        private DatabaseError error;
        private DatabaseReference reference;
        private int resultCount = 0;

        private Result(){}

        public Result onGet(GetListener listener) {
            if (isComplete())
                listener.onDataChange(results.get(results.size()-1));
            else
                getListeners.add(listener);
            return this;
        }

        public Result onChange(GetListener listener) {
            changeListeners.add(listener);
            if (isComplete())
                listener.onDataChange(results.get(0));
            return this;
        }

        public Result onSet(SetListener listener) {
            setListeners.add(listener);
            if (isComplete())
                listener.onComplete(error, reference);
            return this;
        }

        public Result onComplete(CompleteListener listener) {
            completeListeners.add(listener);
            if (isComplete())
                listener.onComplete(results);
            return this;
        }

        public boolean isComplete() {
            return resultCount > 0 && resultCount == results.size();
        }

        private void setResultCount(int count) {
            resultCount = count;
        }

        private void doGet(List<DataSnapshot> dataSnapshot) {
            results = dataSnapshot;
            for (GetListener listener : getListeners)
                listener.onDataChange(results.get(results.size()-1));
            getListeners.clear();
            for (GetListener listener : changeListeners)
                listener.onDataChange(results.get(results.size()-1));
            if (isComplete()) {
                for (CompleteListener listener : completeListeners)
                    listener.onComplete(results);
                completeListeners.clear();
            }
        }

        private void doSet(DatabaseError error, DatabaseReference databaseReference) {
            results.add(null);
            this.error = error;
            this.reference = databaseReference;
            for (SetListener listener : setListeners)
                listener.onComplete(error, databaseReference);
            if (isComplete())
                for (CompleteListener listener : completeListeners)
                    listener.onComplete(results);
        }
    }

    public interface GetListener {
        void onDataChange(DataSnapshot dataSnapshot);
    }

    public interface SetListener {
        void onComplete(DatabaseError databaseError, DatabaseReference databaseReference);
    }

    public interface CompleteListener {
        void onComplete(List<DataSnapshot> results);
    }
}