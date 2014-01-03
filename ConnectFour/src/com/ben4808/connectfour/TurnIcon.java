package com.ben4808.connectfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TurnIcon extends View {
	private Paint color;
	
	public TurnIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = GameView.blue;
	}
	
	public void setColor(Paint p) {
		color = p;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int chipRadius = getResources().getDimensionPixelSize(R.dimen.status_chip_radius);
		int haloRadius = getResources().getDimensionPixelSize(R.dimen.status_chip_halo_radius);
		int radius = chipRadius + haloRadius;
		canvas.drawCircle(radius, radius, radius, GameView.gray);
		canvas.drawCircle(radius, radius, chipRadius, color);
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int prefSize = (getResources().getDimensionPixelSize(R.dimen.status_chip_radius) +
				getResources().getDimensionPixelSize(R.dimen.status_chip_halo_radius)) * 2;
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
        	width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
        	width = Math.min(prefSize, widthSize);
        } else {
        	width = prefSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
        	height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
        	height = Math.min(prefSize, heightSize);
        } else {
        	height = prefSize;
        }
        
        setMeasuredDimension(width, height);
	}
}
