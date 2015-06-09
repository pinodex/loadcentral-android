package com.pinodex.loadcentral;

import android.app.Application;
import android.content.Context;

/**
 * Created by pinodex on 3/27/15.
 */
public class App extends Application {

    private static App instance;

    public App() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}
