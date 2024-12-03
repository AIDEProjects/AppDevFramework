package com.goldsprite.appdevframework.apputils;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.log.*;
import android.util.AttributeSet;
import android.content.res.*;
import com.goldsprite.appdevframework.R;

public class FreeTransformLayout extends FrameLayout
{
	private GestureHandler gestureManager;
	private Paint paint;


	public FreeTransformLayout(Context context) {
		super(context);
		init(context);
	}

	public FreeTransformLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FreeTransformLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context ctx) { init(ctx, null); }
	private void init(Context ctx, AttributeSet attrs) {
		boolean enableViewportGesture = false;
		boolean enableViewportGestureConstrain = false;
		if (attrs != null) {
			TypedArray a = getContext().getTheme().obtainStyledAttributes(
				attrs, R.styleable.FreeTransformLayout, 
				0, 0);
			try {
				enableViewportGesture = a.getBoolean(
					R.styleable.FreeTransformLayout_enableViewportGesture, true);
				enableViewportGestureConstrain = a.getBoolean(
					R.styleable.FreeTransformLayout_enableViewportGestureConstrain, true);
			} finally {}
		}

		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);

		gestureManager = new GestureHandler(){
			@Override
			public void invalidate() {
				FreeTransformLayout.this.invalidate();
			}

			@Override
			public boolean hasView() {
				return true;
			}

			@Override
			public Vector2 getStageSize() {
				View child0 = getChildAt(0);
				Vector2 size = new Vector2(child0.getWidth(), child0.getHeight());
				return size;
			}

			@Override
			public Vector2Int getViewportSize() {
				Vector2Int size = new Vector2Int(getWidth(), getHeight());
				return size;
			}
		};
		GestureHandler.CFG cfg = gestureManager.cfg;
		cfg.enableTranslate = false;
		cfg.enableScl = false;
		cfg.constrainMovement = false;
		cfg.constrainScl = false;
		if (enableViewportGesture) {
			if(enableViewportGestureConstrain){
				cfg.constrainMovement = true;
				cfg.constrainScl = true;
			}
			cfg.enableTranslate = true;
			cfg.enableScl = true;
		}
		setOnTouchListener(gestureManager);

		post(new Runnable(){public void run() {
					Log.log("getViewportSize: " + gestureManager.getViewportSize());
					Log.log("getStageSize: " + gestureManager.getStageSize());
					test();
					Log.log("StagePos: " + gestureManager.StagePos());
				}});
	}

	private void test() {
		Vector2 viewportCenter = gestureManager.getViewportSize().clone().div(2);
		gestureManager.StagePos().set(viewportCenter);
		gestureManager.StagePos().sub(gestureManager.getStageSize().clone().div(2));

		gestureManager.constrainRealStagePos();
		invalidate();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		Vector2 pos = gestureManager.realStagePos;
		canvas.translate(pos.x, pos.y);
		canvas.scale(gestureManager.StageSclFactor(), gestureManager.StageSclFactor());
		super.dispatchDraw(canvas);
		canvas.restore();

		float circleRadius = 15;
		canvas.drawCircle(gestureManager.DoubleFocusPos().x, gestureManager.DoubleFocusPos().y, circleRadius, paint);

	}
}


