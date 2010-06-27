package de.lmu.ifi.mdsg.msp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.lmu.ifi.mdsg.msp.wlan.EkahauAccessPoint;
import de.lmu.ifi.mdsg.msp.wlan.EkahauDataCatcher;
import de.lmu.ifi.mdsg.msp.wlan.EkahauPositionUpdater;

public class EkahauActivity extends Activity {

	boolean positioning = false;
	TextView ekahauTextView;
	WifiManager wifiManager;
	WifiReceiver wifiReceiver;
	String macAddress;
	
	Intent ekaCatcher;

	public static final String X = "X";
	public static final String Y = "Y";
	public static final String ID = "ID";
	public static final String MAC = "MAC";

	private OnClickListener buttonStartListener = new OnClickListener() {
		//@Override
		public void onClick(View v) {
			//TODO create and start new WifiReceiver
			//TODO get WIFI_SERVICE
			//TODO if disabled enable wifi and start scanning
			//TODO start EkahauDataCatcher as background service
			
			wifiManager.setWifiEnabled(true);

			wifiReceiver = new WifiReceiver();
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			
			
			
			ekaCatcher = new Intent(EkahauActivity.this,EkahauDataCatcher.class);
			
			
			String myMAC = wifiManager.getConnectionInfo().getMacAddress().toUpperCase();
			ekaCatcher.putExtra("MAC", myMAC);
			startService(ekaCatcher);
			
			
			Intent ekahauMapActivity = new Intent(EkahauActivity.this,EkahauMapActivity.class);
			startActivity(ekahauMapActivity);
		}
	};

	private OnClickListener buttonStopListener = new OnClickListener() {
		//@Override
		public void onClick(View v) {
			//TODO stop wifiReceiver
			//TODO stop EkahauDataCatcher service
			unregisterReceiver(wifiReceiver);	
			stopService(ekaCatcher);
			

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ekahau_layout);
		ekahauTextView = (TextView) findViewById(R.id.ekahautextview);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		Button buttonStart = (Button) findViewById(R.id.button_start_ekahau_positioning);
		buttonStart.setOnClickListener(buttonStartListener);

		Button buttonStop = (Button) findViewById(R.id.button_stop_ekahau_positioning);
		buttonStop.setOnClickListener(buttonStopListener);

		//If ekahauTextView is clicked, a map should be displayed
		ekahauTextView.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View v) {
				Intent i = new Intent(EkahauActivity.this,
						EkahauMapActivity.class);
				startActivity(i);
			}
		});
		//get mac of this device
		macAddress = wifiManager.getConnectionInfo().getMacAddress()
		.toUpperCase();
	}

	private List<EkahauAccessPoint> fetchAPs() {
		ArrayList<EkahauAccessPoint> resultArray = new ArrayList<EkahauAccessPoint>();
		//TODO get scan results and fill resultArray use parseMacString() and frequency2WifiChannel() to process ScanResults


		
		for( ScanResult r : wifiManager.getScanResults())
		{
			resultArray.add(new EkahauAccessPoint(r.SSID,parseMacString(r.BSSID),frequency2WifiChannel(r.frequency),r.level));
		}
			
		
		return resultArray;
	}

	/**
	 * Returns the channel to a specific frequency
	 * @param frequency 
	 * @return
	 */
	private static int frequency2WifiChannel(int frequency) {
		int result = 0;
		switch (frequency) {
		case 2412: {
			result = 1;
			break;
		}
		case 2417: {
			result = 2;
			break;
		}
		case 2422: {
			result = 3;
			break;
		}
		case 2427: {
			result = 4;
			break;
		}
		case 2432: {
			result = 5;
			break;
		}
		case 2437: {
			result = 6;
			break;
		}
		case 2442: {
			result = 7;
			break;
		}
		case 2447: {
			result = 8;
			break;
		}
		case 2452: {
			result = 9;
			break;
		}
		case 2457: {
			result = 10;
			break;
		}
		case 2462: {
			result = 11;
			break;
		}
		case 2467: {
			result = 12;
			break;
		}
		case 2472: {
			result = 13;
			break;
		}
		case 2484: { // Japan only
			result = 14;
			break;
		}
		default:
			break;
		}
		return result;
	}

	/**
	 * Converts a MAC address string into a byte array
	 * 
	 * @param macstring
	 * @return
	 */
	private static byte[] parseMacString(String macstring) {
		byte[] result = new byte[6];
		String[] stringArray = android.text.TextUtils.split(macstring, ":");
		for (int i = 0; i < 6; i++) {
			result[i] = (byte) (Integer.parseInt(stringArray[i], 16));
		}
		return result;
	}

	/**
	 * Receives scan results and sends the fingerprint to the ekahau positioning engine 
	 *
	 */
	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			positioning = true;
			int elpPort = Integer
					.parseInt(getString(R.string.EkahauServerELPPort));
			int empPort = Integer
					.parseInt(getString(R.string.EkahauServerEMPPort));
			EkahauPositionUpdater epu = new EkahauPositionUpdater(
					getString(R.string.EkahauServerName), elpPort, empPort);
			boolean connected = epu.connectELP();
			if (connected)
				ekahauTextView.setText("ELP connected.");
			else
				ekahauTextView.setText("ELP not connected!");
			if (connected && positioning) {
				List<EkahauAccessPoint> aps = fetchAPs();
				try {
					byte[] myMacAsByteArray = parseMacString(wifiManager
							.getConnectionInfo().getMacAddress());
					epu.sendELP(aps, myMacAsByteArray);
					ekahauTextView.setText("ELP packet sent.");
				} catch (Exception e) {
					ekahauTextView.setText(e.getMessage());
				}
			}

			StringBuilder sb = new StringBuilder();
			List<ScanResult> wifiList = wifiManager.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				sb.append(new Integer(i + 1).toString() + ": ");
				sb.append(wifiList.get(i).BSSID + " ");
				sb.append(wifiList.get(i).SSID + " ");
				sb.append(wifiList.get(i).level);
				sb.append("\n");
			}
			ekahauTextView.setText(sb);
			
			// continuious scanning:
			wifiManager.startScan();
			
			

		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
		Intent service = new Intent(EkahauActivity.this, EkahauDataCatcher.class);
		stopService(service);
		super.onDestroy();
	}
	
	
}
