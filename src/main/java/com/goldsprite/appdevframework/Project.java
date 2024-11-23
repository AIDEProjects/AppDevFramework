package com.goldsprite.appdevframework;
import java.io.*;
import com.goldsprite.appdevframework.log.*;
import android.app.*;

public class Project{
	
	public static boolean launched = false;
	public static String gdPath = "/sdcard/goldspriteProjects/";
	public static String projName = "defaultLog";
	public static String ProjName(){ return projName; }
	public static String ProjPath(){ return gdPath + ProjName() + "/"; }
	public static String logPath = "";
	/*
	 如果未启动: 获取列表所有logs并获取最大索引号+1为新路径
	 如果启动，直接返回路径
	 */
	//以下垃圾代码
	public static String NewLogPath(){
		String logParentPath = ProjPath()+"logs/";
		if (launched){
			//AppLog.toast("追加log");
			return logPath;
		}
		String[] lists = new File(logParentPath).list();
		if (lists == null || lists.length == 0){
			launched = true;
			return logPath = logParentPath + "logs_0.txt";
		}

		int maxIndex = 0;
		for (String i : lists){
			int logIndex = i.lastIndexOf("logs_");
			int pointIndex = i.lastIndexOf(".");
			int num = Integer.parseInt(i.substring(logIndex + 5, pointIndex));
			maxIndex = num > maxIndex ? num : maxIndex;
		}
		launched = true;
		logPath = logParentPath + lists[0].substring(0, lists[0].lastIndexOf("logs_") + 5) + (maxIndex + 1) + ".txt";
		return logPath;
	}

	public static String OptionsPath() { return ProjPath() + "Options.txt"; }
}
