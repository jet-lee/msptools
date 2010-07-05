package de.lmu.ifi.mdsg.msp;

import java.net.URL;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;

public class Footprint {

	private GeoPoint location;
	private String description;
	private Bitmap thumbnail;
	private URL image;
	
	public Footprint(GeoPoint p, String descr, Bitmap thumb, URL image){
		setLocation(p);
		setDescription(descr);
		setThumbnail(thumb);
		setImage(image);
	}
	
	public void setLocation(GeoPoint location) {
		this.location = location;
	}
	public GeoPoint getLocation() {
		return location;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Bitmap getThumbnail() {
		return thumbnail;
	}
	public void setImage(URL image) {
		this.image = image;
	}
	public URL getImage() {
		return image;
	}
}
