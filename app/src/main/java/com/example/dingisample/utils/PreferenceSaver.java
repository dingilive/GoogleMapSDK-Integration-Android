package com.example.dingisample.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tasfiqul Ghani on 6/5/2017.
 */

public class PreferenceSaver {
    private static final String PREF_NAME = "welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    public PreferenceSaver(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public boolean isDingi() {
        return pref.getBoolean("isDingi", false);
    }

    public void setDingi(Boolean login) {
        editor.putBoolean("isDingi", login);
        editor.commit();
    }

}