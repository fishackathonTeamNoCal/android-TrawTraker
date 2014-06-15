package com.example.trawtracker;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Start_PhotoLocation extends Activity {

	private String reportValues;  //for shared preferences....?
	private EditText edActivity, edVesselID, edDirection, edComments, edLocationInfo, edDateTime;
	public String mVesselActivity, mVesselID, mDirection, mComment, mLocationInfo, mDateTime;
	public double latitude, longitude; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start__photo_location);

	getPhoneGPScord();
		
	}

	/*
	 * Record values entered into Report (ie. text boxes, etc)
	 */
	private void saveReportValues(){
		edActivity = (EditText) findViewById(R.id.txtVesselActivity);
		mVesselActivity = edActivity.getText().toString();
		edVesselID = (EditText) findViewById(R.id.txtVesselId);
		mVesselID = edVesselID.getText().toString();
		edDirection = (EditText) findViewById(R.id.txtDirection);
		mDirection = edDirection.getText().toString();
		edComments = (EditText) findViewById(R.id.txtComments);
		mComment = edComments.getText().toString();
		edLocationInfo = (EditText) findViewById(R.id.txtLocation);
		mLocationInfo = edLocationInfo.getText().toString();
		edDateTime = (EditText) findViewById(R.id.txtDateTime);
		mDateTime = edDateTime.getText().toString();
		
	}
	
	private void getPhoneGPScord() {
		// get device properties (device manager?), read GPS coord. what is the data format?
		
		// FIXME - HOW to get the GPS coord from the phone?? device manager settings? :/

	       LocationManager locManager;
	        locManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,
	            500.0f, locationListener);
	        Location location = locManager
	                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        if (location != null) {
	            latitude = location.getLatitude();
	            longitude = location.getLongitude();
	        }
	   
        String slatitude = String.valueOf(latitude);
        String slongitude = String.valueOf(longitude);
		Log.i("***GGGG","lat = " + slatitude + "   !!! long = " + slongitude);
		
		
		Boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		TextView txLocationStatus = (TextView) findViewById(R.id.txtLocationStatus);
		//  notify of GPS status - enhancement - use image of GPS or crossed out GPS for room
		if (isGPSEnabled) {
			txLocationStatus.setText("Using Phone Location");
		} else if (!isGPSEnabled) {
			txLocationStatus.setText("Phone GPS not available");
		}
	}

private void updateWithNewLocation(Location location) {
//    TextView myLocationText = (TextView) findViewById(R.id.text);
    String latLongString = "";
    if (location != null) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
//        latLongString = "Lat:" + lat + "\nLong:" + lng;
    } else {
//        latLongString = "No location found";
    }
//    myLocationText.setText("Your Current Position is:\n" + latLongString);
}

private final LocationListener locationListener = new LocationListener() {

    public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
    }

    public void onProviderDisabled(String provider) {
        updateWithNewLocation(null);
    }

    public void onProviderEnabled(String provider) {}

    public void onStatusChanged(String provider,int status,Bundle extras){}
};

	
	/*
	 * Record the Values entered in the report, send via PostRequest/GetRequest... ?
	 */
	public void sendReport(View v) {
		saveReportValues();
		
		Log.i("GG","activity = " + mVesselActivity + " vesselID= " + mVesselID + "  TypedDirection= " 
		+ mDirection + "  LocationInfo= " + mLocationInfo + "  Date/Time= " + mDateTime);

        new Thread() {
            @Override
            public void run() {
                try {
                    postReport();
                } catch (Exception e) {
                    Log.e("TrawTraker", "Failed to post report", e);
                }
            }
        }.start();

		TextView txReportStatus = (TextView) findViewById(R.id.txtSendReport);
		txReportStatus.setText("report send!");
	}

    private void postReport() {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://jasonchen57-test.appspot.com//submit");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            // TODO: Set the time appropriately based on mDateTime, which should be a time in seconds rather than an
            // arbitrary String.
            nameValuePairs.add(new BasicNameValuePair("date", String.valueOf(System.currentTimeMillis() / 1000.0)));
            nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
            nameValuePairs.add(new BasicNameValuePair("long", String.valueOf(longitude)));
            nameValuePairs.add(new BasicNameValuePair("vessel_id", mVesselID));

            // TODO: Set the comment (we don't actually set mComment right now)
            nameValuePairs.add(new BasicNameValuePair("comment", "hard coded comment"));

            // TODO: Fill in the remaining fields that we wish to upload.
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
	
	public void takePhoto(View v) {
		// somehow go to camera and take picture, return with picture reference to app...?
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start__photo_location, menu);
		return true;
	}

	
	/**
	 * Save Report - restore entered values to device from sharedPreferences - ie. don't have internet...
	 * FIXME
	 */
	private void restoreFilterPreferences() {
		SharedPreferences filters = getSharedPreferences(reportValues, 0);
//	 // for example.... set values and keys into the filter...
//		int tabPreferences = filters.getInt("disaster_pref", 0);
//		tabPreferences.setSelection(imagePrefSelection);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveFilterPreferences();
	}
	
	/**
	 * Save Report - save entered values to device to sharedPreferences - ie. don't have internet...
	 */
	private void saveFilterPreferences() {
		SharedPreferences filters = getSharedPreferences(reportValues, 0);
		SharedPreferences.Editor editor = filters.edit();
//		//for example..Start_PhotoLocation. set value and keys
//		editor.putInt("disaster_pref", tabPreferences.getSelectedItemPosition()); 
		editor.commit(); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
