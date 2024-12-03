package com.goldsprite.appdevframework.log;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import com.goldsprite.appdevframework.*;
import com.goldsprite.appdevframework.io.*;
import com.goldsprite.appdevframework.apputils.*;

public class AppLog {

	//在短暂toast后结束应用
	public static void finishWithToast(String str) {
		try{
			toast(str);
			new Handler().postDelayed(
				new Runnable(){
					public void run() {
						AppUtils.ctx.finish();
					}
				}, 
				1000
			);
		}catch (Exception e){
			Log.logErr(e, true);
		}
	}
	public static void toast(Object strObj) {
		try{
			final String str = "" + strObj;
			AppUtils.ctx.runOnUiThread(
				new Runnable(){
					public void run() {
						Toast.makeText(AppUtils.ctx, str, Toast.LENGTH_SHORT).show();
					}
				}
			);
		}catch (Exception e){
			Log.logErr(e, true);
		}
	}

	public static void dialogE(Object titleObj, Throwable e) {
		String msg = Log.getStackTraceStr(e);
		dialog(titleObj, msg, null, null);
	}
	public static void dialog(Object titleObj, Object msgObj) {
		dialog(titleObj, msgObj, null, null);
	}
	public static void dialog(Object titleObj, Object msgObj, final Runnable sureRun, final Runnable cancleRun) {
		try{
			final String title = "" + titleObj;
			final String msg = "" + msgObj;
			AppUtils.ctx.runOnUiThread(
				new Runnable(){
					public void run() {
						try {
							AlertDialog.Builder builder = new AlertDialog.Builder(AppUtils.ctx);
							builder.setTitle(title);
							builder.setMessage(msg);
							builder.setPositiveButton("确定", 
								new AlertDialog.OnClickListener(){
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										if (sureRun != null) {
											sureRun.run();
										}
									}
								}
							);
							builder.setNegativeButton("取消", 
								new AlertDialog.OnClickListener(){
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										if (cancleRun != null) {
											cancleRun.run();
										}
									}
								}
							);
							AlertDialog dialog = builder.create();
							dialog.show();
						} catch (Exception e) {
							toast("创建dialog代码异常: " + e.getMessage());
						}
					}
				}
			);
		}catch (Exception e){
			Log.logErr(e, true);
		}
	}
	
	
	public static void saveCrashLog(){
		
	}

	
	public static void clearLog() {
		try {
			FilesTool.deleteFile(Project.NewLogPath());
		} catch (Exception e2) {
			dialog("清理Log出错: ", Log.getStackTraceStr(e2));
		}
	}
	public static void saveLog(String log) {
		try {
			if(!Log.hasSavePerm){
				return;
			}
			boolean isMkdirs = true;
			boolean isAppend = true;
			FilesTool.writeString(Project.NewLogPath(), log, isMkdirs, isAppend);
		} catch (Exception e2) {
			dialog("保存Log出错: ", Log.getStackTraceStr(e2));
		}
	}

}
