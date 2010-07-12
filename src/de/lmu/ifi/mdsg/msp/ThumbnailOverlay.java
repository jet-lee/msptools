package de.lmu.ifi.mdsg.msp;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import de.lmu.ifi.mdsg.msp.MSPClientActivity;
import de.lmu.ifi.mdsg.msp.Footprint;

public class ThumbnailOverlay extends Overlay{

	private List<Footprint> footprints;
	private boolean tapable;
	private MSPClientActivity activity;
	
	private Iterator<Footprint> iterator = null;

	private Projection projection;
	
	public ThumbnailOverlay(MSPClientActivity activity){
		this.activity = activity;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		//TODO draw Footprints
				
		projection = mapView.getProjection();

		
		if(activity.footprints!=null && isTapable())
		{
			iterator = activity.footprints.iterator();
			while(iterator.hasNext())
			{
				
				
				Footprint footprint = iterator.next();
				
				GeoPoint geoPoint = footprint.getLocation();
					
				Point p = new Point();
				projection.toPixels(geoPoint, p);

				
				Rect bmpRec = new Rect(p.x,p.y, p.x+45, p.y+45);
				canvas.drawBitmap(footprint.getThumbnail(), null, bmpRec, null);			
				
			}
		}
		
		
		
		
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		if(isTapable()){
			Footprint chosen = null;
			//TODO calculate tapped Footprint
			
			System.out.println("--------------> TAB TAB TAB TAB");
			if(activity.footprints!=null)
			{
				iterator = activity.footprints.iterator();
			}
			
			while(iterator!=null && iterator.hasNext())
			{
				Footprint footprint = iterator.next();
				GeoPoint geoPoint = footprint.getLocation();
				
				Point p_foot = new Point();
				Point p_tab = new Point();
				
				projection.toPixels(geoPoint, p_foot);
				projection.toPixels(p, p_tab);
				
				if(p_tab.x>=p_foot.x & 
				   p_tab.y>=p_foot.y &
				   p_tab.x<=(p_foot.x+45) &
				   p_tab.y<=(p_foot.y+45)
				  )
				{
					chosen = footprint;
					break;
				}
					
					
			}
			
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
