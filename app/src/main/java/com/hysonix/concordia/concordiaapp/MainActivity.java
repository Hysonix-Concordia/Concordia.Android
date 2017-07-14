package com.hysonix.concordia.concordiaapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String _uri = "http://concordiahub.azurewebsites.net/";
    private LocationManager _locationManager;
    private Location _location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        _location = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(_location != null && _location.getTime() > Calendar.getInstance().getTimeInMillis() - 15 * 60 * 1000) {

            startSync();
        }
        else {
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
        }

    }

    private void startSync() {
        runSync();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runSync();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    private void runSync() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    WebClient conn = new WebClient();

                    JSONObject event = new JSONObject();
                    JSONObject data = new JSONObject();
                    JSONObject location = new JSONObject();

                    location.put("Lat", _location.getLatitude());
                    location.put("Lng", _location.getLongitude());

                    data.put("DateTime", formatter.format(new Date()));
                    data.put("Location", location);

                    event.put("Source", "DEVICE");
                    event.put("Type", "SYNC");
                    event.put("Data", data);

                    conn.Send(_uri, event.toString());

                    Toast.makeText(MainActivity.this, "receiver started" , Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            //Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            _location = location;
            _locationManager.removeUpdates(this);
            startSync();
        }
    }

    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {}
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
}
