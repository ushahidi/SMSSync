package org.addhen.smssync;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	private NotificationManager mNM;
	private LocationManager locationManager;
	private Timer timer = new Timer();
	private static final long UPDATE_INTERVAL = 5000;
	private String currentLocation = "";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override  
	public void onCreate() {  
		super.onCreate();
		locationManager= (LocationManager) 
	    getSystemService(Context.LOCATION_SERVICE);
		/*mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification();
        Toast.makeText(this, getLocation()+" here", Toast.LENGTH_SHORT).show();
		Log.i("Location service", currentLocation +"Hello Henry" );*/
	}
	
	public HashMap<String,String> getAddr(Location location){
		HashMap <String,String> sLocation = new HashMap<String,String>();
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		Address addr;
		Double lat = location.getLatitude();
		Double lon = location.getLongitude();
		try{
			
			List<Address> myList = gc.getFromLocation(lat, lon, 1);
			addr = myList.get(0);
			
		}catch(Exception e){
			return sLocation;
		}
		
		sLocation.put("latitude", String.format("%f",lat));
		sLocation.put("longitude", String.format("%f",lon));
				
		return sLocation;
	}
	
	
	private boolean getGPSStatus()
	{
		String allowedLocationProviders =
			Settings.System.getString(getContentResolver(),
			Settings.System.LOCATION_PROVIDERS_ALLOWED);
	 
		if (allowedLocationProviders == null) {
			allowedLocationProviders = "";
		}
	 
		return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
	}	
	 
	private void setGPSStatus(boolean pNewGPSStatus)
	{
		String allowedLocationProviders =
			Settings.System.getString(getContentResolver(),
			Settings.System.LOCATION_PROVIDERS_ALLOWED);
	 
		if (allowedLocationProviders == null) {
			allowedLocationProviders = "";
		}
		
		boolean networkProviderStatus =
			allowedLocationProviders.contains(LocationManager.NETWORK_PROVIDER);
	 
		allowedLocationProviders = "";
		if (networkProviderStatus == true) {
			allowedLocationProviders += LocationManager.NETWORK_PROVIDER;
		}
		if (pNewGPSStatus == true) {
			allowedLocationProviders += "," + LocationManager.GPS_PROVIDER;
		}	
	 
		Settings.System.putString(getContentResolver(),
			Settings.System.LOCATION_PROVIDERS_ALLOWED, allowedLocationProviders);	   
	 
		try
		{
			Method m =
				locationManager.getClass().getMethod("updateProviders", new Class[] {});
			m.setAccessible(true);
			m.invoke(locationManager, new Object[]{});
		}
		catch(Exception e)
		{
			Log.e("GetClassName", e.getClass().getName());
		}
		return;
	}
	
	public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);
        
        if( ! getGPSStatus() )
        	setGPSStatus(true);

        LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				PendingIntent dummyEvent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.addhen.whereami.IGNORE_ME"), 0);
				//setLocation(getAddr(location));
				
				//TODO Send location details.
				
				//NikoWapi.sendLocationInfo(getAddr(location));
				//SmsManager.getDefault().sendTextMessage(intent.getExtras().getString("dest"), null, getAddr(location), dummyEvent, dummyEvent);
				
				locationManager.removeUpdates(this);
			}
            
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
		}; 
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
   
		Location location = locationManager.getLastKnownLocation("gps");
		if (location == null)
			location = locationManager.getLastKnownLocation("network");

		PendingIntent dummyEvent = PendingIntent.getBroadcast(this, 0, new Intent("org.addhen.whereami.IGNORE_ME"), 0);
		//setLocation(getAddr(location));
		
		//NikoWapi.sendLocationInfo(getAddr(location));
		
		//TODO send location details here
		
		//SmsManager.getDefault().sendTextMessage(intent.getExtras().getString("dest"), null, getAddr(location), dummyEvent, dummyEvent);
	
	}
	
	public void onDestroy(){
		shutdownLocationService();	
	}
	
	/**
	 * Start the service
	 * 
	 */
	public void startLocationService() {
		timer.scheduleAtFixedRate(
			new TimerTask() {
			        public void run() {
			        	dispatchLocationInfo();
			        }
			      },
			      0,
			      UPDATE_INTERVAL);
			  //Log.i(getClass().getSimpleName(), "Timer started!!!");
		Toast.makeText(this, "LocationService Started! "+getLocation(), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * shutdown service
	 */
	public void shutdownLocationService() {
		
		if( timer != null ) {
			timer.cancel();
		}
		Toast.makeText(this, "LocationService Stopped!", Toast.LENGTH_SHORT).show();
		//Log.i(getClass().getSimpleName(), "Timer stopped!!!");
	}
	
	/**
	 * Send location updates to a webservice
	 */
	public void dispatchLocationInfo() {
		Toast.makeText(this, getLocation()+" here", Toast.LENGTH_SHORT).show();
		Log.i("Location service", currentLocation +"Hello Henry" );
	}
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.app_name);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SmsSync.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                       text, contentIntent);
        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.app_name, notification);
    }
    
    public void setLocation( String location ) {
    	this.currentLocation = location;
    }
    
    public String getLocation() {
    	return this.currentLocation;
    }
	
	
}

