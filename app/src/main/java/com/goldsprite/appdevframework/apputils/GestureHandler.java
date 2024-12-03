package com.goldsprite.appdevframework.apputils;

import android.view.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.apputils.*;
import android.app.*;
import com.goldsprite.appdevframework.log.*;

public class GestureHandler
{
	private Vector2 stagePos = new Vector2().set(0); // 当前偏移
	public Vector2 realStagePos = new Vector2(0, 0);
	public Vector2 lastRealStagePos = new Vector2(0, 0);

	private Vector2 stageSclOffset = new Vector2(0, 0);
	private float stageSclFactor = 1; // 当前缩放比例
	public Vector2 StageSclOffset() {
		return stageSclOffset;
	}

	public Vector2 StagePos() { return stagePos; }
	public float StageSclFactor() { return stageSclFactor; }
	public Vector2 DoubleFocusPos() { 
		Vector2 focus = listener.getViewportSize().clone().div(2);
		return focus;
	}

	protected CFG cfg;
	public static class CFG
	{
		public boolean constrainMovement;
		public boolean constrainScl;
		public boolean enableTranslate;
		public boolean enableScl;

		public CFG() { allSet(true); }

		public void allSet(boolean boo) {
			enableTranslate = boo;
			enableScl = boo;
			constrainMovement = boo;
			constrainScl = boo;
		}
	}

    private final GestureListener listener;

    public interface GestureListener
	{
		boolean hasView();
		Vector2 getStageSize();
		Vector2Int getViewportSize();
        void onDoublePointerMove(float dx, float dy);
        void onScale(float setScale);
    }


    public GestureHandler(GestureListener listener, CFG cfg) {
		if (cfg == null) {
			cfg = new CFG();
		}
		this.cfg = cfg;
        this.listener = listener;
		//处理无子布局的情况
		if (!listener.hasView()) {
			cfg.allSet(false);
		}
    }

	float translateVelRate = 1;

	int pointerCount;
	//单指位置
	Vector2 
	startSingleFocusPos = new Vector2(), 
	lastSingleFocusPos = new Vector2(), 
	singleFocusPos = new Vector2(), 
	singleTranslate = new Vector2();
	float singleFocusMovementAngle;
	//双指位置
	Vector2 
	startDoubleFocusPos = new Vector2(), 
	doubleFocusPos = new Vector2(), 
	doubleTranslate = new Vector2(), 
	lastDoubleFocusPos = new Vector2(), 
	tickDoubleTranslate = new Vector2();
	Vector2 startCanvasPos = new Vector2();
	float doubleFocusMovementAngle;
	//距离
	float 
	startDoubleDistance, 
	doubleDistance, 
	doubleDistanceDiff, 
	doubleSclDeadZone = 60;
	//拉伸
	float startCanvasScl, lastStageSclFactor;
	//角度
	float doubleFingerAngleDiff;
	//视图
	Vector2 startViewportOrigin = Vector2.zero();
	Vector2 startCanvasOrigin = Vector2.zero();

	public boolean handleTouchEvent(MotionEvent ev) {
		try {
			int action = ev.getAction();
			pointerCount = ev.getPointerCount();
			float realDoubleDistance=0, diff=0;

			if (pointerCount != 2) return true;

			doubleFocusPos.set(
				ev.getX(0) + (ev.getX(1) - ev.getX(0)) / 2f,
				ev.getY(0) + (ev.getY(1) - ev.getY(0)) / 2f
			);
			doubleDistance = calculateDistance(ev);
			diff = doubleDistance - startDoubleDistance;
			if (Math.abs(diff) < doubleSclDeadZone) {
				realDoubleDistance = startDoubleDistance;
			}
			else {
				realDoubleDistance = doubleDistance - Math.signum(diff) * doubleSclDeadZone;
			}


			if (cfg.enableTranslate) {
				if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
					startDoubleFocusPos.set(doubleFocusPos);
					startCanvasPos.set(stagePos);
				}

				if (action == MotionEvent.ACTION_MOVE) {
					doubleTranslate.set(doubleFocusPos).sub(startDoubleFocusPos).div(stageSclFactor);
					stagePos.set(startCanvasPos).add(doubleTranslate);
					
					constrainRealStagePos();
					listener.onDoublePointerMove(realStagePos.x, realStagePos.y);
				}
			}
			if (cfg.enableScl) {
				if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
					startDoubleDistance = doubleDistance;
					startCanvasScl = stageSclFactor;
				}
				if (action == MotionEvent.ACTION_MOVE) {
					doubleDistanceDiff = realDoubleDistance / startDoubleDistance;
					stageSclFactor = startCanvasScl * doubleDistanceDiff;
					stageSclFactor = constrainScl(stageSclFactor);

					constrainRealStagePos();
					listener.onScale(stageSclFactor);

				}
			}

		}
		catch (Throwable e) {
			AppLog.dialogE("onTouch", e);
		}
		return true;
	}

	public void constrainRealStagePos() {
		//计算整个偏移
		updateStageSclOffset();
		realStagePos.set(stagePos).add(stageSclOffset);
		constrainMovement(realStagePos);
		decomposeRealStagePos(realStagePos);
	}

	/*
	 推导过程: 
	 ．．实际舞台位置 = 舞台位置 + 舞台缩放位移
	 ．．舞台位置 = (缩放视口中心 - 视口中心 - 实际舞台位置) / 缩放因子

	 实际正确的: 
	 ．．舞台位置 = (实际舞台位置 + 缩放视口中心 - 视口中心) / 缩放因子
	 */
	public void decomposeRealStagePos(Vector2 realStagePos) {
		// 获取视口中心和缩放后的视口中心
		Vector2 viewportCenter = listener.getViewportSize().clone().div(2); // 视口中心
		Vector2 sclViewportCenter = viewportCenter.clone().scl(stageSclFactor); // 缩放后的视口中心

		// 根据公式计算 stagePos
		if (stageSclFactor == 1) stagePos.set(realStagePos);
		else {
			stagePos.set(sclViewportCenter).sub(viewportCenter).sub(realStagePos).div(stageSclFactor);
			stagePos.set(realStagePos).add(sclViewportCenter).sub(viewportCenter).div(stageSclFactor);
		}
		// 根据 stagePos 更新 stageSclOffset
		updateStageSclOffset();
		//Log.logf("realStagePos:%s, stagePos:%s, stageSclOffset:%s, 验算差:%s", realStagePos, stagePos, stageSclOffset, realStagePos.clone().add(stagePos).add(stageSclOffset));
	}

	//舞台缩放偏移 = 视口中心 - 缩放视口中心 + 舞台位置 * (缩放因子-1)
	public void updateStageSclOffset() {
		Vector2 viewportCenter = listener.getViewportSize().clone().div(2);
		Vector2 sclViewportCenter = viewportCenter.clone().scl(stageSclFactor);
		Vector2 stagePosFactor = stagePos.clone().scl(stageSclFactor - 1);
		stageSclOffset = viewportCenter.sub(sclViewportCenter);
		stageSclOffset.add(stagePosFactor);
	}


	///TODO: 增加Align标签: CENTER, LEFTDOWN
	private void constrainMovement(Vector2 canvasPos) {
		if (!cfg.constrainMovement) return;

		Vector2 sSize = listener.getStageSize();
		Vector2 vSize = listener.getViewportSize();
		if (listener.hasView()) {
			float childWidth = sSize.x * stageSclFactor;
			float childHeight = sSize.y * stageSclFactor;
			int margin = 10;
			float xMin = margin;
			float xMax = vSize.x - childWidth - margin;
			float yMin = margin;
			float yMax = vSize.y - childHeight - margin;

			// 确保内容在滑动时留出 100 像素的空间
			//宽高小于视图
			if (childWidth <= vSize.x && childHeight <= vSize.y) {
				canvasPos.x = Math.max(canvasPos.x, xMin);//左边
				canvasPos.x = Math.min(canvasPos.x, xMax);//右边
				canvasPos.y = Math.max(canvasPos.y, yMin);//顶部
				canvasPos.y = Math.min(canvasPos.y, yMax);//底部
			}
			//宽高超出视图
			else {
				canvasPos.x = Math.max(xMax, Math.min(xMin, canvasPos.x));
				canvasPos.y = Math.max(yMax, Math.min(yMin, canvasPos.y));
			}
		}
	}
	private float constrainScl(float scl) {
		if (!cfg.constrainScl) return scl;

		return Math.max(0.1f, Math.min(5f, scl));
	}

    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

}

