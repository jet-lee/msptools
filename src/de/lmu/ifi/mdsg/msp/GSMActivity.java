package de.lmu.ifi.mdsg.msp;

import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class GSMActivity extends MapActivity {
	

	int signalstrength = -1;
	TextView gsmTextView;
	GsmMonitor gsmMonitorView;
	
	public static TelephonyManager telephonyManager;
	GsmCellLocation gsmCellLocation;
	List<NeighboringCellInfo> neighboringCells;
	
	MapView mapView;
	LocationManager locationManager;
	
	ShowPositionOverlay showOverlay;
	MapController mapController;

/**********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_gsm_layout);
		
		gsmTextView = (TextView) findViewById(R.id.gsmtextview);
		

		
		gsmTextView.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent monitoractivity = new Intent(GSMActivity.this,GsmMonitorActivity.class);
				startActivity(monitoractivity);
			}			
		}
		);
			
		// PhoneStateListener 
		PhoneStateListener listener = new PhoneStateListener(){
			
			//Signalstaerke
			public void onSignalStrengthChanged(int asu){
				
				signalstrength = asu;
			
							
			}
			
					
			//CellLocation	
			public void onCellLocationChanged(CellLocation location){
				
				StringBuffer buffer = new StringBuffer();
				
				buffer.append("CellLocation :"+location.toString()+" Signalstaerke :"+ (-113 + 2*signalstrength)
					+" CellID :"+gsmCellLocation.getCid()+ " Networktyp :"+telephonyManager.getNetworkType());
				
				for(int i = 0; i < neighboringCells.size(); i++){
					buffer.append(" Neighbor Signalstärke " + i + ": " + (-113 + 2*neighboringCells.get(i).getRssi()));
					buffer.append(" Neighbour CellID "+ i +": "+neighboringCells.get(i).getCid());
				}

				
				
				gsmTextView.setText(buffer.toString());		
				
			}		
		
		};
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
		neighboringCells = (List<NeighboringCellInfo>) telephonyManager.getNeighboringCellInfo();
		
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);
		
		
		
		//*********** TEIL 2 *****************//
		
		mapView = (MapView)findViewById(R.id.gsmMapView);
		showOverlay = new ShowPositionOverlay();
		showOverlay.setLocation(new Location("gps"));
		mapView.setBuiltInZoomControls(true);
		
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(showOverlay);
		mapView.postInvalidate();

		mapController = mapView.getController();
		mapController.setZoom(17);


		LocationListener locationListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
			}
			
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				

				
			}
			
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				System.out.println("--------------------> LOCATION BLA ");
				showOverlay.setLocation(location);
				
				Double longitude = location.getLongitude()*1E6;
				Double latitude = location.getLatitude()*1E6;
				GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
				mapController.animateTo(geoPoint);
				

				
			
			}
		};
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// 1. Provier - 2. mindest Zeit - 3. mindest Distanz - 4. listener
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2, 100, locationListener);

		


		
	}
	
/**********************************************************************************************/
	
	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	

	
}
