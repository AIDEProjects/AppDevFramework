package com.goldsprite.appdevframework.math;

public class Vector2 {
	public float x, y;
	public float getX() { return x; }
	public void setX(float x) { this.x = x; }
	public float getY() { return y; }
	public void setY(float y) { this.y = y; }


	public Vector2(){ this(0, 0); }
	public Vector2(Vector2 vec){ this(vec.getX(), vec.getY()); }
	public Vector2(Vector2Int vec){ this(vec.getX(), vec.getY()); }
	public Vector2(float x, float y) { setX(x); setY(y); }


	public Vector2 add(Vector2 vec) {
		setX(getX() + vec.getX());
		setY(getY() + vec.getY());
		return this;
	}

	public Vector2 subtract(Vector2 vec) {
		setX(getX() - vec.getX());
		setY(getY() - vec.getY());
		return this;
	}

	public Vector2 multiply(Vector2 vec) {
		setX(getX() * vec.getX());
		setY(getY() * vec.getY());
		return this;
	}

	public Vector2 divideBy(Vector2 vec) {
		setX(getX() / vec.getX());
		setY(getY() / vec.getY());
		return this;
	}

	public Vector2 add(float num) {
		setX(getX() + num);
		setY(getY() + num);
		return this;
	}

	public Vector2 subtract(float num) {
		setX(getX() - num);
		setY(getY() - num);
		return this;
	}

	public Vector2 multiply(float num) {
		setX(getX() * num);
		setY(getY() * num);
		return this;
	}

	public Vector2 divideBy(float num) {
		setX(getX() / num);
		setY(getY() / num);
		return this;
	}


	public void set(float x, float y){
		setX(x);
		setY(y);
	}
	public void set(Vector2 vec){
		set(vec.x, vec.y);
	}


	public void moveTo(Vector2 destination, float vel){
		Vector2 dir = destination.clone().subtract(this).normalize();
		this.add(dir.multiply(vel));
	}

	public Vector2 normalize() {
		float mag = magnitude();
		this.divideBy(mag);
		return this;
	}

	public float magnitude() {
		return (float) Math.sqrt(getX() * getX() + getY() * getY());
	}

	public static float dotProduct(Vector2 a, Vector2 b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}

	public static Vector2 moveTo(Vector2 from, Vector2 to, float vel){
		Vector2 dir = to.clone().subtract(from).normalize();
		return dir.multiply(vel);
	}

	public static Vector2 lerp(Vector2 start, Vector2 end, float interpulation) {
		return start.clone().multiply(1 - interpulation).add(end.clone().multiply(interpulation));
	}


	public boolean equals(Vector2 vec) {
		return vec.getX() == this.getX() && vec.getY() == this.getY();
	}

	public boolean isZero(){
		if (getX() != 0 || getY() != 0){
			return false;
		}
		return getX() * getY() < Math.pow(10, -6);
	}

	public Vector2 clone(){
		return new Vector2(getX(), getY());
	}

	public static Vector2 zero(){
		return new Vector2();
	}

	@Override
	public String toString() {
		return String.format("{%.1f, %.1f}", getX(), getY());
	}
	
	public Vector2Int toVector2Int(){
		return new Vector2Int(this);
	}


	public static Vector2 up = new Vector2(0, 1);
	public static Vector2 left = new Vector2(-1, 0);
	public static Vector2 down = new Vector2(0, -1);
	public static Vector2 right = new Vector2(1, 0);
	public static Vector2 one = new Vector2(1, 1);
	public static Vector2 zero = new Vector2(0, 0);
}
