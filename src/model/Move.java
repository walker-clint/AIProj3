package model;

import java.io.Serializable;


public class Move implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int x;
	int y;
	
	public Move() {
		x = 0;
		y = 0;
	}
	public Move(int _x, int _y) {
		x = _x;
		y = _y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
