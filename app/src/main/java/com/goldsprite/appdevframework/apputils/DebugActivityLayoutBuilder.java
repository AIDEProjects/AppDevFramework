package com.goldsprite.appdevframework.apputils;
import android.app.*;
import android.view.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import java.util.*;

public class DebugActivityLayoutBuilder {
	private View mainView;
	public View MainView() { return mainView; }

	private LinearLayout mainLayout;
	private RelativeLayout.LayoutParams mLayout_lp;

	private LogView logView;
	private DebugView debugView;

	public DebugActivityLayoutBuilder(final Activity ctx, final View mainView) {
		this(ctx, mainView, null);
	}

	public DebugActivityLayoutBuilder(final Activity ctx, final int layoutId, final Runnable initRun) {
		this(
			ctx, 
			LayoutInflater.from(ctx).inflate(layoutId, null), 
			initRun);
	}

	public DebugActivityLayoutBuilder(final Activity ctx, final int layoutId) {
		this(ctx, layoutId, null); 
	}

	public DebugActivityLayoutBuilder(final Activity ctx, final View mainView, final Runnable initRun) {
		ctx.setContentView(R.layout.debug_activity_main);
		mainLayout = (LinearLayout) ctx.findViewById(R.id.debugActivityLayout);
		debugView = (DebugView) ctx.findViewById(R.id.debugActivity_debugView);
		logView = (LogView) ctx.findViewById(R.id.debugActivity_logView);

		Runnable initLayout = new Runnable(){
			public void run() {
				View decorView = ctx.getWindow().getDecorView();
				int size = Math.min(decorView.getWidth(), decorView.getHeight());
				mLayout_lp = new RelativeLayout.LayoutParams(size, size);
				mLayout_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				mainLayout.setLayoutParams(mLayout_lp);

				DebugActivityLayoutBuilder.this.mainView = mainView;
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.MATCH_PARENT);
				mainView.setLayoutParams(lp2);
				mainLayout.addView(mainView);

				if (initRun != null) mainView.post(initRun);
			}
		};
		mainLayout.post(initLayout);
	}

	public DebugActivityLayoutBuilder showDebugView(final boolean show) {
		Runnable run = new Runnable(){
			public void run() {
				debugView.setVisibility(show ?View.VISIBLE : View.GONE);
				ajustAlign();
			}
		};
		mainLayout.post(run);
		return this;
	}
	public DebugActivityLayoutBuilder showLogiew(final boolean show) {
		Runnable run = new Runnable(){
			public void run() {
				logView.setVisibility(show ?View.VISIBLE : View.GONE);
				ajustAlign();
			}
		};
		mainLayout.post(run);
		return this;
	}

	private void ajustAlign() {
		boolean debugShow = debugView.getVisibility() == View.VISIBLE;
		boolean logShow = logView.getVisibility() == View.VISIBLE;
		Boolean[] visibs = { debugShow && logShow, debugShow, logShow };
		int[] aligns = {
			RelativeLayout.CENTER_IN_PARENT, 
			RelativeLayout.ALIGN_PARENT_BOTTOM, 
			RelativeLayout.ALIGN_PARENT_TOP
		};
		int index = Arrays.asList(visibs).indexOf(true);
		index = index == -1 ?2 : index;
		mLayout_lp.addRule(aligns[index]);
	}

}
