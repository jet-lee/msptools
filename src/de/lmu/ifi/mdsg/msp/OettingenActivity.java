package de.lmu.ifi.mdsg.msp;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class OettingenActivity extends Activity{
	
	LocationManager locationManager;
	GeoPoint geoPoint = new GeoPoint(0,0);
	
	OettingenView oettingenView;
	
	/**********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		oettingenView = new OettingenView(this);
		this.setContentView(oettingenView);
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// 1. Provier - 2. mindest Zeit - 3. mindest Distanz - 4. listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 100, new LocationListener()
		  {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
				Double longitude = location.getLongitude()*1E6;
				Double latitude = location.getLatitude()*1E6;
				geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
				
				oettingenView.setLocation(location);
				oettingenView.invalidate();
				
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
		  });
		

		

	}
	
	
	
	
	
	/**********************************************************************************************/

}
