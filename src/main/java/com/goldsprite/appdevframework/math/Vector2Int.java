package com.goldsprite.appdevframework.math;

public class Vector2Int extends Vector2 {
	public int x, y;
	public float getX() { return x; }
	public void setX(float x) { this.x = (int)x; }
	public float getY() { return y; }
	public void setY(float y) { this.y = (int)y; }


	public Vector2Int(){ this(0, 0); }
	public Vector2Int(Vector2 vec){ this(vec.getX(), vec.getY()); }
	public Vector2Int(Vector2Int vec){ this(vec.getX(), vec.getY()); }
	public Vector2Int(float x, float y) { setX(x); setY(y); }
	public Vector2Int(int x, int y) { setX(x); setY(y); }


	@Override
	public String toString(){
		return String.format("{%d, %d}", x, y);
	}

	public Vector2 toVector2(){
		return new Vector2(this);
	}
}
