package com.github.callanna.housetelecontrol.view.LRC;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.callanna.housetelecontrol.mediaData.lrc.LyricContent;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends TextView{

	private float high;

	private float width;

	private Paint CurrentPaint;

	private Paint NotCurrentPaint;

	private float TextHigh = 30;

	private float TextSize = 25;

	private int Index = 0;

	private List<LyricContent> mSentenceEntities = new ArrayList<LyricContent>();

	public void setSentenceEntities(List<LyricContent> mSentenceEntities) {
		this.mSentenceEntities = mSentenceEntities;
	}

	public LyricView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {
		setFocusable(true);

		CurrentPaint = new Paint();
		CurrentPaint.setAntiAlias(true);
		CurrentPaint.setTextAlign(Paint.Align.CENTER);

		NotCurrentPaint = new Paint();
		NotCurrentPaint.setAntiAlias(true);
		NotCurrentPaint.setTextAlign(Paint.Align.CENTER);

	}
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (canvas == null) {
			return;
		}

		CurrentPaint.setColor(Color.parseColor("#ffd700"));
		NotCurrentPaint.setColor(Color.WHITE);

		CurrentPaint.setTextSize(30);
		CurrentPaint.setTypeface(Typeface.SERIF);

		NotCurrentPaint.setTextSize(20);
		NotCurrentPaint.setTypeface(Typeface.SERIF);

		try {
			canvas.drawText(mSentenceEntities.get(Index).getLyric(), width / 2,
					high / 2, CurrentPaint);

			float tempY = high / 2;
			for (int i = Index - 1; i >=0; i--) {
				tempY = tempY - TextHigh;

				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, NotCurrentPaint);

			}

			tempY = high / 2;
			for (int i = Index + 1; i < mSentenceEntities.size(); i++) {
				tempY = tempY + TextHigh;

				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, NotCurrentPaint);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.high = h;
		this.width = w;
	}

	public void SetIndex(int index) {
		this.Index = index;
		// System.out.println(index);
	}
}
