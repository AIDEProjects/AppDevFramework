package com.goldsprite.appdevframework.apputils;
import android.content.*;
import android.util.*;
import com.goldsprite.appdevframework.apputils.debugwindow.*;
import com.goldsprite.appdevframework.apputils.debugwindow.DebugWindow.LogEntry;
import java.util.*;
import com.goldsprite.appdevframework.R;
import java.io.*;
import com.goldsprite.appdevframework.log.Log;
import android.os.*;


public class LogView extends CustomListView {
	public enum TAG {
		LogAdd
		}

	private static LogView instance;
	private static List<LogEntry> logs = new ArrayList<LogEntry>(){{
			add(new LogEntry("Log-Info..."));
		}};
	private Context ctx;
	private LogAdapter logAdapter;
	public static int maxLine = 200;
	
	private static Handler mainHandler = new Handler(Looper.getMainLooper());
	
	
	public LogView(Context ctx) {
		super(ctx);
		init(ctx);
	}
	public LogView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		init(ctx);
	}

	private void init(Context ctx) {
		instance = this;
		this.ctx = ctx;

		//setFocusable(false);
		setDivider(null);
		logAdapter = new LogAdapter(ctx, R.layout.list_item_log, logs);
		setAdapter(logAdapter);
	}

	public static void addLog(final String str) {
		addLog("v", str);
	}
	public static void addLog(final String level, final String str) {
		Runnable run = new Runnable(){
			public void run(){
				Log.logT(TAG.LogAdd, "LogView添加Log: %s", str);

				logs.add(new LogEntry(level, str));
				Log.logT(TAG.LogAdd, "\t添加Log至LogEntryList");

				boolean isOutofMaxLine = logs.size() > maxLine;
				Log.logT(TAG.LogAdd, "\t是否超过最大行数(%s): %s", maxLine, isOutofMaxLine);

				if (isOutofMaxLine) {
					int len = logs.size() - maxLine;
					String moreStr = "";
					for (int i2=0;i2 < len;i2++) {
						logs.remove(0);
						moreStr += i2 + ", ";
					}
					Log.logT(TAG.LogAdd, "\t依次移除多余项: %s", moreStr);
				}

				boolean isViewInitialized = instance != null;
				Log.logT(TAG.LogAdd, "\t视图是否已初始化instance: %s", isViewInitialized);

				if (!isViewInitialized) {
					Log.logT(TAG.LogAdd, "LogView添加Log数据完成");
					return;
				}

				instance.logAdapter.notifyDataSetChanged();
				Log.logT(TAG.LogAdd, "\t\t通知适配器视图刷新");
				int count = instance.logAdapter.getCount();
				instance.setSelection(count - 1);
				Log.logT(TAG.LogAdd, "\t\t滑动到列表底部");

				Log.logT(TAG.LogAdd, "LogView添加Log数据并刷新视图完成");
			}
		};
		mainHandler.post(run);
	}
	public static void addErrLog(Throwable e) {
		String errStr = "";
		if (e != null) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			errStr = sw.toString();
		}
		addLog("e", "Err: \n" + errStr);
		//AppLog.toast("注意，运行时抛出了一个新的报错在Log");
	}
}

