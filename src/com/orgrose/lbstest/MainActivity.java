package com.orgrose.lbstest;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager locationManager;
	private Location location;
	private Toast mToast;
	
	private TextView latitude;
	private TextView longtitude;
	private TextView address0;
	private TextView address1;
	private TextView address2;
	private TextView address3;
	private TextView address4;	
	private TextView providerT;
	private TextView receiveT;
	private TextView statusT;
	private Button resetBtn;
	private int receive;
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i("BB", "onResume");
		checkMyLocation();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		receive=0;
		mToast = Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT);
		latitude = (TextView) findViewById(R.id.latitude);
		longtitude = (TextView) findViewById(R.id.longtitude);
		address0 = (TextView) findViewById(R.id.address0);
		address1 = (TextView) findViewById(R.id.address1);
		address2 = (TextView) findViewById(R.id.address2);
		address3 = (TextView) findViewById(R.id.address3);
		address4 = (TextView) findViewById(R.id.address4);
		providerT = (TextView) findViewById(R.id.provider);
		receiveT = (TextView) findViewById(R.id.receive);
		statusT = (TextView) findViewById(R.id.status);
		;;;;;
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		
		resetBtn = (Button) findViewById(R.id.reset);
		resetBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				checkMyLocation();
			}			
		});		
	}

	public void checkMyLocation(){
		
	   	Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
	   	receiveT.setText(Integer.toString(receive));
	   	String provider = locationManager.getBestProvider(criteria, true);
	   	
	   	if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			createGpsDisabledAlert();
		}
	   	else{
	   		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);	
	   	}
	   	
	   	if(provider == null){ //gps off이면 network통해서 받아오도록..
	   		Log.i("BB", "no GPS Provider");	   		
	   		//provider = LocationManager.NETWORK_PROVIDER;
	   		provider = LocationManager.NETWORK_PROVIDER;
	   		location = locationManager.getLastKnownLocation(provider);
	   	}
	   	Log.i("BB", "provider="+provider);
	   	providerT.setText(provider);	   	
	   	locationManager.requestLocationUpdates(provider, 1000L, 10.0f, locationListener);
	   			   
	   	if(location == null){
	   		try{
	   			Thread.sleep(1000);
	   		}catch(InterruptedException e){
	   			e.printStackTrace();
	   		}
	   		location = locationManager.getLastKnownLocation(provider); 		    
	   	}
	   	
	   	latitude.setText(Integer.toString((int) location.getLatitude()));
	   	longtitude.setText(Integer.toString((int) location.getLongitude()));
	   	location = locationManager.getLastKnownLocation(provider);
	   	getAddress();
	}
		  
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			receive++;
//			Criteria criteria = new Criteria();	
//			criteria.setAltitudeRequired(false);
//			criteria.setBearingRequired(false);
//			criteria.setSpeedRequired(false);			
//			
//		   	String provider = locationManager.getBestProvider(criteria, true);
			MainActivity.this.location = location;			
			new showToast(MainActivity.this,mToast,"Location changed.");
			Log.i("BB", "Location changed.");
		   	latitude.setText(Double.toString(MainActivity.this.location.getLatitude()));
		   	longtitude.setText(Double.toString(MainActivity.this.location.getLongitude()));
//		   	providerT.setText(provider);
		   	receiveT.setText(Integer.toString(receive));
		   	getAddress();
		}

		@Override
		public void onProviderDisabled(String provider) {
			statusT.setText("Service Disabled");      
		}

		@Override
		public void onProviderEnabled(String provider) {
			statusT.setText("Service Enabled");  
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
            String sStatus = "";
            switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                 sStatus = "OUT_OF_SERVICE";
                 break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                 sStatus = "TEMPORARILY_UNAVAILABLE";
                 break;
            case LocationProvider.AVAILABLE:
                 sStatus = "AVAILABLE";
                 break;
            }
            statusT.setText(provider + "Status changed : " + sStatus);   
		}
   
	};
	
	public void getAddress() {
		Geocoder geoCoder = new Geocoder(this);
		double lat = location.getLatitude();
		double lng = location.getLongitude();
  
		List<Address> addresses = null;
		try {
			addresses = geoCoder.getFromLocation(lat, lng, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(addresses!=null&&addresses.size()>0){
			Address mAddress = addresses.get(0);
			//String Area = mAddress.getCountryName();
			String mAddressStr = mAddress.getCountryName()+" "
					+mAddress.getPostalCode()+" "
					+mAddress.getLocality()+" "
					+mAddress.getThoroughfare()+" "
					+mAddress.getFeatureName();
			address0.setText(mAddress.getCountryName());
			address1.setText(mAddress.getPostalCode());
			address2.setText(mAddress.getLocality());
			address3.setText(mAddress.getThoroughfare());
			address4.setText(mAddress.getFeatureName());
			
		}
	}

	private void createGpsDisabledAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("GPS disabled. Turn on GPS")
				.setCancelable(false)
				.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						showGpsOptions();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();						
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void showGpsOptions(){
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}	
	
	private class showToast extends Activity {
		public showToast(Context c,Toast mToast, String mText) {
			if (mText != null) {
				if (mToast == null)
					mToast = Toast.makeText(c, mText, Toast.LENGTH_SHORT);
				else
					mToast.setText(mText);
				mToast.show();
			}
		}
	}	

}
