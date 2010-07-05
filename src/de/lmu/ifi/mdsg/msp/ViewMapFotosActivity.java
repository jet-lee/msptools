package de.lmu.ifi.mdsg.msp;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import de.lmu.ifi.mdsg.msp.Footprint;

public class ViewMapFotosActivity extends Activity{
	
	ImageView fotoView;
	TextView descriptionView;
	static Footprint f;
	
	public static void setFootprint(Footprint footp){
		f = footp;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_image_layout);
		fotoView = (ImageView)findViewById(R.id.foto_view);
		descriptionView = (TextView)findViewById(R.id.description_textview);
		
		descriptionView.setText(f.getDescription());
		Bitmap bm;
		try{
			Log.w(MSPClientActivity.class.getName(), "trying get image ...");
			Authenticator ath = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					PasswordAuthentication pw = new PasswordAuthentication("mspwebhome", "1u1ifalsch".toCharArray());
					return pw;
				}
			};
			
			Authenticator.setDefault(ath);
			HttpURLConnection conTh = (HttpURLConnection) f.getImage().openConnection();
			bm = BitmapFactory.decodeStream(conTh.getInputStream());
			Log.w(MSPClientActivity.class.getName(), "displaying image in screen ...");
			fotoView.setImageBitmap(bm);
			fotoView.invalidate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
