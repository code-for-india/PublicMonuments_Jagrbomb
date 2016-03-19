package com.cfi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cfi.R;


public class MainActivity extends Activity {

    private String url = "http://auth.uidai.gov.in/1.6/" +
            "public" +
            "/" + "9" +
            "/" + "9" +
            "/" + "MLTbKYcsgYMq1zgL3WMZYrnyvsarlljxpom2A-QTPc0Zud23shpnqPk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button buttonLogin = (Button) findViewById(R.id.button_login);
        Button buttonRegister = (Button) findViewById(R.id.button_register);
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                login();
//            }
//        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }



    private void register() {

        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();

    }


}
