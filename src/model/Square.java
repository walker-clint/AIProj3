package model;

/*
 *	Written by Clinton Walker 2014
 *
 *  Container cLass for holding the QMap information
 * */

import java.io.Serializable;

public class Square implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//==============================================================================
	// variables
	
	private 	double			reward,
								weight,
								scaleFactorX,
								scaleFactorY,
								direction,
								rotation;
	private		boolean			isGoal,
								isWall;
	private 	String			color;
	
	//==============================================================================
	// constructors
	
	public Square(){
		reward = 0;
		weight = 0;
		scaleFactorX = 1;
		scaleFactorY = 1;
		direction = 0;
		isGoal = false;
		isWall = false;
		color = "White";
		rotation = 0.0;
	}
	
	//==============================================================================
	// getters and setters
	
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getScaleFactorX() {
		return scaleFactorX;
	}
	public void setScaleFactorX(double scaleFactorX) {
		if(scaleFactorX > .1){
			this.scaleFactorX = .1;
		} else {
			this.scaleFactorX = scaleFactorX;
		}
	}
	public double getScaleFactorY() {
		return scaleFactorY;
	}
	public void setScaleFactorY(double scaleFactorY) {
		if(scaleFactorY > .1){
			this.scaleFactorY = .1;
		} else {
			this.scaleFactorY = scaleFactorY;
		}
	}
	public double getDirection() {
		return direction;
	}
	public void setDirection(double direction) {
		this.direction = direction;
	}
	public boolean isGoal() {
		return isGoal;
	}
	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public double getRotation() {
		return rotation;
	}
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	public boolean isWall() {
		return isWall;
	}
	public void setWall(boolean isWall) {
		this.isWall = isWall;
	}
	
	
	//==============================================================================
	// methods

	@Override
	public String toString() {
		return "Square [reward=" + reward + ", weight=" + weight
				+ ", scaleFactorX=" + scaleFactorX + ", scaleFactorY=" + scaleFactorY + ", direction=" + direction
				+ ", isGoal=" + isGoal + ", isWall=" + isWall + ", color=" + color + "]";
	}
	
					
	
	

}
