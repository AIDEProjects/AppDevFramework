package com.goldsprite.appdevframework.apputils;
import android.view.*;
import com.goldsprite.appdevframework.log.*;
import java.lang.reflect.*;
import com.goldsprite.appdevframework.math.*;
import android.app.*;
import android.content.*;

public class AppUtils
{
	
	public static String getMotionEventActionFieldName(int action) {
        // 遍历 MotionEvent 类的所有字段
        Field[] fields = MotionEvent.class.getFields();

        for (Field field : fields) {
            try {
                // 检查字段是否为静态字段且是 int 类型
                if (field.getType() == int.class) {

                    // 获取字段的值
                    int fieldValue = field.getInt(null); // null 是因为是静态字段

                    // 比较字段值与传入的 action 值
                    if (fieldValue == action) {
                        return field.getName(); // 返回字段名
                    }
                }
            } catch (Exception e) {
				AppLog.dialog("getActionFieldName异常", Log.getStackTraceStr(e));
            }
        }
        return null; // 没有找到匹配的字段
    }
	
	
	public static Vector2Int getViewportSize(Activity ctx){
		View decorView = ctx.getWindow().getDecorView();
		return new Vector2Int(decorView.getWidth(), decorView.getHeight());
	}
	
	
	public static Activity ctx;
	public static void setCtx(Activity ctx) {
		AppUtils.ctx = ctx;
	}
	
	public static float dp2px(float dp){
		float dpi = ctx.getResources().getDisplayMetrics().density;
		return dp * dpi;
	}
	public static int dp2pxi(float dp){
		return Math.round(dp2px(dp));
	}
	
}
