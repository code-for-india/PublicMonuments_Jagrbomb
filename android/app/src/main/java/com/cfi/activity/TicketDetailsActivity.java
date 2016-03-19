package com.cfi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import com.cfi.BeaconReferenceApplication;
import com.cfi.R;
import com.cfi.fortuno.Wallet;


/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class TicketDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        BeaconReferenceApplication m = (BeaconReferenceApplication)getApplication();
        m.hello(this);
        m.setMonitoringActivity(this);

        verifyBluetooth();

        TextView name = (TextView) findViewById(R.id.name);
        TextView amount = (TextView) findViewById(R.id.amount);
        amount.setText("Name: " + Wallet.getName(this));
        name.setText("Amount: ₹" + Wallet.getCredits(this) + ".00");


        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BeaconReferenceApplication.mRegionBootstrap.disable();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplication()).setMonitoringActivity(null);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        BeaconReferenceApplication.mRegionBootstrap.disable();

    }


    public void didRangeBeaconsInRegion(final Beacon beacon) {
        runOnUiThread(new Runnable() {
            public void run() {

                Double d=new Double(beacon.getDistance());
                if(d < 0.5) {
                    Log.e("NearReached", "ALERT" + " & distance= " + d);
                }else{
                    Log.e("FAR AWAY"," GOOD JOB & distance = "+d);
                }

            }
        });
       /* if (beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                logToDisplay(beacon.getId3().toString(), beacon.getDistance());
            }
        }*/
    }

    public void didEnterRegion(Region region) {

    }

    public void didExitRegion(Region region) {
    }


}
