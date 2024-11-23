package com.goldsprite.appdevframework.log;

import java.io.*;

public class Logcat{
	public static StringBuilder logcatBuilder = new StringBuilder();


	public static void clearLogcat(){
		try{
			Runtime.getRuntime().exec("logcat -c");
		}catch (Exception e){
			Log.logErr("清理logcat出错", e);
		}
	}

	public static void startLogcatReader(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					try{
						// 启动 logcat 进程
						Process process = Runtime.getRuntime().exec("logcat *:E");
						BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

						// 持续读取日志
						String line;
						while ((line = bufferedReader.readLine()) != null){
							synchronized (logcatBuilder){
								logcatBuilder.append(line);
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}).start();
    }

	public static boolean printAndClearCrashLogcat(){
		boolean ret = false;
		new Thread(){
			public void run(){
				printCrashLogcat();
				clearLogcat();
			}
		}.start();
		return ret;
	}
	public static boolean printCrashLogcat(){
		boolean ret = false;
		String line;
		int lineCount=0;
		try{
			// 启动 logcat 进程
			Process process = Runtime.getRuntime().exec("logcat -d AndroidRuntime:E *:S");
			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream())
			);

			// 持续读取日志
			while ((line = bufferedReader.readLine()) != null){
				synchronized (logcatBuilder){
					logcatBuilder.append(line).append("\n");
					lineCount++;
				}
			}
			String crashLog = logcatBuilder.toString() + "\n";
			if (crashLog.contains("FATAL EXCEPTION")){
				AppLog.toast("logcat读取到新的崩溃日志.");
				Log.logErr(crashLog, null);
				ret = true;
			}
			else{
				AppLog.toast("无崩溃日志");
				ret = false;
			}
		}catch (Exception e){
			AppLog.dialogE("打印logcat异常日志出错", e);
		}
		return ret;
    }

}
