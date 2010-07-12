package de.lmu.ifi.mdsg.msp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import de.lmu.ifi.mdsg.msp.Footprint;
import de.lmu.ifi.mdsg.msp.ThumbnailOverlay;

public class MSPClientActivity extends MapActivity {
	
	public final static String SERVER = "http://mspwebhome:1u1ifalsch@msp.mobile.ifi.lmu.de";
	static int counter = 0;
	MapView maxiMap;
	MapController mapControl;
	MyLocationOverlay overlay;
	ThumbnailOverlay thumbnails;
	Button takePicture;
	CheckBox viewPicture;

	private LocationManager locMan;
	
	public List<Footprint> footprints;
	//private List<Footprint> tmp_footprints;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_mspclient_layout);
		takePicture = (Button)findViewById(R.id.sub_take_picture);
		viewPicture = (CheckBox)findViewById(R.id.sub_view_picture);
		viewPicture.setChecked(false);
		
		
		maxiMap = (MapView)findViewById(R.id.maxi_map_view);
		
		takePicture.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				Intent i = new Intent(MSPClientActivity.this, CreateFootprintActivity.class);
				startActivity(i);
			}
		});
		
		viewPicture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//TODO change ThumbnailOverlay to tapable					
					thumbnails.setTapable(true);
					maxiMap.postInvalidate();
	

				} else{
					//TODO change ThumbnailOverlay to not tapable

					thumbnails.setTapable(false);
					maxiMap.postInvalidate();
				}
			}
		});
		
		mapControl = maxiMap.getController();
		// configure options
		maxiMap.setSatellite(true);
		maxiMap.setStreetView(true);
		maxiMap.setBuiltInZoomControls(true);
		maxiMap.displayZoomControls(true);
		mapControl.setZoom(18);
		
		// show location on map
		overlay = new MyLocationOverlay(getApplicationContext(), maxiMap);
		
		maxiMap.getOverlays().add(overlay);
		overlay.enableMyLocation();
		
		
		/********************************************/
		//TODO add ThumbnailOverlay
		
		/*******/
		thumbnails = new ThumbnailOverlay(this);
		/*******/
		
		List<Overlay> overlays = maxiMap.getOverlays();
		overlays.add(thumbnails);
		maxiMap.postInvalidate();

		


		/******************************************/
		locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 20f, new LocationListener() {
			
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			
			
			public void onProviderEnabled(String provider) {
				
			}
			
			
			public void onProviderDisabled(String provider) {
				
			}
			
			
			public void onLocationChanged(Location location) {
				Double lat = location.getLatitude()*1E6;
				Double lon = location.getLongitude()*1E6;
				GeoPoint geo = new GeoPoint(lat.intValue(), lon.intValue());
				mapControl.animateTo(geo);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Thread fetchThumbnails = new Thread(){
			@Override
			public void run(){
				//TODO fetch thumbnails and add the to ThumbnailOverlay
				footprints = connect(SERVER);
				//tmp_footprints = footprints;
				
				
			}
		};
		fetchThumbnails.start();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private List<Footprint> connect(String server){
		
		try {
			Log.w(MSPClientActivity.class.getName(), "trying to connect ...");
			Authenticator ath = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					PasswordAuthentication pw = new PasswordAuthentication("mspwebhome", "1u1ifalsch".toCharArray());
					return pw;
				}
			};
			
			Authenticator.setDefault(ath);
				
			URL uri = new URL(server + "/images/footprints.xml");
			HttpURLConnection con = (HttpURLConnection) uri.openConnection();
			if (con != null) {
				ArrayList<Footprint> footprints = new ArrayList<Footprint>();
				BufferedReader read = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				Log.w(MSPClientActivity.class.getName(), "got input stream!");
				String line;
				int start, end;
				String thumbnail = null;
				String image = null;
				String description = null;
				String location = null;
				Bitmap thumbnailImage = null;
				//grep data
				while ((line = read.readLine()) != null) {
					
					Log.w(MSPClientActivity.class.getName(), line);
					
					if (line.trim().startsWith("<Footprint ")) {
						start = line.indexOf("href") + 6;
						end = line.lastIndexOf("thumbnail") - 2;
						image = line.substring(start, end);
						Log.w(MSPClientActivity.class.getName(), image);
						
						start = line.indexOf("thumbnail") + 11;
						end = line.lastIndexOf(">") - 1;
						thumbnail = line.substring(start, end);
						Log.w(MSPClientActivity.class.getName(), thumbnail);
						
							URL uriTh = new URL(SERVER + "/images/" + thumbnail);
							HttpURLConnection conTh = (HttpURLConnection) uriTh.openConnection();
							thumbnailImage = BitmapFactory.decodeStream(conTh.getInputStream());
							conTh.disconnect();
							Log.w(MSPClientActivity.class.getName(), "got Thumbnail-Bitmap from server!");
					}
					if (line.trim().startsWith("<gps>")) {
						start = line.indexOf("<gps>") + 5;
						end = line.lastIndexOf("</gps>");
						location = line.substring(start, end);
						Log.w(MSPClientActivity.class.getName(), location);
					}
					if (line.trim().startsWith("<descr>")) {
						start = line.indexOf(">");
						end = line.lastIndexOf("<");
						description = line.substring(start + 1, end);
						Log.w(MSPClientActivity.class.getName(), description);
					}
					if (line.trim().startsWith("</Footprint>")) {
						Double latE6 = Double.parseDouble(location.split(" ")[0])*1E6;
						Double longE6 = Double.parseDouble(location.split(" ")[1])*1E6;
						GeoPoint p = new GeoPoint(latE6.intValue(), longE6.intValue());
						Footprint f = new Footprint(p, description, thumbnailImage, new URL(SERVER + "/images/" + image));
						footprints.add(f);
					}
				}
				
				return footprints;
				
			}
			//got no or invalid data
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	public void activateFotoView(Footprint chosen) {
		ViewMapFotosActivity.setFootprint(chosen);
		//TODO start new ViewMapFotosActivity
		Intent viewMapFotos = new Intent(MSPClientActivity.this,ViewMapFotosActivity.class);
		startActivity(viewMapFotos);
	}

}