package com.goldsprite.appdevframework.apputils.debugwindow;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.app.*;
import com.goldsprite.appdevframework.log.*;

import com.goldsprite.appdevframework.R;
import com.goldsprite.appdevframework.apputils.*;


public class DebugWindow {

	private static Activity ctx;
	private static DebugWindow instance;
	private ToggleButton floatingDebugLayout_toggleButton;
	public LinearLayout floatingDebugLayout;
	private CustomListView debugInfoList;
	private List<String> debugInfos;
	private ArrayAdapter<String> debugInfoListAdapter;
	private long flushDebugPanelInterval = 50;
	private boolean isFixedDebugInfo;
	private Runnable flushDebugPanelRunnable;

	private ToggleButton floatingLogLayout_toggleButton;
	public LinearLayout floatingLogLayout;
	private ListView logList;
	private ArrayList<LogEntry> logs;
	private static int logsTick;
	private LogAdapter logListAdapter;
	public static int maxLine = 200;
	
	private ToggleButton cgDebugBackgroundBtn;
	private boolean debugBackground;

	private static DebugWindow.Hierarchy hierarchy;


	public DebugWindow(Activity ctx) {
		this.ctx = ctx;
		instance = this;
		hierarchy = new Hierarchy();

		init();
	}

	private void init() {
		//获取布局并添加到主布局
		floatingDebugLayout = (LinearLayout) ctx.findViewById(R.id.floatingDebugLayout);
		View floatingDebugWindow = LayoutInflater.from(ctx).inflate(R.layout.debug_window, null);
		floatingDebugLayout.addView(floatingDebugWindow);

		//设置切换透明按钮
		cgDebugBackgroundBtn = (ToggleButton)ctx.findViewById(R.id.floatingDebugLayout_toggleTouchable);
		cgDebugBackgroundBtn.setOnClickListener(
			new View.OnClickListener(){
				public void onClick(View v){
					cgBackground();
				}
			}
		);
		
		//初始化调试板
		debugInfoList = floatingDebugWindow.findViewById(R.id.debugInfoList);
		debugInfoList.setDivider(null);
		debugInfos = Debug.debugInfos = new ArrayList<String>();
		for (int i=0;i < 20;i++) {
			debugInfos.add(i, "");
		}
		debugInfos.set(0, "Debug-Info...");
		debugInfoListAdapter = new ArrayAdapter<String>(ctx, R.layout.list_item_debug, debugInfos);
		debugInfoList.setAdapter(debugInfoListAdapter);

		floatingDebugLayout.setTranslationY(-5000);
		floatingDebugLayout_toggleButton = (ToggleButton)ctx.findViewById(R.id.floatingDebugLayout_toggleButton);
		floatingDebugLayout_toggleButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
					if (isChecked) {
						showDebugLayout();
					} else {
						hideDebugLayout();
					}
				}

				private void showDebugLayout() {
					floatingDebugLayout.setTranslationY(-floatingDebugLayout.getHeight());
					floatingDebugLayout.setVisibility(View.VISIBLE);
					floatingDebugLayout.animate()
						.translationY(0)
						.setDuration(300)
						.start();
				}

				private void hideDebugLayout() {
					floatingDebugLayout.animate()
						.translationY(-floatingDebugLayout.getHeight())
						.setDuration(300)
						.withEndAction(new Runnable(){public void run() {
								floatingDebugLayout.setVisibility(View.GONE);
							}})
						.start();
				}
			});

			
		//初始化日志板
		floatingLogLayout = (LinearLayout) ctx.findViewById(R.id.floatingLogLayout);
		View floatingLogWindow = LayoutInflater.from(ctx).inflate(R.layout.log_window, null);
		floatingLogLayout.addView(floatingLogWindow);

		logList = floatingLogWindow.findViewById(R.id.logList);
		logList.setDivider(null);
		logs = new ArrayList<>();
		logs.add(new LogEntry("Log-Info..."));
		logListAdapter = new LogAdapter(ctx, R.layout.list_item_log, logs);
		logList.setAdapter(logListAdapter);
		
		floatingLogLayout.setTranslationY(-5000);
		floatingLogLayout_toggleButton = (ToggleButton)ctx.findViewById(R.id.floatingLogLayout_toggleButton);
		floatingLogLayout_toggleButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
					if (isChecked) {
						showLogLayout();
					} else {
						hideLogLayout();
					}
				}

				private void showLogLayout() {
					floatingLogLayout.setTranslationY(-floatingLogLayout.getHeight());
					floatingLogLayout.setVisibility(View.VISIBLE);
					floatingLogLayout.animate()
						.translationY(0)
						.setDuration(300)
						.start();
				}

				private void hideLogLayout() {
					floatingLogLayout.animate()
						.translationY(-floatingLogLayout.getHeight())
						.setDuration(300)
						.withEndAction(new Runnable(){public void run() {
								floatingLogLayout.setVisibility(View.GONE);
							}})
						.start();
				}
			});
		
		//创建刷新线程
		/*flushDebugPanelRunnable = new Runnable(){
			public void run(){
				instance.debugInfoListAdapter.notifyDataSetChanged();
				debugInfoList.postDelayed(this, flushDebugPanelInterval);
			}
		};
		debugInfoList.post(flushDebugPanelRunnable);
		*/
	}

	public static void setDebugInfo(final int line, final String str) {
		ctx.runOnUiThread(new Runnable(){public void run() {
					Debug.setDebugInfo(line, str);
					instance.debugInfoListAdapter.notifyDataSetChanged();
				}});
	}

	public static void addLog(final String str) {
		addLog("v", str);
	}
	public static void addLog(final String level, final String str) {
		ctx.runOnUiThread(new Runnable(){public void run() {
					String[] strs = str.split("\n");
					for(String i : strs){
						instance.logs.add(new LogEntry(level, String.format("[%d] %s", logsTick++, i)));
					}
					if (instance.logs.size() > maxLine) {
						int len = instance.logs.size()-maxLine;
						for(int i2=0;i2<len;i2++){
							instance.logs.remove(0);
						}
					}
					instance.logListAdapter.notifyDataSetChanged();
				}});
	}
	public static void addErrLog(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		addLog("e", "Err: \n"+sw.toString());
		AppLog.toast("注意，运行时抛出了一个新的报错在Log");
	}
	
	public void cgBackground(){
		debugBackground = !debugBackground;
		if(!debugBackground){
			floatingDebugLayout.setBackgroundColor(Color.parseColor("#00EEEEEE"));
			floatingLogLayout.setBackgroundColor(Color.parseColor("#00EEEEEE"));
			floatingDebugLayout.setFocusable(false);
		}else{
			floatingDebugLayout.setBackgroundColor(Color.parseColor("#555555"));
			floatingLogLayout.setBackgroundColor(Color.parseColor("#555555"));
			floatingDebugLayout.setFocusable(true);
		}
	}


	public static Hierarchy getHierarchy() {
		return hierarchy;
	}

	HashMap<String, Object>hierarchy_map = new HashMap<>();

	public class Hierarchy {

		/*public void bindVector2(String label, Vector2 vec2){
		 if(!hierarchy_map.containsKey(label)){
		 hierarchy_map.put(label, vec2);
		 }
		 }*/

	}
	
	
	public static class LogEntry {
		public String level;
		public String msg;

		public LogEntry(String msg){
			this("v", msg);
		}
		public LogEntry(String level, String msg){
			this.level = level;
			this.msg = msg;
		}
	}
	
	

}
