package de.lmu.ifi.mdsg.msp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatClientActivity extends Activity {

	/*******************************************/
	String nickname = "Monster";
	String position = "1.2 3.8";
	
	
	
	/*******************************************/
	Button send_button;
	EditText chat_eingabe;
	EditText chat_ausgabe;
	
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

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_chat_layout);
		
		
		try {
			socket = new Socket("129.187.214.232",4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connected=true;
		
		
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
	}
	
	/**********************************************************************************************/
	private void updateGUI(){
		
		System.out.println("UpdateGUI--------------------->" + line);
		chat_ausgabe = (EditText) findViewById(R.id.chat_ausgabe);
		
		line = line.substring(1,line.length()-1) + "\n";
		
		all_text = line + all_text;
		chat_ausgabe.setText(all_text);
		line = "";
		
		
	}
	/**********************************************************************************************/

	private void backgroundThreadProcessing(){
		
		BufferedReader in;
		
		while(connected)
		{
			boolean end_message = false;

			System.out.println(" RECEIVED MESSAGE WERT VON LINE------------------>" + line);
			
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
		String text = "#"+nickname+":"+msg+"~";
		
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

}
