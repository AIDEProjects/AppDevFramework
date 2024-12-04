package com.goldsprite.appdevframework.apputils;

import android.content.*;
import android.util.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import com.goldsprite.appdevframework.log.*;
import com.goldsprite.appdevframework.log.Debug;
import com.goldsprite.appdevframework.log.Log;
import java.util.*;
import android.os.*;

public class DebugView extends CustomListView {
	private static DebugView instance;
	private Context ctx;
	private List<String> debugInfos;
	private ArrayAdapter<String> debugAdapter;

	private static Handler mainHandler = new Handler(Looper.getMainLooper());


	public DebugView(Context ctx) {
		super(ctx);
		init(ctx);
	}
	public DebugView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		init(ctx);
	}

	private void init(Context ctx) {
		instance = this;
		this.ctx = ctx;

		setDivider(null);
		debugInfos = Debug.debugInfos = new ArrayList<String>();
		for (int i=0;i < 20;i++) {
			debugInfos.add(i, "");
		}
		debugInfos.set(0, "Debug-Info...");
		debugAdapter = new ArrayAdapter<String>(ctx, R.layout.list_item_debug, debugInfos);
		setAdapter(debugAdapter);
	}

	public static void setDebugInfo(final int line, final String str) {
		Runnable run = new Runnable(){
			public void run() {
				Log.logT(LogView.TAG.LogAdd, "DebugView添加DebugInfo: line: %s, %s", line, str);

				Debug.setDebugInfo(line, str);
				Log.logT(LogView.TAG.LogAdd, "\t添加DebugInfo至DebugInfos");

				boolean isViewInitialized = instance != null;
				Log.logT(LogView.TAG.LogAdd, "\t视图是否已初始化instance: %s", isViewInitialized);

				if (!isViewInitialized) {
					Log.logT(LogView.TAG.LogAdd, "DebugView添加DebugInfo数据完成");
					return;
				}
				
				instance.debugAdapter.notifyDataSetChanged();
				Log.logT(LogView.TAG.LogAdd, "\t\t通知适配器视图刷新");
				Log.logT(LogView.TAG.LogAdd, "DebugView添加DebugInfo数据并刷新视图完成");
			}
		};
		mainHandler.post(run);
	}
}
