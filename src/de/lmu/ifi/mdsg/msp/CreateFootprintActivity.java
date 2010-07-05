package de.lmu.ifi.mdsg.msp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class CreateFootprintActivity extends MapActivity {
	
	public final static int PHOTO_REQUEST_CODE = 1;
	public final static int UPLOAD_REQUEST_CODE = 2;
	public final static String DESCRIPTION_EXTRA = "descr";
	public final static String BUNDLE_EXTRA = "dataBundle";
	public final static String BITMAP_EXTRA = "data";
	public final static int TAKE_PICTURE = 1;
	static int counter = 0;
	ImageView image;
	EditText text;
	MapView miniMap;
	MapController mapControl;
	MyLocationOverlay overlay;
	Button takePicture;
	Button save;
	Location locationPictureTaken;
	Bundle bundle;

	private LocationManager locMan;
	String fileName;
	
	Bitmap thumbnail;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_mspclient_layout);
		image = (ImageView)findViewById(R.id.camera_image_view);
		takePicture = (Button)findViewById(R.id.picture_button);
		save = (Button)findViewById(R.id.save_button);
		miniMap = (MapView)findViewById(R.id.mini_map_view);
		text = (EditText)findViewById(R.id.image_information_textview);
		//get the minimap right
		FrameLayout mapFrame = (FrameLayout)findViewById(R.id.framer);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
		mapFrame.removeAllViews();
		mapFrame.addView(miniMap, params);
		
		takePicture.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PICTURE);
			}
		});
		
		save.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String description = text.getText().toString();
				while(locationPictureTaken == null){
						locationPictureTaken = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(locationPictureTaken == null){//we got no gps fix
							locationPictureTaken = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						}
						//FIXME while may not terminate!
					}
					String descr = "";
					descr += "Location: " + locationPictureTaken.getLatitude() + " " + locationPictureTaken.getLongitude();
					descr += "\nDescription: " + description;
					
					Intent intent = new Intent(CreateFootprintActivity.this, UploadActivity.class);
					intent.putExtra(DESCRIPTION_EXTRA, descr);
					intent.putExtra(BUNDLE_EXTRA, thumbnail);
					
					startActivityForResult(intent, UPLOAD_REQUEST_CODE);
				}
		});
		
		mapControl = miniMap.getController();
		// configure options
		miniMap.setStreetView(true);
		mapControl.setZoom(18);

		// show location on map
		overlay = new MyLocationOverlay(getApplicationContext(), miniMap);
		miniMap.getOverlays().add(overlay);
		overlay.enableMyLocation();
		
		locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 10f, new LocationListener() {
			
			
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PHOTO_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				locationPictureTaken = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				//TODO get the Bitmap out of the data-Intent
				
				thumbnail = data.getParcelableExtra("data");
			
				ImageView imageView = (ImageView) findViewById(R.id.camera_image_view);
				
				imageView.setImageBitmap(thumbnail);
							
			}
		}
		if(requestCode == UPLOAD_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				Toast.makeText(getApplicationContext(),
						"Upload was successful!" ,
						Toast.LENGTH_SHORT).show();
				this.finish();
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	

}