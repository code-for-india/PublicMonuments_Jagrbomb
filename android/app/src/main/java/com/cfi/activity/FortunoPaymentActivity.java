package com.cfi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cfi.R;
import com.cfi.fortuno.Wallet;


/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class FortunoPaymentActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ticket);

//        MpUtils.enablePaymentBroadcast(this, Manifest.permission.PAYMENT_BROADCAST_PERMISSION);

//        PaymentRequest.PaymentRequestBuilder builder = new PaymentRequest.PaymentRequestBuilder();
//        builder.setService("3dc9b54179b954aa68da5713ad406069", "466300825e084905b983a491bfeb2742");
//        builder.setDisplayString("Payment");      // shown on user receipt
//        builder.setProductName("Pay");  // non-consumable purchases are restored using this value
//        builder.setType(MpUtils.PRODUCT_TYPE_NON_CONSUMABLE);        // non-consumable items can be later restored
//        builder.setIcon(R.mipmap.ic_launcher);
//        PaymentRequest pr = builder.build();
//        makePayment(pr);

        int price = getIntent().getIntExtra("price", 0);
        if (price == 0) {
            price = com.cfi.fortuno.Wallet.getCredits(FortunoPaymentActivity.this);
        }

        final int finalPrice = price;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("Please make payment of Rs. " + price + ".00").
                setTitle("Payment").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Wallet.addCredits(FortunoPaymentActivity.this, finalPrice, "Akhilesh");

                findViewById(R.id.rl_ticket).setVisibility(View.VISIBLE);

                Button done = (Button) findViewById(R.id.done);
                TextView name = (TextView) findViewById(R.id.name);
                TextView amount = (TextView) findViewById(R.id.amount);
                amount.setText("Name: " + Wallet.getName(FortunoPaymentActivity.this));
                name.setText("Amount: ₹" + Wallet.getCredits(FortunoPaymentActivity.this) + ".00");

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });


            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();

    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//
//        if (Wallet.getCredits(this) != 0) {
//            findViewById(R.id.rl_ticket).setVisibility(View.VISIBLE);
//
//            TextView done = (TextView) findViewById(R.id.done);
//            TextView name = (TextView) findViewById(R.id.name);
//            TextView amount = (TextView) findViewById(R.id.amount);
//            amount.setText("Name: " + Wallet.getName(this));
//            name.setText("Amount: ₹" + Wallet.getCredits(this)+".00");
//
//            done.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
//        }
//    }
}


