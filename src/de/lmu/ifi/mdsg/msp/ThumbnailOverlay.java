package de.lmu.ifi.mdsg.msp;

import java.util.List;

import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.lmu.ifi.mdsg.msp.MSPClientActivity;
import de.lmu.ifi.mdsg.msp.Footprint;

public class ThumbnailOverlay extends Overlay{

	private List<Footprint> footprints;
	private boolean tapable;
	private MSPClientActivity activity;
	
	public ThumbnailOverlay(MSPClientActivity activity){
		this.activity = activity;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		//TODO draw Footprints
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		if(isTapable()){
			Footprint chosen = null;
			//TODO calculate tapped Footprint
			if(chosen != null){
				activity.activateFotoView(chosen);
			}
		}
		return super.onTap(p, mapView);
		
	}

	public void setTapable(boolean tapable) {
		this.tapable = tapable;
	}

	public boolean isTapable() {
		return tapable;
	}

	public void setFootprints(List<Footprint> footprints) {
		this.footprints = footprints;
	}

	public List<Footprint> getFootprints() {
		return footprints;
	}

}
