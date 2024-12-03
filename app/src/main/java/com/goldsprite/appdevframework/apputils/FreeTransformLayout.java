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
	private GestureHandler.GestureListener listener;
	private Paint paint;

	private Vector2Int stageSize = new Vector2Int(), viewportSize = new Vector2Int();

	private float scale = 1.0f;
	private Vector2 translate = new Vector2().set(0f);


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
		boolean inCenter = false;
		if (attrs != null) {
			// 获取自定义属性
			TypedArray a = getContext().getTheme().obtainStyledAttributes(
				attrs, R.styleable.FreeTransformLayout, 0, 0);
//			TypedArray b = getContext().getTheme().obtainStyledAttributes(
//				attrs, new int[]{ android.R.attr.gravity }, 
//				0, 0);
			try {
				// 获取布尔属性
				enableViewportGesture = a.getBoolean(
					R.styleable.FreeTransformLayout_enableViewportGesture, true);
				enableViewportGestureConstrain = a.getBoolean(
					R.styleable.FreeTransformLayout_enableViewportGestureConstrain, true);
				
//				// 获取 gravity 属性并检查是否为 CENTER
//				int gravity = a.getInt(android.R.attr.gravity, -1);
				// 直接从 attrs 获取 gravity 属性
				int gravity = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "gravity", -1);
				// 检查 gravity 是否为 Gravity.CENTER
				if (gravity == Gravity.CENTER) {
					inCenter = true;
				}

			}
			finally {
				a.recycle();
			}
		}

		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);

		listener = new GestureHandler.GestureListener(){
			public boolean hasView() {
				return true;
			}
			public Vector2 getStageSize() {
				View child0 = getChildAt(0);
				stageSize.set(child0.getWidth(), child0.getHeight());
				return stageSize;
			}
			public Vector2Int getViewportSize() {
				viewportSize.set(getWidth(), getHeight());
				return viewportSize;
			}

			public void onDoublePointerMove(float dx, float dy) {
				translate(dx, dy);
				invalidate();
			}
			public void onScale(float setScale) {
				setScale(setScale);
				invalidate();
			}
		};
		final GestureHandler.CFG cfg = new GestureHandler.CFG();
		cfg.allSet(false);
		if (enableViewportGesture) {
			if (enableViewportGestureConstrain) {
				cfg.constrainMovement = true;
				cfg.constrainScl = true;
			}
			cfg.enableTranslate = true;
			cfg.enableScl = true;
		}
		gestureManager = new GestureHandler(listener, cfg);
		
		final boolean inCenter2 = inCenter;
		post(new Runnable(){public void run() {
					Log.log("getViewportSize: " + listener.getViewportSize());
					Log.log("getStageSize: " + listener.getStageSize());
					Log.log("StagePos: " + gestureManager.StagePos());
					if(inCenter2) moveToViewportCenter();
				}});
	}

	private void moveToViewportCenter() {
		Vector2 viewportCenter = listener.getViewportSize().clone().div(2);
		Vector2 initPos = new Vector2(viewportCenter);
		initPos.sub(listener.getStageSize().clone().div(2));

		gestureManager.realStagePos.set(initPos);
		gestureManager.constrainRealStagePos();
		translate(initPos.x, initPos.y);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handle = gestureManager.handleTouchEvent(event);
		if (handle) {
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		Vector2 pos = translate;
		canvas.translate(pos.x, pos.y);
		canvas.scale(scale, scale);
		super.dispatchDraw(canvas);
		canvas.restore();

		float circleRadius = 15;
		canvas.drawCircle(gestureManager.DoubleFocusPos().x, gestureManager.DoubleFocusPos().y, circleRadius, paint);

	}

	public void translate(float dx, float dy) {
		translate.set(dx, dy);
	}

	public void setScale(float setScale) {
		scale = setScale;
	}
}


