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

public class FreeTransformLayout extends FrameLayout {
	public enum TAG {
		LifeCycle
		}
	private GestureHandler gestureHandler;
	private GestureHandler.GestureListener listener;
	private Paint paint;

	private Vector2Int 
	stageSize = new Vector2Int(), 
	viewportSize = new Vector2Int(), 
	coordSign = new Vector2Int();

	private float scale = 1.0f;
	private Vector2 translation = new Vector2().set(0f);


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
		Log.logT(TAG.LifeCycle, "init开始初始化");
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

				Log.logT(
					TAG.LifeCycle, ""
					+ "获取attrs: "
					+ "\n\tenableViewportGesture: %s"
					+ "\n\tenableViewportGestureConstrain: %s"
					+ "\n\tinCenter: %s", 
					enableViewportGesture, enableViewportGestureConstrain, inCenter
				);
			} finally {
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
			public Vector2Int getStageSize() {
				View child0 = getChildAt(0);
				stageSize.set(child0.getWidth(), child0.getHeight());
				return stageSize;
			}
			public Vector2Int getViewportSize() {
				viewportSize.set(getWidth(), getHeight());
				return viewportSize;
			}
			public Vector2Int coordinatesSigned(){
				coordSign.set(1, -1);
				return coordSign;
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
		gestureHandler = new GestureHandler(listener, cfg);

		final boolean inCenter2 = inCenter;
		post(new Runnable(){public void run() {

					Log.logT(
						TAG.LifeCycle, ""
						+ "布局已准备: "
						+ "\n\tgetViewportSize: %s"
						+ "\n\tgetStageSize: %s", 
						listener.getViewportSize(), listener.getStageSize()
					);
					if (inCenter2) moveToViewportCenter();

					Log.logT(TAG.LifeCycle, "布局初始化完成");
				}}
		);

	}

	private void moveToViewportCenter() {
		String str = "";
		str += "启用InCenter";
		Vector2 viewportCenter = listener.getViewportSize().clone().div(2);
		Vector2 initPos = new Vector2(viewportCenter);
		initPos.sub(listener.getStageSize().clone().div(2));

		gestureHandler.realStagePos.set(initPos);
		str += String.format(
			"\n\t初始值: \n\t\t视口中心: %s, \n\t\tinitPos: %s, \n\t\trealStagePos: %s", 
			viewportCenter, initPos, gestureHandler.realStagePos);

		gestureHandler.decomposeRealStagePos(gestureHandler.realStagePos);
		gestureHandler.constrainRealStagePos();
		translate(gestureHandler.realStagePos.x, gestureHandler.realStagePos.y);
		str += String.format(
			"\n\t限制后: \n\t\tinitPos: %s, \n\t\trealStagePos: %s", 
			initPos, gestureHandler.realStagePos);

		invalidate();
		str += "\n\t视图刷新";
		Log.logT(GestureHandler.TAG.InCenter, str);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handle = gestureHandler.handleTouchEvent(event);
		if (handle) {
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(translation.x, translation.y);
		canvas.scale(scale, scale);
		super.dispatchDraw(canvas);
		canvas.restore();

		float circleRadius = 15;
		canvas.drawCircle(gestureHandler.DoubleFocusPos().x, gestureHandler.DoubleFocusPos().y, circleRadius, paint);

	}

	public void translate(float dx, float dy) {
		translation.set(dx, dy);
	}

	public void setScale(float setScale) {
		scale = setScale;
	}
}


