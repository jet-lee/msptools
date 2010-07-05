package de.lmu.ifi.mdsg.msp;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

public class HttpFileUploader {

	public final String msgConnecting = "Trying to connect (can take some time)";
	public static final String msgConnectionProblem = "Unable to connect to server!";

	public Boolean action_completed = false;
	public Boolean action_pending = false;
	public String errorMessage = "";
	URL connectURL;
	String params;
	String responseString;
	String fileName;
	byte[] dataToServer;
	FileInputStream fileInputStream = null;
	Runnable toPost;
	Handler mHandler;
	Activity parent;

	public HttpFileUploader(Activity parent, String urlString, String params, String fileName,
			Handler mHandler, Runnable toPost) {
		try {
			connectURL = new URL(urlString);
		} catch (Exception ex) {
			errorMessage = ex.toString();
			mHandler.post(toPost);
			Log.i("MW", "Malformed URL in HttpFileUploader");
		}
		this.parent = parent;
		this.params = params + "=";
		this.fileName = fileName;
		this.toPost = toPost;
		this.mHandler = mHandler;
	}

	void postString(String s) {
		errorMessage = s;
		mHandler.post(toPost);
	}

	public void doStart(FileInputStream stream) {
		fileInputStream = stream;
		this.action_completed = false;
		this.action_pending = true;
		sendFile();
	}

	void sendFile() {

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String Tag = "MW";
		try {
			// ------------------ CLIENT REQUEST

			Log.e(Tag, "Starting to bad things");
			postString("Trying to connect...");
			// Open a HTTP connection to the URL

			HttpURLConnection conn = (HttpURLConnection) connectURL
					.openConnection();

			// Allow Inputs
			conn.setDoInput(true);

			// Allow Outputs
			conn.setDoOutput(true);

			// Don't use a cached copy.
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			Log.e(Tag, "Headers are written");
			postString("Connected. Uploading data");
			// create a buffer of maximum size

			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			// read file and write it into form...

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e(Tag, "File is written");
			postString("File is written");
			this.action_completed = true;
			fileInputStream.close();
			dos.flush();

			InputStream is = conn.getInputStream();
			// retrieve the response from server
			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			Log.i("Response", s);
			postString("Response:" + s);
			dos.close();

		} catch (MalformedURLException ex) {
			Log.e(Tag, "error: " + ex.getMessage(), ex);
			errorMessage = ex.toString();
			mHandler.post(toPost);
			action_pending = false;
		}

		catch (IOException ioe) {
			Log.e(Tag, "error: " + ioe.getMessage(), ioe);
			errorMessage = ioe.toString();
			mHandler.post(toPost);
			action_pending = false;
		}
	}

}

// Read more:
// http://getablogger.blogspot.com/2008/01/android-how-to-post-file-to-php-server.html#ixzz0sF3mRo4j
