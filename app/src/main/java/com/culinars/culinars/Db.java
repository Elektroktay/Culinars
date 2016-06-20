package com.culinars.culinars;

import com.firebase.client.Firebase;

/**
 * Created by Oktayşen on 14/6/2016.
 */
public class Db {

    public static Firebase getInstance(String node) {
        return (new Firebase("" + node));
    }
}
