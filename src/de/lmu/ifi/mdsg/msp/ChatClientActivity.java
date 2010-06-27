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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;



public class ChatClientActivity extends Activity {

	/*******************************************/
	String mynickname = "Monster";
	
	String inputLine;
	String positionLine;
	
	private Map<String, Location> otherLocations;

	private LocationManager locman;
	private Location location;
	
	/*******************************************/
	Button send_button;
	EditText chat_eingabe;
	EditText chat_ausgabe;
	
	EditText editNickname;
	CheckBox checkBox;
	
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
	}

	/**********************************************************************************************/

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_chat_layout);
		
		try {
			//socket = new Socket("129.187.214.232",4000);
			socket = new Socket("192.168.0.10",4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		connected=true;
		
		/***********************/
		
		// NICKNAME EINGEBEN
		
		editNickname = (EditText) findViewById(R.id.edit_nickname);
				
		editNickname.setOnKeyListener(new OnKeyListener() {
			

			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction() == KeyEvent.ACTION_DOWN){
					
					if(keyCode == KeyEvent.KEYCODE_ENTER){
									
						mynickname = editNickname.getText().toString();
						editNickname.setText("Your nick is: " + mynickname);
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
				} else {
					sendPosition = false;
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
		
		otherLocations = new HashMap<String, Location>();
	}
	
	/**********************************************************************************************/
	
	//AUSGABE DES CHATs
	private void updateGUI(){
		
		
		
		System.out.println("UpdateGUI--------------------->" + line);
		chat_ausgabe = (EditText) findViewById(R.id.chat_ausgabe);
		
		//line = line.substring(1,line.length()-1) + "\n";
		
		
		parseString();
		
		all_text = line + all_text;
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
			System.out.println("----------->  LOCATION <-------------");
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

			String[] latLong = positionLine.split(" ");
			l.setLatitude(Double.parseDouble(latLong[0]));
			l.setLongitude(Double.parseDouble(latLong[1]));
			
			// Speichert NICK und POSITION in OTHERLOCATION-MAP
			if(mynickname == nick)
			{
				otherLocations.put(nick, l);
			}
			
			line = line.substring(1,line.indexOf("*"));
		}
		else 
		{
			line = line.substring(line.indexOf("#")+1,line.length()-1);
		}
		
	}
	
	/**********************************************************************************************/


}
