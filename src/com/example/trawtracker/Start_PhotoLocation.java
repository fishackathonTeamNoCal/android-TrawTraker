package com.example.trawtracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

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
    private static final int REQUEST_TAKE_PHOTO = 1;

	private String reportValues;  //for shared preferences....?
	private EditText edActivity, edVesselID, edDirection, edComments, edLocationInfo, edDateTime;
	public String mVesselActivity, mVesselID, mDirection, mComment, mLocationInfo, mDateTime;
	public double latitude, longitude; 
	
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	double CURRENT_TIME;
	String txCURRENT_TIME;

    String mCurrentPhotoPath;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start__photo_location);

		// Open Camera immediately to take photo
		takePhoto();
		
		// Set GPS when Screen Opens
		getPhoneGPScord();
		
		// Set Current Time in form (but allow to type/change form)
		CURRENT_TIME = System.currentTimeMillis() / 1000; // current time in seconds
		txCURRENT_TIME = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(CURRENT_TIME*1000);
		edDateTime = (EditText) findViewById(R.id.txtDateTime);
		edDateTime.setText(txCURRENT_TIME);
	}

	public void takePhoto() {
	    // create Intent to take a picture and return control to the calling application
        dispatchTakePictureIntent();

		TextView txPhotoStatus = (TextView) findViewById(R.id.photo_text);
		txPhotoStatus.setText("image captured!");
	}
	
    private void dispatchTakePictureIntent() {
		TextView txPhotoStatus = (TextView) findViewById(R.id.photo_text);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
        		txPhotoStatus.setText("image saved...");
            } catch (IOException ex) {
        		txPhotoStatus.setText("image error");
                Log.e("TrawTraker", "Error while saving photo.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        		txPhotoStatus.setText("image captured!");
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
		// Get GPS location from phone - long and lat in degree (or 0,0 if not an option)

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
		
		Boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		ImageView mGPSicon = (ImageView) findViewById(R.id.imGPSStatus);

		if (isGPSEnabled) {
			mGPSicon.setImageDrawable(getResources().getDrawable(R.drawable.ic_gps_icon_green));
		} else if (!isGPSEnabled) {
			mGPSicon.setImageDrawable(getResources().getDrawable(R.drawable.ic_gps_icon_red));
		}
	}

private void updateWithNewLocation(Location location) {
    String latLongString = "";
    if (location != null) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        latLongString = "Lat:" + lat + "\nLong:" + lng;
    } else {
        latLongString = "No location found";
    }
    Log.i("TT","Your Current Position is:\n" + latLongString);
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
	 * Record the Values entered in the report
	 */
	public void sendReport(View v) {
		saveReportValues();
		
		Log.i("TT","activity = " + mVesselActivity + " vesselID= " + mVesselID + "  TypedDirection= " 
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

		Intent returnMainScreen = new Intent(getBaseContext(),MainInfo.class);
		startActivity(returnMainScreen);
	}

    private void postReport() {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://jasonchen57-test.appspot.com//submit");
        Log.i("Requestd Connection", httppost.getURI().toString());
        
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            // TODO: Set the time appropriately based on mDateTime, which should be a time in seconds rather than an
            // arbitrary String.
            nameValuePairs.add(new BasicNameValuePair("date", String.valueOf(CURRENT_TIME)));
            nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
            nameValuePairs.add(new BasicNameValuePair("long", String.valueOf(longitude)));
            nameValuePairs.add(new BasicNameValuePair("vessel_id", mVesselID));
            
            nameValuePairs.add(new BasicNameValuePair("comment", mComment));
            nameValuePairs.add(new BasicNameValuePair("heading", mDirection));
            nameValuePairs.add(new BasicNameValuePair("location_typed", mLocationInfo));
            nameValuePairs.add(new BasicNameValuePair("date_time_typed", mDateTime));

            int targetW = 600;
            int targetH = 600;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // FIXME: We set the quality to 50, out of 100. What's the appropriate value?
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            nameValuePairs.add(new BasicNameValuePair("encodedImg", encodedImage));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.d("TT", "send HTTPresponse");
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
