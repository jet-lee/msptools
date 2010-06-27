package de.lmu.ifi.mdsg.msp.wlan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import de.lmu.ifi.mdsg.msp.EkahauActivity;
import de.lmu.ifi.mdsg.msp.R;

public class EkahauDataCatcher extends Service{
	private static final String EKAHAU_SERVER_URL = "http://mspgw.ifi.lmu.de:8550/epe/pos/taglist?fields=all&mac=";
	private static String User = "msp";
	private static String Password = "mspUser10";
	
	private String mac;
	private Timer updateTimer;
	private static boolean firstTimeStarted = true;
	
	public int[] requestUpdate(String mac) {
		try {
			Log.w(EkahauDataCatcher.class.getName(), "trying to connect ...");
			//Http authentication using digest
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(User, Password);

			DefaultHttpClient httpclient = new DefaultHttpClient();
			List<String> authpref = new ArrayList<String>();
			authpref.add(AuthPolicy.BASIC);
			authpref.add(AuthPolicy.DIGEST);
			httpclient.getParams().setParameter("http.auth.target-scheme-pref", authpref);
			httpclient.getCredentialsProvider().setCredentials(
			        new AuthScope("mspgw.ifi.lmu.de", 8550), creds);

			//fetch content
			HttpGet httpget = new HttpGet(EKAHAU_SERVER_URL + mac);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader read = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				Log.i(EkahauDataCatcher.class.getName(), "got input stream!");
				String line;
				int start, end;
				int x = -1;
				int y = -1;
				int mapid = -1;
				//grep position data
				while ((line = read.readLine()) != null) {
					
					Log.w(EkahauDataCatcher.class.getName(), line);
					
					if (line.trim().startsWith("<posx>")) {
						start = line.indexOf(">");
						end = line.lastIndexOf("<");
						x = Integer.parseInt(line.substring(start + 1, end));
					}
					if (line.trim().startsWith("<posy>")) {
						start = line.indexOf(">");
						end = line.lastIndexOf("<");
						y = Integer.parseInt(line.substring(start + 1, end));
					}
					if (line.trim().startsWith("<posmapid>")) {
						start = line.indexOf(">");
						end = line.lastIndexOf("<");
						mapid = Integer
								.parseInt(line.substring(start + 1, end));
					}
				}
				if (x > 0 && y > 0 && mapid > -1) {
					// got valid data
					int[] result = { x, y, mapid };
					Log.i(EkahauDataCatcher.class.getName(), "got result (x,y,id):("+x+","+y+","+mapid+")");
					return result;
				} 
			}
			//got no or invalid data
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private TimerTask update = new TimerTask() {
		
		@Override
		public void run() {
			update();
		}
	};;;
	
	@Override
	public void onCreate() {
		super.onCreate();
		updateTimer = new Timer("ekahauUpdate");
	}
	
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		//get mac address out of intent
		mac = intent.getStringExtra(EkahauActivity.MAC);
		//only use one timer, even if started twice
		if(firstTimeStarted){
			//a single update every 10 seconds
			updateTimer.scheduleAtFixedRate(update, 0, 10000);
			firstTimeStarted = false;
		}
	}


	protected void update(){
		int[] pos = requestUpdate(mac);
		Resources res = getResources();
		if(pos != null){
			//create new intent with specific name
			Intent update = new Intent(res.getString(R.string.ekahau_update_intent_name));
			//TODO add the data we want to send
			
			
			
			update.putExtra("x", pos[0]);
			update.putExtra("y", pos[1]);
			update.putExtra("id",pos[2]);

			//send new position data to all interested activities
			sendBroadcast(update);
		}
	}

	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
