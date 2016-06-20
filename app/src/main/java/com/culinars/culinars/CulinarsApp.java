package com.culinars.culinars;

import com.firebase.client.Firebase;

/**
 * Created by Oktayşen on 14/6/2016.
 */
public class CulinarsApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
