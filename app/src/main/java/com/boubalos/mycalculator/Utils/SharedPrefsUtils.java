package com.boubalos.mycalculator.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefsUtils {


    /**
     * Write to a shared preferences file, overloaded.
     **/
    public static void writeToSharedPreferences(Context context, String preferencesName,
                                                String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Read a value from the selected shared preferences file.
     **/
    public static String readValueFromSharedPreferences(Context context, String preferencesName,
                                                      String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName, MODE_PRIVATE);
        return sharedPref.getString(key, "");                                                         //If there is no pair with the given key, return -1
    }



}
