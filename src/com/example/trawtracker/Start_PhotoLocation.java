package com.example.trawtracker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
	
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	
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
		    // create Intent to take a picture and return control to the calling application
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

		    // start the image capture Intent
		    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		    
			TextView txPhotoStatus = (TextView) findViewById(R.id.photo_text);
			txPhotoStatus.setText("image captured!");
	}
	


	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Image saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }

	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Video saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        } else {
	            // Video capture failed, advise user
	        }
	    }
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
