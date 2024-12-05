package com.goldsprite.appdevframework.apputils;

import android.view.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.apputils.*;
import android.app.*;
import com.goldsprite.appdevframework.log.*;

public class GestureHandler {

	public enum TAG {
		InCenter, ConstrainTranslate, RealtimeInfo
		}

	private Vector2 sclMinMaxLimit = new Vector2(0.3f, 6f);
	public Vector2 SclMinMaxLimit() { return sclMinMaxLimit; }

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
	public CFG Cfg() { return cfg; }
	public static class CFG {
		public boolean constrainMovement;
		public boolean constrainScl;
		public boolean enableTranslate;
		public boolean enableScl;
		public Vector2 pivot = Pivot.LeftDown;

		public CFG() { allSet(true); }

		public void allSet(boolean boo) {
			enableTranslate = boo;
			enableScl = boo;
			constrainMovement = boo;
			constrainScl = boo;
		}
	}

    private final GestureListener listener;

    public interface GestureListener {
		boolean hasView();
		Vector2Int getStageSize();
		Vector2Int getViewportSize();
		Vector2Int coordinatesSigned();
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
	Vector2 startStagePos = new Vector2();
	float doubleFocusMovementAngle;
	//距离
	float 
	startDoubleDistance, 
	doubleDistance, 
	doubleDistanceDiff, 
	doubleSclDeadZone = 80;
	//拉伸
	float startStageScl, lastStageSclFactor;
	//角度
	float doubleFingerAngleDiff;
	//视图
	Vector2 startViewportOrigin = Vector2.zero();
	Vector2 startCanvasOrigin = Vector2.zero();
	private MotionEvent touchEvent;
	Vector2 oldPos = new Vector2();
	Vector2 transDiff = new Vector2(), finalTransDiff = new Vector2();
	float doubleTransDeadZone = 20f;
	boolean outDeadZone;
	boolean sclOutDeadZone;
	float translateVal;

	public boolean handleTouchEvent(MotionEvent ev) {
		try {
			touchEvent = ev;
			int action = ev.getAction();
			pointerCount = ev.getPointerCount();
			float realDoubleDistance=0, diff=0;

			if (action == MotionEvent.ACTION_UP) {
				outDeadZone = false;
				//sclOutDeadZone = false;
			}
			
			if (pointerCount != 2) return true;
			
			doubleFocusPos.set(
				getApplyTouchX(0) + (getApplyTouchX(1) - getApplyTouchX(0)) / 2f,
				getApplyTouchY(0) + (getApplyTouchY(1) - getApplyTouchY(0)) / 2f
			);
			doubleDistance = calculateDistance(ev);
			
			//记录按下数据
			if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
				startDoubleFocusPos.set(doubleFocusPos);
				startStagePos.set(stagePos);
				
				startDoubleDistance = doubleDistance;
				startStageScl = stageSclFactor;
			}
			
			//计算位移距离，排除死区
			transDiff.set(doubleFocusPos).sub(startDoubleFocusPos);
			translateVal = Math.abs(transDiff.x) + Math.abs(transDiff.y);
			if (!outDeadZone) {
				outDeadZone = translateVal > doubleTransDeadZone;
				doubleFocusPos.set(startDoubleFocusPos);
				finalTransDiff.set(transDiff);
			}else{
				doubleFocusPos.sub(finalTransDiff);
			}
			//计算拉伸距离，排除死区
			diff = doubleDistance - startDoubleDistance;
			sclOutDeadZone = Math.abs(diff) > doubleSclDeadZone;
			if (!sclOutDeadZone) {
				realDoubleDistance = startDoubleDistance;
			}
			else {
				realDoubleDistance = doubleDistance - Math.signum(diff) * doubleSclDeadZone;
			}

			//计算位移
			if (cfg.enableTranslate) {
				if (action == MotionEvent.ACTION_MOVE) {
					doubleTranslate.set(doubleFocusPos).sub(startDoubleFocusPos).div(stageSclFactor);
					tickDoubleTranslate.set(doubleFocusPos).sub(lastDoubleFocusPos);
					lastDoubleFocusPos.set(doubleFocusPos);
					stagePos.set(startStagePos).add(doubleTranslate);
				}
			}
			//计算缩放
			if (cfg.enableScl) {
				if (action == MotionEvent.ACTION_MOVE) {
					doubleDistanceDiff = realDoubleDistance / startDoubleDistance;
					stageSclFactor = startStageScl * doubleDistanceDiff;
				}
			}

			Log.logT(TAG.RealtimeInfo, "手势实时信息: ");
			Log.logT(TAG.RealtimeInfo, "\t是否触发位移: %s, 位移量: %s/%s", outDeadZone, MathUtils.preciNum(translateVal), MathUtils.preciNum(doubleTransDeadZone));
			Log.logT(TAG.RealtimeInfo, "\t是否触发缩放: %s, 缩放量: %s/%s", sclOutDeadZone, MathUtils.preciNum(diff), MathUtils.preciNum(doubleSclDeadZone));
			if (action == MotionEvent.ACTION_MOVE) {
				//限制并应用缩放
				float oldScl = stageSclFactor;
				stageSclFactor = constrainScl(stageSclFactor);
				boolean isSclConstrain = oldScl != stageSclFactor;
				listener.onScale(stageSclFactor);
				
				//限制并应用位移
				boolean isConstrain = constrainRealStagePos();
				listener.onDoublePointerMove(realStagePos.x, realStagePos.y);

				String dirStr = doubleTranslate.getDirectionString();
				float sclDiff = stageSclFactor - lastStageSclFactor;
				float totalSclDiff = stageSclFactor - startStageScl;
				lastStageSclFactor = stageSclFactor;
				String sclingMode = sclDiff == 0 ?"0" : (sclDiff > 0 ?"+" : "-");
				Log.logT(TAG.RealtimeInfo, "\t位移: ");
				Log.logT(TAG.RealtimeInfo, "\t\t方向: %s", dirStr);
				Log.logT(TAG.RealtimeInfo, "\t\t开始位置: %s", startStagePos);
				Log.logT(TAG.RealtimeInfo, "\t\t步距离: %s", tickDoubleTranslate);
				Log.logT(TAG.RealtimeInfo, "\t\t总距离: %s", doubleTranslate);
				Log.logT(TAG.RealtimeInfo, "\t\t当前位置(%s): %s, 实际: %s", isConstrain ?"被约束": "未约束", stagePos, realStagePos);
				Log.logT(TAG.RealtimeInfo, "\t缩放: ");
				Log.logT(TAG.RealtimeInfo, "\t\t增减: %s", sclingMode);
				Log.logT(TAG.RealtimeInfo, "\t\t开始因子: %s", MathUtils.preciNum(startStageScl));
				Log.logT(TAG.RealtimeInfo, "\t\t步缩放量: %s", MathUtils.preciNum(sclDiff));
				Log.logT(TAG.RealtimeInfo, "\t\t总缩放量: %s", MathUtils.preciNum(totalSclDiff));
				Log.logT(TAG.RealtimeInfo, "\t\t当前因子(%s): %s", isSclConstrain ?"被约束": "未约束", MathUtils.preciNum(stageSclFactor));
			}
			
		} catch (Throwable e) {
			AppLog.dialogE("onTouch", e);
		}
		return true;
	}

	public boolean constrainRealStagePos() {
		Log.logT(TAG.ConstrainTranslate, "限制移动位置: ");
		Log.logT(TAG.ConstrainTranslate, "\t当前位移位置stagePos: %s", stagePos);
		Log.logT(TAG.ConstrainTranslate, "\t当前缩放偏移位置stageSclOffset: %s", stageSclOffset);

		updateStageSclOffset();
		Log.logT(TAG.ConstrainTranslate, "\t计算后缩放偏移位置updateStageSclOffset: %s", stageSclOffset);

		realStagePos.set(stagePos).add(stageSclOffset);
		Log.logT(TAG.ConstrainTranslate, "\t计算后实际位移位置realStagePos: %s", realStagePos);

		boolean ret = constrainMovement(realStagePos);
		Log.logT(TAG.ConstrainTranslate, "\t约束实际位移位置realStagePos: %s", realStagePos);

		decomposeRealStagePos(realStagePos);
		Log.logT(TAG.ConstrainTranslate, "\trealStagePos反解，stagePos: %s, stageSclOffset: %s", realStagePos, stagePos, stageSclOffset);

		oldPos.set(realStagePos);
		applyViewXY(realStagePos);
		Log.logT(TAG.ConstrainTranslate, "\t坐标轴%s转换: applyViewXY(realPos): %s -> %s", listener.coordinatesSigned(), oldPos, realStagePos);
		return ret;
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

	private boolean constrainMovement(Vector2 canvasPos) {
		if (!cfg.constrainMovement) return false;
		oldPos = canvasPos.clone();
		Log.logT(TAG.ConstrainTranslate, "\t约束位置constrainMovement");

		Vector2Int sSize = listener.getStageSize();
		Log.logT(TAG.ConstrainTranslate, "\t\tstageSize: %s", sSize);
		Vector2Int vSize = listener.getViewportSize();
		Log.logT(TAG.ConstrainTranslate, "\t\tviewportSize: %s", vSize);

		Log.logT(TAG.ConstrainTranslate, "\t\t是否有子布局: %s", listener.hasView());
		if (listener.hasView()) {
			float childWidth = sSize.x * stageSclFactor;
			float childHeight = sSize.y * stageSclFactor;
			int margin = 10;
			float xMin = margin;
			float xMax = vSize.x - childWidth - margin;
			float yMin = margin;
			float yMax = vSize.y - childHeight - margin;
			Log.logT(TAG.ConstrainTranslate, "\t\t限制参数: ");
			Log.logT(TAG.ConstrainTranslate, "\t\t\tstageSclFactor: %s", MathUtils.preciNum(stageSclFactor));
			Log.logT(TAG.ConstrainTranslate, "\t\t\tchildWidth: %s, childHeight: %s", MathUtils.preciNum(childWidth), MathUtils.preciNum(childHeight));
			Log.logT(TAG.ConstrainTranslate, "\t\t\tmargin: %s", MathUtils.preciNum(margin));
			Log.logT(TAG.ConstrainTranslate, "\t\t\txMin: %s, xMax: %s", MathUtils.preciNum(xMin), MathUtils.preciNum(xMax));
			Log.logT(TAG.ConstrainTranslate, "\t\t\tyMin: %s, yMax: %s", MathUtils.preciNum(yMin), MathUtils.preciNum(yMax));

			// 确保内容在滑动时留出 100 像素的空间
			//宽高小于视图
			boolean isOutofView = childWidth > vSize.x || childHeight > vSize.y;
			Log.logT(TAG.ConstrainTranslate, "\t\t宽高是否超出视图: %s", isOutofView);
			Log.logT(TAG.ConstrainTranslate, "\t\t约束前位置canvasPos: %s", canvasPos);
			if (!isOutofView) {
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
			Log.logT(TAG.ConstrainTranslate, "\t\t约束后位置canvasPos: %s", canvasPos);
		}
		return !canvasPos.equals(oldPos);
	}
	private float constrainScl(float scl) {
		if (!cfg.constrainScl) return scl;

		float newScl = Math.max(sclMinMaxLimit.x, Math.min(sclMinMaxLimit.y, scl));
		return newScl;
	}

    private float calculateDistance(MotionEvent event) {
        float dx = getApplyTouchX(1) - getApplyTouchX(0);
        float dy = getApplyTouchY(1) - getApplyTouchY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

	//用于安卓屏幕坐标轴系(1,-1)转换到(1,1)左下至右上坐标系
	private float getApplyTouchX(int pointer) {
		float originX = touchEvent.getX(pointer);
		float applyX = applyX(originX);
		return applyX;
	}
	private float getApplyTouchY(int pointer) {
		float originY = touchEvent.getY(pointer);
		float applyY = applyY(originY);
		return applyY;
	}
	private float applyX(float x) {
		return x;
	}
	private float applyY(float y) {
		return listener.getViewportSize().y - y;
	}
	private float applyViewX(float x) {
		int sign = listener.coordinatesSigned().x;
		return sign == 1 ?x : listener.getViewportSize().x - listener.getStageSize().x * stageSclFactor - x;
	}
	private float applyViewY(float y) {
		int sign = listener.coordinatesSigned().y;
		return sign == 1 ?y : listener.getViewportSize().y - listener.getStageSize().y * stageSclFactor - y;
	}
	private <T extends Vector2> T applyViewXY(T vec) {
		vec.set(applyViewX(vec.x), applyViewY(vec.y));
		return vec;
	}

}
