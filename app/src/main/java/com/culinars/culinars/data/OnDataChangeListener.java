package com.culinars.culinars.data;

/**
 * Created by Oktayşen on 7/7/2016.
 */
public interface OnDataChangeListener<T> {
    void onDataChange(T newValue, int event);
}
