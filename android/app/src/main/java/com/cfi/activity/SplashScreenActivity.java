package com.cfi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.cfi.R;


/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class SplashScreenActivity extends Activity {
    private static final int SPLASH_DISPLAY_TIME = 1500;
    private String PREFS = "RegisterUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

                if (!prefs.getString("name", "").equalsIgnoreCase("")) {
                    Intent goint = new Intent(SplashScreenActivity.this, GoogleMapActivity.class);
                    startActivity(goint);
                    finish();
                    overridePendingTransition(R.anim.splashfadein,
                            R.anim.splashfadeout);
                } else {
                    Intent goint = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(goint);
                    finish();
                    overridePendingTransition(R.anim.splashfadein,
                            R.anim.splashfadeout);
                }
            }
        }, SPLASH_DISPLAY_TIME);
    }
}