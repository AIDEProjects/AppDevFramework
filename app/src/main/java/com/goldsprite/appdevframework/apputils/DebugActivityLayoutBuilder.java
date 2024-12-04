package com.goldsprite.appdevframework.apputils;
import android.content.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import android.app.*;
import android.view.*;

public class DebugActivityLayoutBuilder {
	private View mLayoutView;
	public View MLayoutView() { return mLayoutView; }

	public DebugActivityLayoutBuilder(final Activity ctx, final View mainView, final Runnable initRun) {
		final LinearLayout layout = (LinearLayout) ctx.findViewById(R.id.debugActivityLayout);
		Runnable initLayout = new Runnable(){
			public void run() {
				View decorView = ctx.getWindow().getDecorView();
				int size = Math.min(decorView.getWidth(), decorView.getHeight());
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				layout.setLayoutParams(lp);

				mLayoutView = mainView;
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.MATCH_PARENT);
				mLayoutView.setLayoutParams(lp2);
				layout.addView(mLayoutView);

				if (initRun != null) mLayoutView.post(initRun);
			}
		};
		layout.post(initLayout);
	}

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


}
