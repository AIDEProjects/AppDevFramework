package com.goldsprite.appdevframework.apputils;
import android.content.*;
import android.util.*;
import com.goldsprite.appdevframework.apputils.debugwindow.*;
import com.goldsprite.appdevframework.apputils.debugwindow.DebugWindow.LogEntry;
import java.util.*;
import com.goldsprite.appdevframework.R;
import java.io.*;
import com.goldsprite.appdevframework.log.*;


public class LogView extends CustomListView {
	private static LogView instance;
	private Context ctx;
	private static List<LogEntry> logs;
	static{
		logs = new ArrayList<>();
		logs.add(new LogEntry("Log-Info..."));
	}
	private LogAdapter logAdapter;
	public static int maxLine = 200;
	private static int logsTick;
	
	
	public LogView(Context ctx){
		super(ctx);
		init(ctx);
	}
	public LogView(Context ctx, AttributeSet attr){
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
		String[] strs = str.split("\n");
		for(String i : strs){
			logs.add(new LogEntry(level, String.format("[%d] %s", logsTick++, i)));
		}
		if (logs.size() > maxLine) {
			int len = logs.size()-maxLine;
			for(int i2=0;i2<len;i2++){
				logs.remove(0);
			}
		}
		
		if(instance == null) return;
		instance.logAdapter.notifyDataSetChanged();
		instance.post(new Runnable(){public void run() {
					int count = instance.logAdapter.getCount();
					instance.setSelection(count-1);
				}});
	}
	public static void addErrLog(Throwable e) {
		if(instance == null) return;
		
		String errStr = "";
		if(e != null){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			errStr = sw.toString();
		}
		addLog("e", "Err: \n"+errStr);
		//AppLog.toast("注意，运行时抛出了一个新的报错在Log");
	}
}
