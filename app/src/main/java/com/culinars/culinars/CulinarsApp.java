package com.culinars.culinars;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;

/**
 * Created by Oktayşen on 14/6/2016.
 */
public class CulinarsApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.setApplicationId("276752316023119");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
