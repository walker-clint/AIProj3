package model;

import java.io.Serializable;


public class Move implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int r;
	int c;
	
	
	public Move() {
		r = 0;
		c= 0;
	}
	public Move(int _r, int _c) {
		r = _r;
		c = _c;
	}
	public int getR() {
		return r;
	}
	public void setX(int r) {
		this.r = r;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return "Move [row=" + r + ", column=" + c + "]";
	}
}
