package com.goldsprite.appdevframework.math;

public class MathUtils
{
	//获取保留digit位有效精度的四舍五入数
	public static float preciNum(float value) { return (float)preciNum(value+0d); }
	public static double preciNum(double value) { return preciNum(value, 1); }
	public static float preciNum(float value, int digit) { return (float)preciNum(value+0d, digit); }
	public static double preciNum(double value, int digit) {
		int firstNonZeroIndex = findFirstNonZeroIndex(value);
		int precisionToKeep = firstNonZeroIndex + digit;
		double roundedValue = roundToPrecision(value, precisionToKeep);
		return roundedValue;
	}

    // 找到小数点后第一个非零位的位置
    public static int findFirstNonZeroIndex(double value) {
		String valueStr = Double.toString(value);
        int start = valueStr.indexOf('.') + 1;
		int index = start; // 跳过小数点
        while (index < valueStr.length() && valueStr.charAt(index) == '0') {
            index++;
        }
        return index - start; // 返回从小数点后开始的位置
    }

    // 四舍五入到指定小数位
    public static double roundToPrecision(double value, int precision) {
        double factor = Math.pow(10, precision); // 10 的倍数
        return Math.round(value * factor) / factor;
    }
}
