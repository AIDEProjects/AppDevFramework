package com.goldsprite.appdevframework.log;

import android.content.*;
import android.os.*;
import java.io.*;
import java.util.*;

import java.lang.Process;
import com.goldsprite.appdevframework.apputils.*;

public class Logcat
{
	public static StringBuilder logcatBuilder = new StringBuilder();


	public static void clearLogcat() {
		try {
			Runtime.getRuntime().exec("logcat -c");
		}
		catch (Exception e) {
			Log.logErr("清理logcat出错", e);
		}
	}

	public static void startLogcatReader() {
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 启动 logcat 进程
						Process process = Runtime.getRuntime().exec("logcat *:E");
						BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));

						// 持续读取日志
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							synchronized (logcatBuilder) {
								logcatBuilder.append(line);
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
	}

	public static boolean printAndClearCrashLogcat() {
		boolean ret = false;
		new Thread(){
			public void run() {
				printCrashLogcat();
				clearLogcat();
			}
		}.start();
		return ret;
	}
	public static boolean printCrashLogcat() {
		boolean ret = false;
		String line;
		int lineCount=0;
		try {
			// 启动 logcat 进程
			Process process = Runtime.getRuntime().exec("logcat -d AndroidRuntime:E *:S");
			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream())
			);

			// 持续读取日志
			while ((line = bufferedReader.readLine()) != null) {
				synchronized (logcatBuilder) {
					logcatBuilder.append(line).append("\n");
					lineCount++;
				}
			}
			String crashLog = logcatBuilder.toString() + "\n";
			if (crashLog.contains("FATAL EXCEPTION")) {
				AppLog.toast("logcat读取到新的崩溃日志.");
				Log.logErr(crashLog, null);
				ret = true;
			}
			else {
				AppLog.toast("无崩溃日志");
				ret = false;
			}
		}
		catch (Exception e) {
			AppLog.dialogE("打印logcat异常日志出错", e);
		}
		return ret;
	}


	private static long startTime, timestamp;
	private static long timeTick;
	/**
	 * 获取 DropBoxManager 中的崩溃日志
	 *
	 * @return 日志内容
	 */
	public static boolean fetchDropBoxLogs(String[] str) {
		long currentTime = System.currentTimeMillis();
		startTime = currentTime - 1000 * 60;
		timeTick = startTime;
		timestamp = startTime;
		
		StringBuilder logContent = new StringBuilder();
		int i=0;
		
		try {
			DropBoxManager dropBoxManager = (DropBoxManager) AppUtils.ctx.getSystemService(Context.DROPBOX_SERVICE);
			if (dropBoxManager == null) {
				str[0] = "DropBoxManager is not available on this device.";
				return false;
			}

			// 遍历 DropBoxManager 中的日志项
			DropBoxManager.Entry entry;
			entry = dropBoxManager.getNextEntry(null, timestamp);
			while (entry != null && timeTick > 0) {
				String tag = entry.getTag();
				//long timestamp = (timeTick-=interval);
				timestamp = entry.getTimeMillis();

				// 只获取应用崩溃相关日志
				if ("system_app_crash".equals(tag) || "data_app_crash".equals(tag)) {
					logContent.append("Tag: ").append(tag).append("\n");
					logContent.append("Time: ").append(new Date(timestamp)).append("\n");

					// 读取日志内容
					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = entry.getInputStream().read(buffer)) != -1) {
						logContent.append(new String(buffer, 0, bytesRead));
					}
					logContent.append("\n---\n");
				}
				entry.close();

				// 获取下一条日志
				entry = dropBoxManager.getNextEntry(null, timestamp);
				i++;
			}
		}
		catch (Exception e) {
			logContent.append("Error reading DropBox logs: ").append(e.getMessage());
		}

		if (logContent.length() == 0) {
			logContent.append("No recent crash logs found.");
		}
		else {
			logContent.append("\n" + i + "条 crash logs found.");
		}

		str[0] = logContent.toString();
		return logContent.length() != 0;
	}

}
