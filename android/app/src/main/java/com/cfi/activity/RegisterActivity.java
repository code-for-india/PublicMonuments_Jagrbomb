package com.cfi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;


import java.util.HashMap;
import java.util.Map;

import com.cfi.R;

/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class RegisterActivity extends Activity {

    private String PREFS = "RegisterUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button buttonButton = (Button) findViewById(R.id.button_done);
        buttonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {

        Firebase ref = new Firebase("https://vivid-inferno-4516.firebaseio.com/");

        EditText editTextAadharNo = (EditText) findViewById(R.id.adhar_number);
        EditText editTextUserName = (EditText) findViewById(R.id.user_name);

        Firebase postRef = ref.child("user");
        Map<String, String> post1 = new HashMap<String, String>();
        post1.put("name", editTextUserName.getText().toString());
        post1.put("aadhar", editTextAadharNo.getText().toString());
        post1.put("fingerprint", "");
        post1.put("actionRequired", "false");
        post1.put("payment", "");
        post1.put("beacon", "0");
        postRef.child(editTextAadharNo.getText().toString()).setValue(post1);

        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", editTextUserName.getText().toString());
        editor.putString("aadhar", editTextAadharNo.getText().toString());
        editor.putString("fingerprint", "");
        editor.putString("actionRequired", "false");
        editor.putString("payment", "");
        editor.putString("beacon", "0");
        editor.commit();

        Toast.makeText(this, "Register successful.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);
        finish();
    }
}
