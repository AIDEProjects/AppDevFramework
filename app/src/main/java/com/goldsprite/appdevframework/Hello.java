package com.goldsprite.appdevframework;

import android.app.*;
import android.widget.*;

public class Hello
{
	public static void hello(Activity ctx){
		Toast.makeText(ctx, "hello", Toast.LENGTH_SHORT).show();
	}
}
