package com.goldsprite.appdevframework.io;

import com.goldsprite.appdevframework.log.*;
import java.io.*;

public class FilesTool {

	public static void deleteFile(String logPath) {
		try {
			File file = new File(logPath);
			if (!file.exists() || !file.isFile()) return;

			file.delete();
		} catch (Exception e) {
			AppLog.dialog("删除文件失败: ", Log.getStackTraceStr(e));
		}
	}

	public static void writeString(String filePath, String str, boolean isMkdirs, boolean isAppend) {
		try {
			File file = new File(filePath);
			if (isMkdirs) {
				file.getParentFile().mkdirs();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppend));
			bw.write(str);
			bw.newLine();
			bw.close();
		} catch (Exception e) {
			AppLog.dialog("写入文件失败: ", Log.getStackTraceStr(e));
		}
	}
	
	
	public static boolean exists(String path) {
		return new File(path).exists();
	}
	
}
