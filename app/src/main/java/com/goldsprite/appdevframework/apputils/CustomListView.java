package com.goldsprite.appdevframework.apputils;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class CustomListView extends ListView
{
	
	public CustomListView(Context context) {
		super(context);
	}
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return isFocusable() ? super.onTouchEvent(ev) : false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 禁止父视图拦截触摸事件
		return isFocusable() ? super.onInterceptTouchEvent(ev) : false;
	}
	
}
