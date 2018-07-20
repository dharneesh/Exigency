package exigency.exigency.user.activity;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import exigency.exigency.user.helper.MySingleton;

public class MyService extends IntentService implements SensorEventListener {
    String url = "http://35.154.176.233/ulocation.php";
    String strAdd = "";
    GPSTracker gps;
    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;

    public MyService() {
        super("OK");
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        vibrate();

    }

    public void vibrate() {
        Log.v(String.valueOf(vibrateThreshold),"test"+ String.valueOf(deltaX));
        Log.v(String.valueOf(vibrateThreshold),"vibeeeee"+ String.valueOf(vibrateThreshold));
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
            gps = new GPSTracker(MyService.this);
            if(gps.canGetLocation()){

                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();


                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        strAdd = strReturnedAddress.toString();
                        Log.w("current address", "" + strReturnedAddress.toString());
                    } else {
                        Log.w("My Current  address", "No Address returned!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("My Current  address", "Canont get Address!");
                }
              SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String n1 = sharedPref.getString("contact1", "");
                String n2 = sharedPref.getString("contact2", "");
                String n3 = sharedPref.getString("contact3", "");
                final String uid = sharedPref.getString("uid", "");
                String n4="9042557571";
                String msg = "Testing :\n\nEmergency something happens in\n"+ "\t\tLatitude  =\t\t"+latitude+" \n\t\tLongitude  = "+longitude+"\n\t\tCurrent address:\t\t"+strAdd;


                final String finalStrAdd = strAdd;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(MyService.this, "Successfully Added ", Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(MyService.this, "error in listner", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("uid",uid);
                            params.put("lat",String.valueOf(latitude));
                            params.put("lon", String.valueOf(longitude));
                            params.put("add", strAdd);
                            return params;
                        }
                    };
                    MySingleton.getmInstance(MyService.this).addToRequestque(stringRequest);






    SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(n1, null, msg, null, null);
                smsManager.sendTextMessage(n2, null, msg, null, null);
                smsManager.sendTextMessage(n3, null, msg, null, null);
                smsManager.sendTextMessage(n4, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();

                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude+"\n current address"+strAdd, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
            }
        }
    @Override
    protected void onHandleIntent(Intent intent) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 4;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }


}