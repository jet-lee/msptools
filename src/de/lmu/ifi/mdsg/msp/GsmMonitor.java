package de.lmu.ifi.mdsg.msp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

public class GsmMonitor extends View {
	
	
	TelephonyManager telemanager = GSMActivity.telephonyManager;
	int signalstrength = 0;

/**********************************************************************************************/
	
	public GsmMonitor(Context context){
		super(context);
		
		// PhoneStateListener 
		PhoneStateListener listener = new PhoneStateListener()
		{	
			//Signalstaerke
			public void onSignalStrengthChanged(int asu)
			{	
				signalstrength = asu;
				GsmMonitor.this.invalidate();
			}
		};
			
		telemanager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
	}
	
	
/**********************************************************************************************/
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		paint.setColor(Color.BLUE);
		canvas.drawPaint(paint);
	
		paint.setColor(Color.BLACK);
		paint.setTextSize(20);
		canvas.drawText("Signalstaerke", 5,20, paint);
		
		
		paint.setColor(Color.GREEN);		
		int left = 0;
		int top = 50;
		int bottom = top + 30;
		int right = 20;
		
		
		int signal =(int)( signalstrength* 0.3 + 2 );
		
		//int signal = (int)( 11 * 0.3 + 2);
		if(signal > 9)
		{
			signal = 9;
		}
		
		for(int i=0;i<signal;i++)
		{
			canvas.drawRect(left,top,right ,bottom, paint);
			
			top += 40;
			bottom += 40;
		
			right += 30;
		}
		
		
		
	}
}


