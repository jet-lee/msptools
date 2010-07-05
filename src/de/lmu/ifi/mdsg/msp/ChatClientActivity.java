package de.lmu.ifi.mdsg.msp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;



public class ChatClientActivity extends Activity {

	/*******************************************/
	public static String mynickname = "Monster";
	
	String inputLine;
	String positionLine;
	
	public static Map<String, Location> otherLocations;

	private LocationManager locman;
	private Location location;

	Resources res;
	
	/*******************************************/
	Button send_button;
	EditText chat_eingabe;
	EditText chat_ausgabe;
	
	EditText editNickname;
	CheckBox checkBox;
	
	Button buddy_button;
	
	boolean sendPosition = false;
	
	String all_text = "";
	boolean connected;
	String line = "";
	Socket socket;
	private Handler handler = new Handler();
	/*******************************************/

	private Runnable receiveProcessing = new Runnable(){
		public void run(){
			backgroundThreadProcessing();
		}
	};
	private Runnable doUpdateUI = new Runnable(){
		public void run(){
			updateGUI();
		}
	};
	
	/**********************************************************************************************/

	
	public void setLocation(Location _loc){
		location = _loc;
		otherLocations.put(mynickname,location);		
	}

	/**********************************************************************************************/

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_chat_layout);
		
		
		try {
			socket = new Socket("129.187.214.232",4000);
			//socket = new Socket("192.168.0.10",4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		connected=true;
		
		/***********************/
		
		// Alte Zustaende neu laden
		
		if(savedInstanceState != null  && savedInstanceState.getString("mynick") != null)
		{
			mynickname = savedInstanceState.getString("mynick");
			all_text = savedInstanceState.getString("history");
			
		}
		
		
		
		/***********************/

		// Nickname eingeben
		
		editNickname = (EditText) findViewById(R.id.edit_nickname);
		
		editNickname.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					editNickname.setText("");
					return true;
				}
				
				return false;
			}
		});
				

		editNickname.setOnKeyListener(new OnKeyListener() {
			
			
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction() == KeyEvent.ACTION_DOWN){
					
					if(keyCode == KeyEvent.KEYCODE_ENTER){
						
						otherLocations.remove(mynickname);
						mynickname = editNickname.getText().toString();
						
						editNickname.setText("Your nick is: " + mynickname);
						
						if(location!=null && sendPosition)
						{
							otherLocations.put(mynickname,location);
						}
						return true;
					}
				}
				return false;
			}
		});
		
		/***********************/
		
		//CheckBOX ob Position mit geschickt wird
		
		checkBox = (CheckBox)findViewById(R.id.position_box);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					sendPosition = true;
						
					otherLocations.put(mynickname,location);
					
				} else {
					sendPosition = false;
					otherLocations.remove(mynickname);
				}
			}
		});
		
		/***********************/

		Thread thread = new Thread(null, receiveProcessing, "Background");
		thread.start();
		
		send_button = (Button) findViewById(R.id.chat_button);
		
		chat_eingabe = (EditText) findViewById(R.id.chat_eingabe);
		
		send_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) 
			{
				
				sendMessage( chat_eingabe.getText().toString() );
				chat_eingabe.setText("");
				
			}
			
			
		}
		
		);
		
		
		/***********************/
		
		//location service for position
		
		locman = (LocationManager)getSystemService(LOCATION_SERVICE);
		location = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				setLocation(location);
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				setLocation(null);
				
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

				
			}
			
		});
		
		/***********************/		
		
		// Show Buddys on MAP
		
		buddy_button = (Button) findViewById(R.id.buddy_button);
		
		buddy_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				
				Intent buddy_activity = new Intent(ChatClientActivity.this,BuddyMapActivity.class);
				
				startActivity(buddy_activity);
				
				
				
			}
		});
		
		
		/***********************/
		
		// Broadcast-Receiver aktivieren && registrieren
		
		res = getResources();

        ProximityCheckerReceiver proximityCheckerReceiver = new ProximityCheckerReceiver();
        
        registerReceiver(proximityCheckerReceiver , new IntentFilter(res.getString(R.string.found_buddy)));
		

		/***********************/
        
		otherLocations = new HashMap<String, Location>();
		
		
	}
	
	/**********************************************************************************************/
	
	//AUSGABE DES CHATs
	private void updateGUI(){
		
		
		chat_ausgabe = (EditText) findViewById(R.id.chat_ausgabe);
		
	
		
		parseString();
		
		all_text = line + "\n" + all_text;
		chat_ausgabe.setText(all_text);
		line = "";
		
		
	}
	/**********************************************************************************************/

	// AUSLESEN DES CHATs 
	private void backgroundThreadProcessing(){
		
		BufferedReader in;
		
		while(connected)
		{
			boolean end_message = false;

			
			
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

				char chr;
				
				while(!end_message)
				{
					if((chr = (char) in.read())>0)
					{
						line += chr;
					}
					
					System.out.println(" RECEIVED MESSAGE ------------------>" + line);
				
					
					if(chr == '~')
					{
					handler.post(doUpdateUI);
					end_message = true;
					}

				}
			

				

			} 
			catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	/**********************************************************************************************/

	private void sendMessage(String msg)
	{
		// TEXT-Zeile die verschickt wird
		String text;
	
		if(sendPosition && location != null)
		{
			String pos_string = "*" + location.getLatitude() + " " + location.getLongitude();
			text = "#"+mynickname+":"+msg+pos_string+"~";
		}
		else 
		{ 
			text = "#"+mynickname+":"+msg+"~"; // FALLS Location unbekannt
			
		}

		try 
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),false);
			
			out.write(text);
			out.flush();
			
			System.out.println(" SEND MESSAGE ------------------>");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	/**********************************************************************************************/
	private void parseString()
	{

		// Nimmt NICK-NAMEN 
		String nick = line.substring(1,line.indexOf(":"));
		
		// Nimmt POSITION (falls vorhanden)
		if(line.contains("*")){

			inputLine = line.substring(0,line.indexOf("*"));
			positionLine = line.substring(line.indexOf("*")+1,line.length()-1);

			
			Location l = new Location(LocationManager.GPS_PROVIDER);

			positionLine.replace(',', '.');
			String[] latLong = positionLine.split(" ");
			l.setLatitude(Double.parseDouble(latLong[0]));
			l.setLongitude(Double.parseDouble(latLong[1]));
			
			// Speichert NICK und POSITION in OTHERLOCATION-MAP		
			
			otherLocations.put(nick, l);
			
			// Fuegt zum LocationManager Proximity Alert hinzu
	        if(mynickname != nick)
	        {
	        	
	        	Intent intent = new Intent(res.getString(R.string.found_buddy));
	        	
	        	intent.putExtra("nick", nick);
	
	            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent , PendingIntent.FLAG_CANCEL_CURRENT);
	        	locman.addProximityAlert(l.getLatitude(), l.getLongitude(), 50f, 180000, pendingIntent);
	        }
			
			
			line = line.substring(1,line.indexOf("*"));
		}
		else 
		{
			line = line.substring(line.indexOf("#")+1,line.length()-1);
		}
		
	}
	
	/**********************************************************************************************/
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
	
		outState.putString("mynick", mynickname);
		outState.putString("history", all_text);
		super.onSaveInstanceState(outState);
		
		
	}

	/**********************************************************************************************/
	
	public class ProximityCheckerReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {


	    	System.out.println("-------------->        ALLLERT " + intent.getStringExtra("nick") + " <------------------------");
	    	Toast toast = Toast.makeText(ChatClientActivity.this, "Achtung "+intent.getStringExtra("nick"), Toast.LENGTH_LONG);

	    	toast.show();
	    	
	    }
	}


}





