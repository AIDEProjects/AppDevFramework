package com.goldsprite.appdevframework.math;

public class Angle
{
	public static float angleDifference(float angle1, float angle2) {
		// 将角度转换为 0 到 360 的范围
		angle1 = angle1 % 360;
		angle2 = angle2 % 360;

		// 计算角度差
		float difference = Math.abs(angle1 - angle2);

		// 确保差值在 0 到 180 度之间
		if (difference > 180) {
			difference = 360 - difference;
		}

		return difference;
	}
	
}
