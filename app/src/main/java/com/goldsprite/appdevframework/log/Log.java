package com.goldsprite.appdevframework.log;

import java.io.*;
import com.goldsprite.appdevframework.apputils.*;
import java.util.*;
import com.goldsprite.appdevframework.utils.*;

public class Log {
	public enum TAG {
		Default
		}

	public final static Map<Enum, Boolean> banTags = new LinkedHashMap<Enum, Boolean>(){
		{
			put(GestureHandler.TAG.InCenter, false);
			put(GestureHandler.TAG.ConstrainTranslate, true);
			put(FreeTransformLayout.TAG.LifeCycle, false);
		}
	};

	public static boolean hasSavePerm = true;


	public static boolean isTagHide(Enum tag) {
		if (banTags.containsKey(tag) && banTags.get(tag).equals(true)) return true;
		return false;
	}


	public static void logT(Enum tag, String log) {
		if (isTagHide(tag)) return;
		String tagName = StringUtils.getEnumFullName(tag);
		log = log.replace("\n", String.format("\n[%s]: ", tagName));
		log = String.format("[%s]: %s", tagName, log);
		LogView.addLog(log);
		AppLog.saveLog(log);
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

}
