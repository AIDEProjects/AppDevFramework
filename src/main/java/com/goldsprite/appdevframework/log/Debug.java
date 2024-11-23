package com.goldsprite.appdevframework.log;
import java.util.*;

public class Debug
{
	public static List<String> debugInfos;
	
	
	public static void setDebugInfo(int line, String msg){
		if (line > 0 && line < debugInfos.size()) {
			debugInfos.set(line, msg);
		}
	}
}
