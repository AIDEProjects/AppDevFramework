package com.goldsprite.appdevframework.apputils;

import android.view.*;
import com.goldsprite.appdevframework.math.*;
import android.view.View.*;

public abstract class GestureManager implements OnTouchListener
{
    private Vector2 canvasPos = new Vector2(); // 当前偏移
    private float canvasScl = 1; // 当前缩放比例

    public Vector2 CanvasPos() { return canvasPos; }
    public float CanvasScl() { return canvasScl; }

	protected boolean constrainMovement = true;
	protected boolean constrainScl = true;
	protected boolean enableTranslate = true;
	protected boolean enableScl = true;

	public abstract void invalidate();
	public abstract boolean hasView();
	public abstract Vector2 getViewSize();
	public abstract Vector2Int getViewportSize();

	public GestureManager() {
	}


	int pointerCount;
	Vector2 
	startSingleFocusPos = new Vector2(), 
	singleFocusPos = new Vector2(), 
	singleTranslate = new Vector2();
	float singleFocusMovementAngle;
	Vector2 
	startDoubleFocusPos = new Vector2(), 
	doubleFocusPos = new Vector2(), 
	doubleTranslate = new Vector2(), 
	lastDoubleFocusPos = new Vector2(), 
	tickDoubleTranslate = new Vector2();
    Vector2 startCanvasPos = new Vector2();
	float doubleFocusMovementAngle;
	float 
	startDoubleDistance, 
	doubleDistance, 
	doubleDistanceDiff, 
	doubleSclDeadZone = 60;
	float startCanvasScl;
	Vector2 child0SclSize;
	float doubleFingerAngleDiff;
	@Override
    public boolean onTouch(View v, MotionEvent ev) {
		int action = ev.getAction();
		pointerCount = ev.getPointerCount();
		float realDoubleDistance=0, diff=0;

		//if (pointerCount == 1){
		singleFocusPos.set(ev.getX(0), ev.getY(0));//单指坐标
		//}
		if (pointerCount >= 2) {
			doubleFocusPos.set(
				ev.getX(0) + (ev.getX(1) - ev.getX(0)) / 2f,
				ev.getY(0) + (ev.getY(1) - ev.getY(0)) / 2f
			);
			doubleDistance = (float)Math.sqrt(
				Math.pow(ev.getX(1) - ev.getX(0) , 2)
				+ Math.pow(ev.getY(1) - ev.getY(0), 2));
			diff = doubleDistance - startDoubleDistance;
			if (Math.abs(diff) < doubleSclDeadZone) {
				realDoubleDistance = startDoubleDistance;
			}
			else realDoubleDistance = doubleDistance - Math.signum(diff) * doubleSclDeadZone;
		}
		//计算偏差角
		singleFocusMovementAngle = getVectorAngle(singleFocusPos.clone().sub(startSingleFocusPos).scl(new Vector2(1, -1)).normalize());//单指位移角度
		doubleFocusMovementAngle = getVectorAngle(doubleFocusPos.clone().sub(startDoubleFocusPos).scl(new Vector2(1, -1)).normalize());//第二指位移角度
		doubleFingerAngleDiff = angleDifference(doubleFocusMovementAngle, singleFocusMovementAngle);
		//DebugView.setDebugInfo(6, "双指角度差: " + doubleFingerAngleDiff);

		//单指按下滑动
		if (action == MotionEvent.ACTION_DOWN) {
			startSingleFocusPos.set(singleFocusPos);
			//DebugView.setDebugInfo(3, "单指按下, 中心坐标: " + startSingleFocusPos);
		}
		if (pointerCount == 1 && action == MotionEvent.ACTION_MOVE) {
			doubleTranslate.set(doubleFocusPos.sub(startDoubleFocusPos));
			//DebugView.setDebugInfo(3, "单指滑动, 偏移量: " + doubleTranslate + ", 角度: " + singleFocusMovementAngle);
		}

		//双指按下滑动
		if (enableTranslate) {
			if (pointerCount == 2 && action == MotionEvent.ACTION_POINTER_2_DOWN) {
				startDoubleFocusPos.set(doubleFocusPos);
				startCanvasPos.set(canvasPos);
				//DebugView.setDebugInfo(3, "双指按下, 记录中心坐标: " + startDoubleFocusPos);
			}
			if (pointerCount == 2 && action == MotionEvent.ACTION_MOVE) {
				doubleTranslate.set(doubleFocusPos.sub(startDoubleFocusPos));
				tickDoubleTranslate.set(doubleFocusPos.sub(lastDoubleFocusPos));
				lastDoubleFocusPos.set(doubleFocusPos);
				//DebugView.setDebugInfo(3, "双指滑动, 偏移量: " + doubleTranslate /*+ ", 步长偏移量: " + tickDoubleTranslate*/+ ", 第二指角度: " + doubleFocusMovementAngle);

				canvasPos.set(startCanvasPos.clone().add(doubleTranslate));
				constrainMovement(canvasPos);
				invalidate();
			}
		}

		//双指按下缩放
		if (enableScl) {
			if (pointerCount == 2 && action == MotionEvent.ACTION_POINTER_2_DOWN) {
				startDoubleDistance = doubleDistance;
				startCanvasScl = canvasScl;
				//DebugView.setDebugInfo(4, "双指按下, 记录中心距离: " + startDoubleDistance);
			}
			if (pointerCount == 2 && action == MotionEvent.ACTION_MOVE) {
				doubleDistanceDiff = realDoubleDistance / startDoubleDistance;
				//DebugView.setDebugInfo(4, String.format("双指缩放, 死区: %s/%s, 实际偏差量: %s", (int)(Math.abs(diff) * 10) / 10f, doubleSclDeadZone, doubleDistanceDiff));

				child0SclSize = new Vector2(getViewSize().x, getViewSize().y).scl(canvasScl);
				//DebugView.setDebugInfo(5, "双指缩放, 子布局实际Size: " + child0SclSize);
				canvasScl = startCanvasScl * doubleDistanceDiff;
				canvasScl = constrainScl(canvasScl);
				invalidate();
			}
		}

		//全部抬起
		if (pointerCount == 1 && action == MotionEvent.ACTION_UP) {
			//DebugView.setDebugInfo(3, "全部抬起");
			pointerCount = 0;
			//DebugView.setDebugInfo(2, "手指数量: " + pointerCount);
			//pointerIds.clear();
		}

		//DebugView.setDebugInfo(1, "触摸状态: " + AppUtils.getMotionEventActionFieldName(action));
		//DebugView.setDebugInfo(2, "手指数量: " + pointerCount);

        return true;
    }


	public float getVectorAngle(Vector2 vector) {
		double radians = Math.atan2(vector.y, vector.x);
		double angle = (float)Math.toDegrees(radians);
		if (angle < 0) {
			angle += 360;
		}
		return (float)angle;
	}
	public static float angleDifference(float angle1, float angle2) {
        // 将角度转换为 0 到 360 的范围
        angle1 = angle1 % 360;
        angle2 = angle2 % 360;

        // 计算角度差
        float difference = Math.abs(angle1 - angle2);

        // 确保差值在 0 到 180 度之间
        if (difference > 180) {
            difference = 360 - difference;
        }

        return difference;
    }

	/*
	 增加Align标签: CENTER, LEFTDOWN
	 */
    private void constrainMovement(Vector2 canvasPos) {
		if (!constrainMovement) return;

        if (hasView()) {
            int childWidth = (int) (getViewSize().x * canvasScl);
            int childHeight = (int) (getViewSize().y * canvasScl);
			int margin = 10;

			// 确保内容在滑动时留出 100 像素的空间
			if (childWidth * childHeight < getViewportSize().x * getViewportSize().y) {
				canvasPos.x = Math.max(canvasPos.x, margin);//左边
				canvasPos.x = Math.min(canvasPos.x, getViewportSize().x - childWidth - margin);//右边
				canvasPos.y = Math.max(canvasPos.y, margin);//顶部
				canvasPos.y = Math.min(canvasPos.y, getViewportSize().y - childHeight - margin);//底部
			}
			else {
				canvasPos.x = Math.max(getViewportSize().x - margin - childWidth, Math.min(margin, canvasPos.x));
				canvasPos.y = Math.max(getViewportSize().y - margin - childHeight, Math.min(margin, canvasPos.y));
			}
        }
    }
	private float constrainScl(float scl) {
		if (!constrainScl) return scl;

		return Math.max(0.1f, Math.min(5f, scl));
	}

}

