package model;

/*
 *	Written by Clinton Walker 2014
 *
 *  Container cLass for holding the QMap information
 * */

import java.io.Serializable;

import application.Main;


public class Square implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//==============================================================================
	// variables
	private 	int				r, c;
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
		r = 0;
		c = 0;
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
	
	public Square(int _r, int _c){
		r = _r;
		c = _c;
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
		if(scaleFactorX < .01){
			this.scaleFactorX = .01;
		} else if(scaleFactorX > .1){ 
			this.scaleFactorX = .1;
		} else {
			this.scaleFactorX = scaleFactorX;
		}
	}
	public double getScaleFactorY() {
		return scaleFactorY;
	}
	public void setScaleFactorY(double scaleFactorY) {
		if(scaleFactorY < .01){
			this.scaleFactorY = .01;
		} else if(scaleFactorY > .1){ 
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

	public void updateReward(int lastR, int lastC){
		double rewardFromLastPosition = Main.MACHINE.qtable[lastR][lastC].getReward();
		reward = rewardFromLastPosition + (0.5 * ( rewardFromLastPosition - reward));
	}
	
	public void updateWeight(int nextR, int nextC){
		System.out.println("update weight in location [" + r +  "][" + c + "]");
		double alpha = Main.GBC.getAlpha();
		double gamma = Main.GBC.getGamma();
		double stepOne = Main.MACHINE.qtable[nextR][nextC].getWeight() - weight;
		System.out.println("stepone value: " + stepOne);
		double stepTwo = gamma * stepOne;
		System.out.println("steptwo value: " + stepTwo);
		double stepThree = reward + stepTwo;
		double stepFour = alpha * stepThree;
		double stepFive = reward + stepFour;
		reward = stepFive;
		
	}
	
	public void updateSquareDisplay(){

		double leftX = Main.MACHINE.getLeft(r, c) * -1;
		double rightX = Main.MACHINE.getRight(r, c);
		double rayX = Math.abs(leftX + rightX);
		double upY = Main.MACHINE.getUp(r, c);
		double downY = Main.MACHINE.getDown(r, c) * -1;
		double rayY = Math.abs(upY + downY);
		
		double magnitude = Math.sqrt((rayX * rayX) + (rayY * rayY));
		magnitude *= 10;
		setScaleFactorX(magnitude * Main.SCALE_ADJUSTMENT_FACTOR);
		setScaleFactorY(magnitude * Main.SCALE_ADJUSTMENT_FACTOR);
		setRotation(Math.atan2(rayY, rayX));
		System.out.println("Update Display: [" + r + "][" + c + "] dir:" + direction + " mag: " + magnitude + "  Rotation: " + rotation);
	}
	
	@Override
	public String toString() {
		return "Square [reward=" + reward + ", weight=" + weight
				+ ", scaleFactorX=" + scaleFactorX + ", scaleFactorY=" + scaleFactorY + ", direction=" + direction
				+ ", isGoal=" + isGoal + ", isWall=" + isWall + ", color=" + color + "]";
	}
	
					
	
	

}
