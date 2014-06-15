package com.example.trawtracker;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.SharedPreferences;
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

public class Start_PhotoLocation extends Activity {

	private String reportValues;  //for shared preferences....?
	private EditText edActivity, edVesselID, edDirection, edComments, edLocationInfo, edDateTime;
	public String mVesselActivity, mVesselID, mDirection, mComment, mLocationInfo, mDateTime;
	
	
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
		Boolean haveGPS = false;    // true if phone GPS coordinates are available, else false
		
		// FIXME - HOW to get the GPS coord from the phone?? device manager settings? :/
		
		TextView txLocationStatus = (TextView) findViewById(R.id.txtLocationStatus);
		//  notify of GPS status - enhancement - use image of GPS or crossed out GPS for room
		if (haveGPS) {
			txLocationStatus.setText("Using Phone Location");
		} else if (!haveGPS) {
			txLocationStatus.setText("Phone GPS not available");
		}
	}
	
	/*
	 * Record the Values entered in the report, send via PostRequest/GetRequest... ?
	 */
	public void sendReport(View v) {
		saveReportValues();
		
		Log.i("GG","activity = " + mVesselActivity + " vesselID= " + mVesselID + "  TypedDirection= " 
		+ mDirection + "  LocationInfo= " + mLocationInfo + "  Date/Time= " + mDateTime);
		
		TextView txReportStatus = (TextView) findViewById(R.id.txtSendReport);
		txReportStatus.setText("report send!");
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
