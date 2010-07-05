package de.lmu.ifi.mdsg.msp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.lmu.ifi.mdsg.msp.HttpFileUploader;

public class UploadActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_layout);
		btnUpload = (Button) findViewById(R.id.upload_button);
		btnUpload.setOnClickListener(this);
		errorText = (TextView) findViewById(R.id.upload_textview);
		fileName = (EditText) findViewById(R.id.upload_edittext);
		exit = (Button) findViewById(R.id.exit_button);
		exit.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				cleanUpAndFinish();
			}
		});
		bitmap = (Bitmap) getIntent().getParcelableExtra(
				CreateFootprintActivity.BUNDLE_EXTRA);
		descr = getIntent().getStringExtra(
				CreateFootprintActivity.DESCRIPTION_EXTRA);
	}

	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}

	};
	final Runnable enableExit = new Runnable() {
		public void run() {
			exit.setEnabled(true);
		}

	};

	/**************************************************************/
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (bitmap == null) {
			bitmap = (Bitmap) savedInstanceState.getBundle(
					CreateFootprintActivity.BUNDLE_EXTRA).get(
					CreateFootprintActivity.BITMAP_EXTRA);
		}
		descr = savedInstanceState
				.getString(CreateFootprintActivity.DESCRIPTION_EXTRA);
	};

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CreateFootprintActivity.DESCRIPTION_EXTRA, descr);
		outState.putBundle(CreateFootprintActivity.BUNDLE_EXTRA, getIntent()
				.getBundleExtra(CreateFootprintActivity.BUNDLE_EXTRA));
	};

	/**************************************************************/

	private void updateResultsInUi() {
		btnUpload.setEnabled(!htfu.action_pending);
		errorText.setText(htfu.errorMessage + "\n" + errorText.getText());
	}

	HttpFileUploader htfu;
	HttpFileUploader htfu_th;

	public void onClick(View v) {
		exit.setEnabled(false);
		String msg;
		String name = fileName.getText().toString();
		String insertFileName = getResources().getString(
				R.string.upload_file_name);
		if (name.equals("") || name.equals(insertFileName)) {
			msg = "Please insert filename!";
			Toast toast = Toast.makeText(UploadActivity.this, msg,
					Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		final File full = new File("/sdcard/" + name + ".jpeg");
		final File thumbnail = new File("/sdcard/th_" + name + ".jpeg");
		try {
			bitmap
					.compress(CompressFormat.JPEG, 60, new FileOutputStream(
							full));
			bitmap.compress(CompressFormat.JPEG, 5, new FileOutputStream(
					thumbnail));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		msg = "Uploading " + full.length() + " bytes " + full.canRead();

		Toast toast = Toast.makeText(UploadActivity.this, msg,
				Toast.LENGTH_SHORT);
		toast.show();

		Thread t = new Thread() {
			public void run() {
				try {
					FileInputStream fis = new FileInputStream(full);
					htfu = new HttpFileUploader(UploadActivity.this,
							"http://msp.mobile.ifi.lmu.de/upload.php",
							"noparamshere", full.getName(), mHandler,
							mUpdateResults);
					htfu.doStart(fis);
					//TODO Update descr with the filename of the image on the server
					
					
					fis = new FileInputStream(thumbnail);
					htfu = new HttpFileUploader(UploadActivity.this,
							"http://msp.mobile.ifi.lmu.de/upload.php",
							"noparamshere", thumbnail.getName(), mHandler,
							mUpdateResults);
					htfu.doStart(fis);
					//TODO Update descr with the filename of the thumbnail on the server
					
					//TODO create information about new Thumbnail on server
					mHandler.post(enableExit);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void cleanUpAndFinish() {
		if (htfu != null && htfu.action_completed) {
			Intent data = new Intent();
			data.putExtra(CreateFootprintActivity.DESCRIPTION_EXTRA, descr);
			this.setResult(RESULT_OK, data);
			Log.e("UploadActivity", "Result ok");
			this.finish();
		} else {
			this.setResult(RESULT_CANCELED);
			Log.e("UploadActivity", "Result canceled");
			this.finish();
		}
	}

	Button btnUpload;
	Button exit;
	TextView errorText;
	EditText fileName;
	private Bitmap bitmap;
	private String descr;
}