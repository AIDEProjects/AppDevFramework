package com.goldsprite.appdevframework.log;

import java.io.*;
import com.goldsprite.appdevframework.apputils.*;
import java.util.*;
import com.goldsprite.appdevframework.utils.*;
import java.util.concurrent.locks.*;
import android.os.*;
import java.util.concurrent.*;

public class Log {
	public enum TAG { Default }

	public final static Map<Enum, Integer> showTags = new LinkedHashMap<Enum, Integer>();

	public static boolean hasSavePerm = true;

	private static boolean viewOutput = true;
	public static void setViewOutput(boolean boo) { viewOutput = boo; }

	private static ConcurrentLinkedQueue<Runnable> addLogQueue = new ConcurrentLinkedQueue<>();
	private static Handler addLogHandler;

	
	static {
		initShowTags();
		addLogHandler = new Handler(Looper.getMainLooper());
	}

	private static void initShowTags() {
		setTagMode(TAG.Default, true, true);
		setTagMode(GestureHandler.TAG.InCenter, false, false);
		setTagMode(GestureHandler.TAG.ConstrainTranslate, false, false);
		setTagMode(GestureHandler.TAG.RealtimeInfo, false, false);
		setTagMode(FreeTransformLayout.TAG.LifeCycle, true, true);
		setTagMode(LogView.TAG.LogAdd, false, false); // 此项禁止视图显示会造成循环调用
		
	}
	

	public static void logT(final Enum tag, final String log) {
		Runnable addLogTask = new Runnable(){
			public void run() {
				logTTask(tag, log);
			}
		};
		addLogQueue.offer(addLogTask);

		Runnable first = addLogQueue.poll();
		if (first != null) addLogHandler.post(first);
	}

	private static void logTTask(Enum tag, String log) {
		Integer tagMode = showTags.get(tag);
		if (tagMode == null) return;

		String tagName = StringUtils.getEnumFullName(tag);

		// tag前缀
		log = String.format("[%s]: %s", tagName, log);
		log = log.replace("\n", String.format("\n[%s]: ", tagName));
		
		// 在View模式下输出日志
		if (LogMode.isViewMode(tagMode)) {
			LogView.addLog(log);
		}
		// 在本地模式下保存日志
		if (LogMode.isLocalMode(tagMode)) {
			//加时间戳
			String timeStamp = StringUtils.getFormatTimeStamp("HH:mm:ss:SSS");
			log = String.format("[%s] %s", timeStamp, log);
			log = log.replace("\n", String.format("\n[%s] ", timeStamp));
			AppLog.saveLog(log);
		}

	}

	public static void logT(Enum tag, String log, Object... objs) {
		try {
			log = String.format(log, objs);
			logT(tag, log);
		} catch (Exception e) {
			logErr("格式化log打印异常", e);
		}
	}


	public static void log(String log) {
		logT(TAG.Default, log);
	}
	public static void log(String log, Object... objs) {
		logT(TAG.Default, log, objs);
	}


	public static void logErr(Throwable e) {
		StackTraceElement methodStackTrace = Thread.currentThread().getStackTrace()[2];
		String msg = methodStackTrace.getMethodName();
		logErr(msg, e, false);
	}
	public static void logErr(String msg, Throwable e) {
		logErr(msg, e, false);
	}
	public static void logErr(Throwable e, boolean isOrigin) {
		StackTraceElement methodStackTrace = Thread.currentThread().getStackTrace()[2];
		String msg = methodStackTrace.getMethodName();
		logErr(msg, e, isOrigin);
	}
	//isOrigin参数用于AppLog可能异常时
	public static void logErr(String msg, Throwable e, boolean isOrigin) {
		String log = msg + ": \n" + (e == null ?"" : getStackTraceStr(e));
		//if(!isOrigin) AppLog.toast(String.format("发生异常：%s, log已存储到本地.", msg));

		LogView.addLog("e", log);
		AppLog.saveLog(log);
	}


	public static String getStackTraceStr(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}


	public static class LogMode {
		// 定义两个模式的位标志
		public static final int VIEW_MODE = 1 << 0;  // 位0
		public static final int LOCAL_MODE = 1 << 1; // 位1

		// 编码：将多个标志存入一个 int 中
		public static int encodeModeAll(boolean all) {
			return encodeMode(all, all);
		}
		public static int encodeMode(boolean local, boolean view) {
			int mode = 0;
			if (view) {
				mode |= VIEW_MODE;  // 将 VIEW_MODE 的位设置为 1
			}
			if (local) {
				mode |= LOCAL_MODE; // 将 LOCAL_MODE 的位设置为 1
			}
			return mode;
		}

		// 解码：从一个 int 中提取各个标志位的值
		public static boolean isViewMode(int mode) {
			return (mode & VIEW_MODE) != 0;  // 如果位0为1，则表示启用
		}

		public static boolean isLocalMode(int mode) {
			return (mode & LOCAL_MODE) != 0; // 如果位1为1，则表示启用
		}
	}

	
	public static void setTagModeAll(Enum tag, boolean all){
		setTagMode(tag, all, all);
	}
	public static void setTagMode(Enum tag, boolean local, boolean view){
		showTags.put(tag, LogMode.encodeMode(local, view));
	}

}
