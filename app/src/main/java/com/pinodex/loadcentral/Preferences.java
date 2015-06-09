package com.pinodex.loadcentral;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by pinodex on 4/10/15.
 */
public class Preferences {

    private static SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(App.getContext());

    private static SharedPreferences.Editor preferencesEditor = preferences.edit();

    public static String getString(String name, String defaultValue) {
        return preferences.getString(name, defaultValue);
    }

    public static String getString(String name) {
        return preferences.getString(name, null);
    }

    public static Boolean getBoolean(String name, Boolean defaultValue) {
        return preferences.getBoolean(name, defaultValue);
    }

    public static Boolean getBoolean(String name) {
        return preferences.getBoolean(name, false);
    }

    public static Integer getInt(String name, Integer defaultValue) {
        return preferences.getInt(name, defaultValue);
    }

    public static Integer getInt(String name) {
        return preferences.getInt(name, 0);
    }

    public static Float getFloat(String name, Float defaultValue) {
        return preferences.getFloat(name, defaultValue);
    }

    public static Float getFloat(String name) {
        return preferences.getFloat(name, 0);
    }

    public static Long getLong(String name, Long defaultValue) {
        return preferences.getLong(name, defaultValue);
    }

    public static Long getLong(String name) {
        return preferences.getLong(name, 0);
    }

    public static void putString(String name, String value) {
        preferencesEditor.putString(name, value).commit();
    }

    public static void putBoolean(String name, Boolean value) {
        preferencesEditor.putBoolean(name, value).commit();
    }

    public static void putInt(String name, Integer value) {
        preferencesEditor.putInt(name, value).commit();
    }

    public static void putFloat(String name, Float value) {
        preferencesEditor.putFloat(name, value).commit();
    }

    public static void putLong(String name, Long value) {
        preferencesEditor.putLong(name, value).commit();
    }

}
