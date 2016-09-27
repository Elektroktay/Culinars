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
 *
 * Usage: fb().user().child(userKey).getOnce().onGet(getListener);
 */
public class FB {

    /**
     * Starts building a Firebase request.
     * @return The Request object.
     */
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

        /**
         * Equivalent to child("users").
         * @return The Request object.
         */
        public Request user() {
            ref = ref.getRef().child("users");
            return this;
        }

        /**
         * Equivalent to child("recipes").
         * @return The Request object.
         */
        public Request recipe() {
            ref = ref.getRef().child("recipes");
            return this;
        }

        /**
         * Equivalent to child("instructions").
         * @return The Request object.
         */
        public Request instruction() {
            ref = ref.getRef().child("instructions");
            return this;
        }

        /**
         * Equivalent to child("ingredients").
         * @return The Request object.
         */
        public Request ingredient() {
            ref = ref.getRef().child("ingredients");
            return this;
        }

        /**
         * Equivalent to child("comments").
         * @return The Request object.
         */
        public Request comment() {
            ref = ref.getRef().child("comments");
            return this;
        }

        /**
         * Equivalent to child("content").
         * @return The Request object.
         */
        public Request content() {
            ref = ref.getRef().child("content");
            return this;
        }

        /**
         * Goes down to the given node in the database tree.
         * @return The Request object.
         */
        public Request child(String node) {
            ref = ref.getRef().child(node);
            return this;
        }

        /**
         * Goes down to the given nodes in the database tree.
         * Going down further is not recommended.
         * @return The Request object.
         */
        public Request children(String[] ids) {
            for (String id : ids) {
                keys.add(id);
            }
            keyMode = true;
            return this;
        }

        /**
         * Goes down to the given nodes in the database tree.
         * Going down further is not recommended.
         * @return The Request object.
         */
        public Request children(List<String> ids) {
            return children(ids.toArray(new String[1]));
        }

        /**
         * Returns the Firebase Query object this request is currently building.
         * @return The Firebase Query object this request is currently building.
         */
        public Query ref() {
            return ref;
        }

        /**
         * Executes a get request and listens to any further changes to come.
         * If the result is to be used only once, use getOnce() instead.
         * @see Request#getOnce()
         * @return The result for this request.
         */
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

        /**
         * Replaces the Firebase Query currently being built with the given Query.
         * @param query Query to be replaced with
         * @return The Request object.
         */
        public Request withQuery(Query query) {
            ref = query;
            return this;
        }

        /**
         * Executes a get request and stops listening after receiving a result.
         * If the live changes to the result matter, use get() instead.
         * @see Request#get()
         * @return The result for this request.
         */
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

        /**
         * Executes a set request on the node built so far with the Object given.
         * @param value The object to be set.
         * @return The Result for this request.
         */
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

        /**
         * Executes a getOnce request on the current node, then feeds the keys of the results into a new Request using children(String[])
         * Use this to resolve a list of keys that refer to somewhere else in the database.
         * @return A new Request that will receive its children from this Request's results.
         */
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
            ref.addListenerForSingleValueEvent(ls);
            return then;
        }

        /**
         * Removes the current node from the database.
         * @return The Result for this request.
         */
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

        /**
         * Adds a listener to be called once when get() or getOnce() resolves.
         * Note: This listener will only be called once, even if the request was get().
         * To listen to the changes, use onChange() instead.
         * Note 2: If multiple results are expected, use onChange() for iterative results, or onComplete() for all results together.
         * @see FB.Result#onChange(GetListener)
         * @see FB.Result#onComplete(CompleteListener)
         * @param listener Listener to be called.
         * @return This Result.
         */
        public Result onGet(GetListener listener) {
            if (isComplete())
                listener.onDataChange(results.get(results.size()-1));
            else
                getListeners.add(listener);
            return this;
        }

        /**
         * Adds a listener to be called once when get() or getOnce() resolves.
         * Note: If the request was get(), the listener will be called again on any changes.
         * If you don't want the listener to be called again, use onGet()
         * Note: If multiple results are expected, this listener will be called for every result as they arrive.
         * If you want all results at once, use onComplete()
         * @see FB.Result#onGet(GetListener)
         * @see FB.Result#onComplete(CompleteListener)
         * @param listener Listener to be called.
         * @return This Result.
         */
        public Result onChange(GetListener listener) {
            changeListeners.add(listener);
            if (isComplete())
                listener.onDataChange(results.get(0));
            return this;
        }

        /**
         * Adds a listener to be called once when set() resolves.
         * @param listener Listener to be called.
         * @return This Result.
         */
        public Result onSet(SetListener listener) {
            setListeners.add(listener);
            if (isComplete())
                listener.onComplete(error, reference);
            return this;
        }

        /**
         * Adds a listener to be called once when the request completes.
         * @param listener
         * @return
         */
        public Result onComplete(CompleteListener listener) {
            if (isComplete())
                listener.onComplete(results);
            else
                completeListeners.add(listener);
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