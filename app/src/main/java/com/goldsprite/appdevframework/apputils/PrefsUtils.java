package com.goldsprite.appdevframework.apputils;
import android.content.*;
import com.goldsprite.appdevframework.*;

public class PrefsUtils
{
	public static String DefaultPrefsKey(){
		return Project.ProjName();
	}
	
	public static SharedPreferences getPrefs(String prefsKey){
		SharedPreferences prefs = AppUtils.ctx.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
		return prefs;
	}
	public static SharedPreferences getPrefs(){return getPrefs(DefaultPrefsKey());}
	
	public static SharedPreferences.Editor getPrefsEditor(String prefsKey){
		SharedPreferences.Editor editor = getPrefs(prefsKey).edit();
		return editor;
	}
	public static SharedPreferences.Editor getPrefsEditor(){return getPrefsEditor(DefaultPrefsKey());}
}
