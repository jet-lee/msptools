package de.lmu.ifi.mdsg.msp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	ListView modulesListView;
	ArrayAdapter<String> aa;
	String[] mspModules;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		modulesListView = (ListView) findViewById(R.id.modulesListView);

		final Resources res = getResources();

		mspModules = res.getStringArray(R.array.msp_modules_array);

		aa = new ArrayAdapter<String>(this, R.layout.list_item, mspModules);
		modulesListView.setAdapter(aa);

		modulesListView.setTextFilterEnabled(true);

		modulesListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// GSMActivity
				switch(position)
				{
				case 0: // GSM Activity 
					Intent gsmActivity = new Intent(MainActivity.this,GSMActivity.class);
					
					startActivity(gsmActivity);
					break;
					
				case 1: // CHAT CLIENT
					 Intent chatActivity = new Intent(MainActivity.this,ChatClientActivity.class);
					 
					 startActivity(chatActivity);
					 break;
					 
				case 2: // GPS Activity 
					
					 Intent gpsActivity = new Intent(MainActivity.this,GPSActivity.class);
					 
					 startActivity(gpsActivity); 
					 break;
					 
				case 3: // Ekahau Activity
					
					Intent ekahauActivity = new Intent(MainActivity.this,EkahauActivity.class);
					
					startActivity(ekahauActivity);
					break;
					
				}
				

			}
		});
	}
}