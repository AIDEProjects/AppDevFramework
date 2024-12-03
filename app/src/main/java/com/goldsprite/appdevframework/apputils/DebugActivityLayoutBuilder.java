package com.goldsprite.appdevframework.apputils;
import android.content.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import android.app.*;
import android.view.*;

public class DebugActivityLayoutBuilder
{
	public DebugActivityLayoutBuilder(final Activity ctx, final int layoutId){
		final LinearLayout layout = (LinearLayout) ctx.findViewById(R.id.debugActivityLayout);
		Runnable initLayout = new Runnable(){
			public void run() {
				View decorView = ctx.getWindow().getDecorView();
				int size = Math.min(decorView.getWidth(), decorView.getHeight());
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				layout.setLayoutParams(lp);

				View v = LayoutInflater.from(ctx).inflate(layoutId, null);
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.MATCH_PARENT);
				v.setLayoutParams(lp2);
				layout.addView(v);
			}
		};
		layout.post(initLayout);
	}
}
