package com.goldsprite.appdevframework.utils;
import java.time.*;
import java.time.format.*;

public class StringUtils {
	public static String getFormatTimeStamp(){
		return getFormatTimeStamp("yyyy-MM-dd HH:mm:ss:SSS");
	}
	public static String getFormatTimeStamp(String format){
		
		// 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式，左右加上方括号
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        // 格式化时间
        String formattedTime = String.format("%s", now.format(formatter));
		return formattedTime;
	}
	
	public static <E extends Enum<E>> String getEnumFullName(E enumValue) {
        if (enumValue == null) {
            return null;
        }
        // 获取 enum 类的简单名称
        String enumName = enumValue.getDeclaringClass().getSimpleName();
        Class declaringClass = enumValue.getDeclaringClass().getDeclaringClass();
		String declaringClassName = declaringClass==null ?"" :declaringClass.getSimpleName()+"_";
        // 获取 enum 的名称
        String enumConstantName = enumValue.name();

        // 根据需求组合字符串, 这里使用了 '_' 下划线来连接
        return declaringClassName/* + enumName + "_"*/ + enumConstantName;
    }
	
	public static int natureOrderCompare(String str1, String str2){
		String[] parts1 = splitByNumberAndText(str1);
		String[] parts2 = splitByNumberAndText(str2);

		int minLength = Math.min(parts1.length, parts2.length);
		for (int i = 0; i < minLength; i++){
			String part1 = parts1[i];
			String part2 = parts2[i];

			//检测两者皆为数字
			if (isNumeric(part1) && isNumeric(part2)){
				int maxLen = Math.max(part1.length(), part2.length());
				//将短字符串长度补齐
				part1 = padLeftZeros(part1, maxLen);
				part2 = padLeftZeros(part2, maxLen);
			}

			// 最后使用默认排序
			int cmp = part1.compareTo(part2);
			//顺序改变则返回
			if (cmp != 0) return cmp;
		}

		//如果前面顺序都相同则匹配两者长度，以更短的在前
		return Integer.compare(parts1.length, parts2.length);

	}

	public static String padLeftZeros(String numberStr, int digits){
		String ret = String.format("%" + digits + "s", numberStr);
		ret = ret.replace(" ", "0");
		return ret;
	}

	public static String[] splitByNumberAndText(String str){
		//非宽断言正则, 用于切割数字与非数字的边界：向前查找非数字向后查找数字 或 前面为数字后面为非数字
		String pattern = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
		return str.split(pattern);
	}

	public static boolean isNumeric(String str){
		String pattern = "\\d+";
		return str.matches(pattern);
	}
	
}
