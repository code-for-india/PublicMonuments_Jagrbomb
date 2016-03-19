package com.cfi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import com.firebase.client.Firebase;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

import com.cfi.activity.GoogleMapActivity;


public class BeaconReferenceApplication extends Application implements BootstrapNotifier, RangeNotifier {
    private static final String TAG = "BeaconRefere";
    private BeaconManager mBeaconManager;
    private Region mAllBeaconsRegion;
    private Activity mMonitoringActivity;
    private BackgroundPowerSaver mBackgroundPowerSaver;
    @SuppressWarnings("unused")
    public static RegionBootstrap mRegionBootstrap;
    private SharedPreferences sharedPreferences;
    private String name;
    private Context context;
    private boolean isLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();


        Firebase.setAndroidContext(this);
        mAllBeaconsRegion = new Region(/*"all beacons"*/"backgroundRegion", null, null, null);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        context = BeaconReferenceApplication.this;
        mBackgroundPowerSaver = new BackgroundPowerSaver(this);
        mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);


        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));    //iBeacons

        //mBeaconManager.startRangingBeaconsInRegion(region);

       /* mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(" m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        */
       /* mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));		// Estimote
       */
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb
        //
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //
        // In order to find out the proper BeaconLayout definition for other kinds of beacons, do
        // a Google search for "setBeaconLayout" (including the quotes in your search.)

    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region arg1) {
            if (beacons.size() > 0) {
                for (Beacon beacon : beacons) {

                    Log.d("distanceee", beacon.getDistance() + "");
                    Log.d("beacon.getId1()", beacon.getId1() + "");
                    Log.d("majorrrrrrr", beacon.getId2() + "");
                    Log.d("minorrr",beacon.getId3()+"");

                    if (mMonitoringActivity != null) {

                        if(String.valueOf(beacon.getId3()).equalsIgnoreCase("34167"))
                            if(mMonitoringActivity instanceof GoogleMapActivity)
                                ((GoogleMapActivity)mMonitoringActivity).didRangeBeaconsInRegion(beacon);
                    }

                }
                //  }
            }
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void didEnterRegion(Region arg0) {

        if (mMonitoringActivity != null) {
            ((GoogleMapActivity)mMonitoringActivity).didEnterRegion(arg0);

        }

        try {
            mBeaconManager.startRangingBeaconsInRegion(mAllBeaconsRegion);
            mBeaconManager.setRangeNotifier(this);

        } catch (RemoteException e) {
            Log.e(TAG, "Cannot start ranging");
        }
    }

    @Override
    public void didExitRegion(Region arg0) {
        if (mMonitoringActivity != null) {
            ((GoogleMapActivity)mMonitoringActivity).didExitRegion(arg0);
        }
    }

    public void setMonitoringActivity(Activity activity) {
        Log.e("anandjkhjkashfjjkjsa", "simran");
        mMonitoringActivity = activity;
    }

    public void hello(Activity beaconActivity) {
        setMonitoringActivity(beaconActivity);
        Log.e("jjkjsa@@@@@@@@@@@@@@@@2", "simran");

        mAllBeaconsRegion = new Region(/*"all beacons"*/"backgroundRegion", null, null, null);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        context = BeaconReferenceApplication.this;
        mBackgroundPowerSaver = new BackgroundPowerSaver(this);
        mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);


        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));    //iBeacons

    }
}
