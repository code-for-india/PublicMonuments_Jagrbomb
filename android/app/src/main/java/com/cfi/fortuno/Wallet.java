package com.cfi.fortuno;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Project: <b>android-videoready</b><br/>
 * Created by: Akhilesh Dhar Dubey on 16/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class Wallet {
    private static final String PREFS = "com.fortumo.FortumoDemo.PREFS";
    private static final String CREDITS = "virtualcredits";
    private static final String NAME = "name";

    public static int addCredits(Context context, int amount, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int currentCredits = amount;
        //prefs.getInt(CREDITS, 0);

        SharedPreferences.Editor editor = prefs.edit();
//        currentCredits += amount;
        editor.putInt(CREDITS, currentCredits);
        editor.putString(NAME, name);
        editor.commit();
        return currentCredits;
    }

    public static int getCredits(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(CREDITS, 0);
    }

    public static String getName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(NAME, "NA");
    }
}
