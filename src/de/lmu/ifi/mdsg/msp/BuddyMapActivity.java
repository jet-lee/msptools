package de.lmu.ifi.mdsg.msp;

import java.util.List;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
public class BuddyMapActivity extends MapActivity{


	
	MapView mapView;
	
	BuddyPositionOverlay showOverlay;
	MapController mapController;

/**********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddy_map_layout);
	
			

		
	
		
		
		//*********** OVERLAY MAP GOOGLE  *****************//
		

		mapView = (MapView)findViewById(R.id.buddyMapView);
		showOverlay = new BuddyPositionOverlay();
		
		
		
		mapView.setBuiltInZoomControls(true);
		
		List<Overlay> overlays = mapView.getOverlays();
		
		overlays.add(showOverlay);
		mapView.postInvalidate();

		mapController = mapView.getController();
		mapController.setZoom(17);
		
		Location location = ChatClientActivity.otherLocations.get(ChatClientActivity.mynickname);
		
		if(ChatClientActivity.otherLocations.containsKey(ChatClientActivity.mynickname)&&(location!=null))
		{
			
			Double longitude = location.getLongitude()*1E6;
			Double latitude = location.getLatitude()*1E6;
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
			mapController.animateTo(geoPoint);
		}


		
	}
	
/**********************************************************************************************/
	
	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	

	
}