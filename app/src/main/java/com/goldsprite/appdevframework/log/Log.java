package com.goldsprite.appdevframework.log;

import java.io.*;
import com.goldsprite.appdevframework.apputils.*;
import java.util.*;

public class Log
{
	public enum LogTag{
		Default
	}
	
	public final static Map<Enum, Boolean> banTags = new LinkedHashMap<Enum, Boolean>();
	
	
	public static boolean isTagHide(Enum tag){
		if(banTags.containsKey(tag) && banTags.get(tag).equals(true)) return true;
		return false;
	}
	
	
	public static void logT(Enum tag, String log){
		if(isTagHide(tag)) return;
		log = String.format("[%s]: %s", tag, log);
		LogView.addLog(log);
		AppLog.saveLog(log);
	}
	public static void logfT(Enum tag, String log, Object... objs){
		try{
			log = String.format(log, objs);
			logT(tag, log);
		}catch(Exception e){
			logErr("格式化log打印异常", e);
		}
	}
	
	
	public static void log(String log){
		logT(LogTag.Default, log);
	}
	public static void logf(String log, Object... objs){
		logfT(LogTag.Default, log, objs);
	}
	
	
	public static void logErr(Throwable e){
		StackTraceElement methodStackTrace = Thread.currentThread().getStackTrace()[2];
		String msg = methodStackTrace.getMethodName();
		logErr(msg, e, false);
	}
	public static void logErr(String msg, Throwable e){
		logErr(msg, e, false);
	}
	public static void logErr(Throwable e, boolean isOrigin){
		StackTraceElement methodStackTrace = Thread.currentThread().getStackTrace()[2];
		String msg = methodStackTrace.getMethodName();
		logErr(msg, e, isOrigin);
	}
	//isOrigin参数用于AppLog可能异常时
	public static void logErr(String msg, Throwable e, boolean isOrigin){
		String log = msg+": \n"+(e==null ?"" :getStackTraceStr(e));
		//if(!isOrigin) AppLog.toast(String.format("发生异常：%s, log已存储到本地.", msg));

		LogView.addLog("e", log);
		AppLog.saveLog(log);
	}

	
	public static String getStackTraceStr(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
}
