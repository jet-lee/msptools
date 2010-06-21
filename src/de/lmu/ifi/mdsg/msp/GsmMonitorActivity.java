package de.lmu.ifi.mdsg.msp;

import android.app.Activity;
import android.os.Bundle;



public class GsmMonitorActivity extends Activity {

/**********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(new GsmMonitor(this));

	}
	
/**********************************************************************************************/
}
