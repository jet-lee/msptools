package de.lmu.ifi.mdsg.msp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import de.lmu.ifi.mdsg.msp.EkahauView;

public class EkahauMapActivity extends Activity {
	
	EkahauView myView;
	PositionReceiver positionReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ekahau_map_layout);
		//TODO get the EkahauView
		
		myView = (EkahauView) findViewById(R.id.view_of_ifi_ekahau);
		
		positionReceiver = new PositionReceiver();
		
	}

	class PositionReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			//TODO get the position data
			
			
			int x = intent.getIntExtra("x", 0);
			int y = intent.getIntExtra("y", 0);
			int id = intent.getIntExtra("id", -1);

			
			System.out.println(" --------------------__> x" + x +" y " + y );
	    	myView.setPosition(x, y, id);

		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//TODO create and start a new PositionReceiver
		Resources res = getResources();
		registerReceiver(positionReceiver, new IntentFilter(res.getString(R.string.ekahau_update_intent_name)));

	}
	
	@Override
	protected void onStop() {
		//TODO stop the PositionReceiver
		super.onStop();
		unregisterReceiver(positionReceiver);
	}

}
