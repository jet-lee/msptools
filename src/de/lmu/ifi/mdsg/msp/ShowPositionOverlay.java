package de.lmu.ifi.mdsg.msp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class ShowPositionOverlay extends Overlay {

	private Location location;
	private final int radius = 5;
	private final String hereIAm = "Here I Am";
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		if(!shadow){
			//get current location
			Double longitude = location.getLongitude()*1E6;
			Double latitude = location.getLatitude()*1E6;
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
			Point p = new Point();
			projection.toPixels(geoPoint, p);
			
			//set up paint
			RectF oval = new RectF(p.x-radius, p.y-radius, p.x+radius, p.y+radius);
			Paint paint = new Paint();
			paint.setARGB(250, 255, 0, 0);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setStyle(Style.FILL_AND_STROKE);
			
			Paint backPaint = new Paint();
			backPaint.setARGB(175, 50, 50, 50);
			backPaint.setAntiAlias(true);
			
			int width = (int)paint.measureText(hereIAm);
			RectF backRect = new RectF(p.x+2+radius, p.y-3*radius, p.x+2*radius+width+3, p.y+radius);
			//draw marker
			canvas.drawOval(oval, paint);
			canvas.drawRoundRect(backRect, 5, 5, backPaint);
			
			canvas.drawText(hereIAm, p.x+2*radius, p.y, paint);
		}
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
