package de.lmu.ifi.mdsg.msp;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class GPSActivity extends Activity {

	
	LocationManager locationManager;
	GpsStatus gpsStatus;
	Iterable<GpsSatellite> satellites;
	GeoPoint geoPoint;


	TextView gpsOverview;
	Button oettingenbutton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_gps_layout);
		
		gpsOverview = (TextView)findViewById(R.id.gpstextview);
		oettingenbutton = (Button)findViewById(R.id.buttonOettingen);
		
		
		oettingenbutton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent oettingenActivity = new Intent(GPSActivity.this,OettingenActivity.class);
				
				startActivity(oettingenActivity);
			}
			
			
		});
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		

		locationManager.addGpsStatusListener(new GpsStatus.Listener() {
			
			public void onGpsStatusChanged(int event) {
				
				
				switch(event)
				{
				case GpsStatus.GPS_EVENT_STARTED:
					
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					
					gpsStatus = locationManager.getGpsStatus(null);				
					satellites = gpsStatus.getSatellites();
					
					String ausgabe = "";
					
					Iterator<GpsSatellite> iter = satellites.iterator();
					
					while(iter.hasNext())
					{
						GpsSatellite g = iter.next();
						
						
						// Azimuth: Horizontalwinkel (Himmelsrichtung), in der sich der Satellit relativ zum Standort befindet
						// Elevation: Vertikalwinkel der Satellitenposition relativ zum Standort
						ausgabe +="Satellite NR: " + g.getPrn() +"\n";
						ausgabe +="Azimuth: " + g.getAzimuth() + "\n";
						ausgabe +="Elevation: " + g.getElevation()+"\n";
						
						if(geoPoint != null){
							ausgabe += "Breitengrad: " + geoPoint.getLatitudeE6() +"\n";
							ausgabe += "Laengengrad: " + geoPoint.getLongitudeE6() +"\n";
						}
						
						ausgabe +="---------------------------------------------------------- \n";
					
						
					}
					
					gpsOverview.setText(ausgabe);
					

					break;
				}

			}
		}
			
		);
		
		// 1. Provier - 2. mindest Zeit - 3. mindest Distanz - 4. listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 100, new LocationListener()
		  {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
				Double longitude = location.getLongitude()*1E6;
				Double latitude = location.getLatitude()*1E6;
				geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
				
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
	
	
	
	
}
