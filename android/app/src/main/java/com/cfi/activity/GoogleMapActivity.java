package com.cfi.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import com.cfi.BeaconReferenceApplication;
import com.cfi.R;


/**
 * Project: <b>MonitoringOfPublicMonuments</b><br/>
 * Created by: Akhilesh Dhar Dubey on 19/3/16.<br/>
 * Email id: akhilesh.dubey@tothenew.com<br/>
 * Skype id: akhileshdubey91
 */
public class GoogleMapActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);


        BeaconReferenceApplication m = (BeaconReferenceApplication)getApplication();
        m.hello(this);
        m.setMonitoringActivity(this);
        verifyBluetooth();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.ticket:

                Intent intent = new Intent(this, TicketDetailsActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //BeaconReferenceApplication.mRegionBootstrap.disable();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //((BeaconReferenceApplication) this.getApplication()).setMonitoringActivity(null);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        //BeaconReferenceApplication.mRegionBootstrap.disable();

    }

    public void didEnterRegion(Region region) {

    }

    public void didExitRegion(Region region) {
    }

    public void didRangeBeaconsInRegion(final Beacon beacon) {
        runOnUiThread(new Runnable() {
            public void run() {

                Double d = new Double(beacon.getDistance());
                if (d < 0.5) {
                    Log.e("NearReached", "ALERT" + " & distance= " + d);
                } else {
                    Log.e("FAR AWAY", " GOOD JOB & distance = " + d);
                }

            }
        });
       /* if (beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                logToDisplay(beacon.getId3().toString(), beacon.getDistance());
            }
        }*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        LatLng Delhi = new LatLng(28.6139, 77.2090);
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Delhi, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Delhi) // Sets the center of the map to
                .zoom(13)                   // Sets the zoom
                .bearing(65) // Sets the orientation of the camera to east
                .tilt(45)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));


        LatLng rastrapatiBhavan = new LatLng(28.6143, 77.1998);
        LatLng qutubminar = new LatLng(28.5244, 77.1852);
        LatLng akshardham = new LatLng(28.6125, 77.2772);


        Marker rbMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title("Rastrapti Bhavan")
                .position(rastrapatiBhavan)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        Marker qmMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title("Qutub minar")
                .position(qutubminar)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        Marker akMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title("Akshardham Temple")
                .position(akshardham)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mGoogleMap.setOnInfoWindowClickListener(this);

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        switch (marker.getId()) {

            case "m0":

                Intent intent = new Intent(this, com.cfi.activity.AboutActivity.class);
                intent.putExtra("name", "Rastrapti Bhavan");
                intent.putExtra("price", 200);
                intent.putExtra("about", "The Rashtrapati Bhavan About this sound pronunciation, (\"Presidential Residence\") is the official home of the President of India, located in New Delhi, India. It may refer to only the mansion (the 340-room main building) that has the President's official residence, halls, guest rooms and offices; it may also refer to the entire 130 hectare (320 acre) President Estate that additionally includes huge presidential gardens (Mughal Gardens), large open spaces, residences of bodyguards and staff, stables, other offices and utilities within its perimeter walls. The main palace building was formerly known as Viceroy's House. In terms of area, was the largest residence of a Head of State in the world until the Presidential Complex of Turkey opened 29 October 2014.");
                startActivity(intent);

                break;

            case "m1":

                Intent intent1 = new Intent(this, AboutActivity.class);
                intent1.putExtra("name", "Qutub minar");
                intent1.putExtra("price", 100);
                intent1.putExtra("about", "The construction of Qutub Minar was commissioned by Qutub-ud-Din Aibak, the founder of the Delhi Sultanate in 1199 AD. The minar was built on the ruins of the Lal Kot, the Red Citadel in the city of Dhillika.[8] It and the complex around it used the ruins of 27 Hindu and Jain temples purposefully destroyed in the Islamic incursions. Aibak's successor Iltutmish added three more storeys to complete the tower.");
                startActivity(intent1);

                break;

            case "m2":

                Intent intent2 = new Intent(this, AboutActivity.class);
                intent2.putExtra("price", 250);
                intent2.putExtra("name", "Akshardham Temple");
                intent2.putExtra("about", "Akshardham or Swaminarayan Akshardham complex is a Hindu mandir, and a spiritual-cultural campus in New Delhi, India.[1][2] Also referred to as Delhi Akshardham or Swaminarayan Akshardham, the complex displays millennia of traditional Hindu and Indian culture, spirituality, and architecture. It is a large Hindu temple.");
                startActivity(intent2);

                break;

        }
    }


}