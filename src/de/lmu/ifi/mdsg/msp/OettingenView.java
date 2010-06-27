package de.lmu.ifi.mdsg.msp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import de.lmu.ifi.mdsg.msp.R;

public class OettingenView extends ImageView{

	public static final int MARKER_RADIUS = 5;
	//scale and shift depend on the image
	/*
	public static final double scaleX = 0.000015278125;
	public static final double scaleY = 0.000022375;
	public static final double shiftX = 48.148283;
	public static final double shiftY = 11.58501;
	*/
	double lat0 =48.15086;
    double long0 = 11.593268;
	
	double lat1 = 48.14882;
	double long1= 11.595344;
	

    
    
    	
	public double scaleX = (lat1-lat0)/320;
	public double scaleY = (long1 - long0)/480;
	public double shiftX = lat0;
	public double shiftY = long0;
	
	
	private Paint markerPaint;
	private Location l;
	
	//Constructors
	public OettingenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public OettingenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public OettingenView(Context context) {
		super(context);
		init();
	}
	
	public void setLocation(Location _l){
		l = _l;
	}
	
	/**
	 * Initialize paint and image
	 */
	private void init(){
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(Color.BLUE);
		Resources res = getResources();
		setImageDrawable(res.getDrawable(R.drawable.oetti));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//if we got a location
		if(l != null){
			double longitude = l.getLongitude();
			double latitude = l.getLatitude();
			Log.i(this.getClass().getName(), "coords: " + latitude + " " + longitude);
			//transform coordinates to pixels
			float[] pixelCoords = convert(latitude, longitude);
			Log.i(this.getClass().getName(), "pixel coords: " + pixelCoords[0] + " " + pixelCoords[1]);
			//if the result is inside the bounds of the image
			if(pixelCoords[0]>=0 && pixelCoords[0]<=320 && pixelCoords[1]>=0 && pixelCoords[1]<=480){
				//draw position on image
				canvas.drawCircle(pixelCoords[0], pixelCoords[1], MARKER_RADIUS, markerPaint);
			} else {
				//log the problem
				Log.e(this.getClass().getName(), "can not display position on map");
			}
		}
		
	}
	
	/**
	 * 
	 * @param x The latitude from a WGS84 coordinate.
	 * @param y The longitude of a WGS84 coordinate.
	 * @return A pair {pixX, pixY} representing the corresponding pixel coordinates of the input.
	 */
	private float[] convert(double x, double y){
		//Point_WGS84_x = Shift_x + Point_pixel_x * Scale_x
		float pixX = (float) ((x-shiftX)/scaleX);
		//Point_WGS84_y = Shift_y + Point_pixel_y * Scale_y
		float pixY = (float) ((y-shiftY)/scaleY);
		float[] pixs = {pixX, pixY};
		return pixs;
	}
	
}
