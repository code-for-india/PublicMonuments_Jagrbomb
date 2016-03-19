package com.cfi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cfi.R;


/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String name = getIntent().getStringExtra("name");
        String about = getIntent().getStringExtra("about");
        final int price = getIntent().getIntExtra("price", 0);

        TextView txtAbout = (TextView) findViewById(R.id.about);
        TextView txtName = (TextView) findViewById(R.id.place_name);
        TextView txtPrice = (TextView) findViewById(R.id.price);

        txtAbout.setText(about);
        txtName.setText(name);
        txtPrice.setText("Ticket Price: ₹" + price + ".00");

        Button buyTicket = (Button) findViewById(R.id.buyTicket);
        buyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // for purchase
                Intent intent = new Intent(AboutActivity.this, FortunoPaymentActivity.class);
                intent.putExtra("price", price);
                startActivity(intent);
                finish();
            }
        });

    }
}
