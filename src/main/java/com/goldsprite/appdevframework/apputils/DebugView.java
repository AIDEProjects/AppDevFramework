package com.goldsprite.appdevframework.apputils;

import android.content.*;
import android.util.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import com.goldsprite.appdevframework.log.*;
import java.util.*;

public class DebugView extends CustomListView
{
	private static DebugView instance;
	private Context ctx;
	private List<String> debugInfos;
	private ArrayAdapter<String> debugAdapter;


	public DebugView(Context ctx){
		super(ctx);
		init(ctx);
	}
	public DebugView(Context ctx, AttributeSet attr){
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
		if(instance==null) return;
		
		instance.post(
			new Runnable(){
				public void run() {
					Debug.setDebugInfo(line, str);
					instance.debugAdapter.notifyDataSetChanged();
				}
			}
		);
	}
}
