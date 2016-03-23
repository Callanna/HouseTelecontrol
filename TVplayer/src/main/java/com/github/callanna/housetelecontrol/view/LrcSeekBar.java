package com.github.callanna.housetelecontrol.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class LrcSeekBar extends SeekBar {

	private Context context;
	private Paint backgroundPaint;
	private Paint progressPaint;
	private Paint secondProgressPaint;

	private Paint thumbPaint;

	private boolean isLoadColor = false;


	public LrcSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LrcSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LrcSeekBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		this.context = context;
		initPaint();
	}

	private void initPaint() {

		backgroundPaint = new Paint();
		backgroundPaint.setDither(true);
		backgroundPaint.setAntiAlias(true);

		progressPaint = new Paint();
		progressPaint.setDither(true);
		progressPaint.setAntiAlias(true);

		secondProgressPaint = new Paint();
		secondProgressPaint.setDither(true);
		secondProgressPaint.setAntiAlias(true);

		thumbPaint = new Paint();
		thumbPaint.setDither(true);
		thumbPaint.setAntiAlias(true);

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (!isLoadColor) {
			backgroundPaint.setColor(Color.parseColor("#11ffffff"));
			progressPaint.setColor(Color.parseColor("#00ffff"));
			secondProgressPaint.setColor(Color.parseColor("#88ffffff"));
			thumbPaint.setColor(Color.parseColor("#00ffff"));
			isLoadColor = true;
		}

		int height = 2;

		Rect backgroundRect = new Rect(0, getHeight() / 2 - height, getWidth(),
				getHeight() / 2 + height);
		canvas.drawRect(backgroundRect, backgroundPaint);
		if (getMax() != 0) {
			Rect secondProgressRect = new Rect(0, getHeight() / 2 - height,
					getSecondaryProgress() * getWidth() / getMax(), getHeight()
							/ 2 + height);
			canvas.drawRect(secondProgressRect, secondProgressPaint);
			Rect progressRect = new Rect(0, getHeight() / 2 - height,
					getProgress() * getWidth() / getMax(), getHeight() / 2
							+ height);
			canvas.drawRect(progressRect, progressPaint);
			int thumbRadius = 10;
			int floatx = getProgress() * getWidth() / getMax() - thumbRadius/2;
			int floaty = getHeight() / 2 ;
			canvas.drawCircle(floatx,floaty,thumbRadius, thumbPaint);
		}
	}

}
