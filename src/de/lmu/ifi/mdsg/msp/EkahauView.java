package de.lmu.ifi.mdsg.msp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import de.lmu.ifi.mdsg.msp.R;

public class EkahauView extends ImageView {

	public static final int MARKER_RADIUS = 5;
	// scale depend on the image
	public static final float scale = 0.271f;

	private Paint markerPaint;
	private float x, y, id;

	// Constructors
	public EkahauView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public EkahauView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EkahauView(Context context) {
		super(context);
		init();
	}

	/**
	 * Initialize paint and image
	 */
	private void init() {
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(Color.RED);
		Resources res = getResources();
		setImageDrawable(res.getDrawable(R.drawable.mapview1_skaliert));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if we got a location
		if (x > 0 && y > 0 && id > -1) {
			Resources res = getResources();
			if (id == 0) {
				setImageDrawable(res.getDrawable(R.drawable.mapview0_skaliert));//forces redraw
				id = 3;//only update if new coordinates arrive
			}
			if (id == 1) {
				setImageDrawable(res.getDrawable(R.drawable.mapview1_skaliert));//forces redraw
				id = 3;//only update if new coordinates arrive
			}
			if (id == 2) {
				setImageDrawable(res.getDrawable(R.drawable.mapview2_skaliert));//forces redraw
				id = 3;//only update if new coordinates arrive
			}
			// draw position on image
			float x_pix = scale * y;
			float y_pix = 1084-scale * x;
			Log.i(this.getClass().getName(), "display position on map "+x_pix+","+y_pix);
			canvas.drawCircle(x_pix, y_pix,
					MARKER_RADIUS, markerPaint);
		} else {
			// log the problem
			Log.e(this.getClass().getName(), "can not display position on map");
		}
	}

	public void setPosition(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}

}