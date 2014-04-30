package model;

/*
 *	Written by Clinton Walker 2014
 *
 *  Container cLass for holding the QMap information
 * */

import java.io.Serializable;

import javafx.scene.paint.Color;
import application.Main;


public class Square implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//==============================================================================
	// variables
	private 	int				r, c, moveR, moveC;
	private 	double			reward,
								weight,
								scaleFactor,
								direction,
								rotation,
								magnitude,
								left, right, up, down;
	
	private		boolean			isGoal,
								isWall,
								leftValid, rightValid, upValid, downValid, noValidMoves, moveIsGoal;
	private 	String			color;
	
	//==============================================================================
	// constructors
	
	public Square(){
		r = 0;
		c = 0;
		reward = 0;
		weight = 0;
		scaleFactor = 1;
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
		scaleFactor = .001;
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
	
	public double getScaleFactor() {
		return scaleFactor;
	}
	public void setScaleFactor(double scaleFactor) {
		if(scaleFactor < .03){
			if (scaleFactor > .0000000001 && scaleFactor < .0000001){
				Main.GBC.arrows[r][c].setFill(Color.RED);
				this.scaleFactor = .07;
			} else if (scaleFactor > .0000001 && scaleFactor < .000005){
				Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
				this.scaleFactor = .08;
			} else if (scaleFactor > .000005 && scaleFactor < .00001){
				Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
				this.scaleFactor = .09;
			} else if (scaleFactor > .00001 && scaleFactor < .00012){
				Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
				this.scaleFactor = .1;
			} else if (scaleFactor > .00012 && scaleFactor < .0016){
				Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
				this.scaleFactor = .12;
			} else if (scaleFactor > .0016 && scaleFactor < .03){
				Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
				this.scaleFactor = .14;
			} else {
				this.scaleFactor = .06;
				Main.GBC.arrows[r][c].setFill(Color.BLACK);
			}
		} else if(scaleFactor > .2){ 
			this.scaleFactor = .2;
			Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
		} else if(scaleFactor > .03 && scaleFactor <= .07){
			this.scaleFactor = .15;
			Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
		} else if(scaleFactor > .07 && scaleFactor <= .09){
			this.scaleFactor = .16;
			Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
		} else if(scaleFactor > .09 && scaleFactor <= .11){
			this.scaleFactor = .17;
			Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
		} else if(scaleFactor > .11 && scaleFactor <= .2){
			this.scaleFactor = .19;
			Main.GBC.arrows[r][c].setFill(Color.CORNFLOWERBLUE);
		} else {
			this.scaleFactor = scaleFactor;
			Main.GBC.arrows[r][c].setFill(Color.BLACK);
		}
	}
	public double getDirection() {
		return direction;
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
	public int getMoveR() {
		return moveR;
	}
	public void setMoveR(int moveR) {
		this.moveR = moveR;
	}
	public int getMoveC() {
		return moveC;
	}
	public void setMoveC(int moveC) {
		this.moveC = moveC;
	}
	public boolean isMoveIsGoal() {
		return moveIsGoal;
	}
	public void setMoveIsGoal(boolean moveIsGoal) {
		this.moveIsGoal = moveIsGoal;
	}
	
	//==============================================================================
	// methods

	public void updateReward(int lastR, int lastC){
		double rewardFromLastPosition = Main.MACHINE.qtable[lastR][lastC].getReward();
		double stepone = (rewardFromLastPosition - reward);
		System.out.print("\nReward update: [" + r + "][" + c + "] is " + reward + " + (" + Main.GBC.getRewardDecay() +  " * " + rewardFromLastPosition + " - " + reward + " ) = "  );
		reward = reward + (Main.GBC.getRewardDecay() * ( rewardFromLastPosition - reward));
		System.out.print( reward + "\n" );
		
	}
	
	public void updateWeight(int nextR, int nextC){
		System.out.println("update weight in location [" + r +  "][" + c + "] current Weight= " + weight);
		double alpha = Main.GBC.getAlpha();
		double gamma = Main.GBC.getGamma();
		double oldWeight = weight;
		double stepOne = (gamma * Main.MACHINE.qtable[nextR][nextC].getWeight() );
		System.out.println("stepone value: " + stepOne);
		double stepTwo =  stepOne - weight;
		System.out.println("steptwo value: " + stepTwo);
		double stepThree = reward + stepTwo;
		double stepFour = alpha * stepThree;
		double stepFive = weight + stepFour;
		weight = stepFive;
		System.out.println(weight + " = (" + oldWeight + " + " + alpha + "( " + reward + " + " + gamma + "*" + Main.MACHINE.qtable[nextR][nextC].getWeight() + " - " + weight + ")"  );
		System.out.println(weight + " = (" + oldWeight + " + " + alpha + "( " + reward + " + " + stepOne + " - " + weight + ")"  );
		System.out.println(weight + " = (" + oldWeight + " + " + alpha + "( " + reward + " + " + stepTwo + ")"  );
		System.out.println(weight + " = (" + oldWeight + " + " + alpha + "( " + stepThree + ")"  );
		System.out.println(weight + " = (" + oldWeight + " + " + stepFour + ")"  );
		System.out.println("new Weight value: " + weight);
		if (weight < 0){
			double stopme = 1 + weight;
			stopme++;
		}
	}
	
	public void updateSquareDisplay(){

		double leftX = getLeft(r, c) * -1;
		double rightX = getRight(r, c);
		double rayX = (leftX + rightX);
		double upY = getUp(r, c);
		double downY = getDown(r, c) * -1;
		double rayY = (upY + downY);
		
		magnitude = Math.sqrt((rayX * rayX) + (rayY * rayY));
		magnitude *= 10;
		if (Main.GBC.isPropogationReward()){
			setScaleFactor(reward);
		} else {
			setScaleFactor(weight);
		}
		
		setRotation(Math.atan2(rayX, rayY));
		System.out.println ("RayX: " + rayX + " rayY: " + rayY + " rotation radians: " + rotation);
		rotation = rotation * (180.0 / Math.PI);
		setDirection();
		if (Main.GBC.isArrowTypeDirection()){
			Main.GBC.arrows[r][c].setRotate(direction);
		} else {
			Main.GBC.arrows[r][c].setRotate(rotation);
		}
		System.out.println("Update Display: [" + r + "][" + c + "] dir:" + direction + " mag: " + magnitude + "  Rotation: " + rotation);
	}
	
	public void setDirection(){
		double lV = 0, rV = 0, uV = 0, dV = 0, val, dir;
		if(leftValid){
			lV = Main.MACHINE.qtable[r-1][c].getWeight();
			if(Main.MACHINE.qtable[r-1][c].isGoal()){
				lV = 1000000;
				moveIsGoal = true;
				setRotation(270);
			}
		}
		if(rightValid){
			rV = Main.MACHINE.qtable[r+1][c].getWeight();
			if(Main.MACHINE.qtable[r+1][c].isGoal()){
				rV = 1000000;
				moveIsGoal = true;
				setRotation(90);
			}
		}
		if(upValid){
			uV = Main.MACHINE.qtable[r][c-1].getWeight();
			if(Main.MACHINE.qtable[r][c-1].isGoal()){
				uV = 1000000;
				moveIsGoal = true;
				setRotation(0);
			}
		}
		if(downValid){
			dV = Main.MACHINE.qtable[r][c+1].getWeight();
			if(Main.MACHINE.qtable[r][c+1].isGoal()){
				dV = 1000000;
				moveIsGoal = true;
				setRotation(180);
			}
		}
		if (rV > lV){
			val = rV;
			dir = 90;
		} else {
			val = lV;
			dir = 270;
		}
		
		if(uV > val){
			val = uV;
			dir = 0;
		}
		
		if(dV > val){
			val = dV;
			dir = 180;
		}
		direction = dir;
		if(dir == 270){ //move left
			moveR = r-1;
			moveC = c;
		}
		if(dir == 90){ //move right
			moveR = r+1;
			moveC = c;
		}
		if(dir == 0){ //move up
			moveR = r;
			moveC = c-1;
		}
		if(dir == 180){ //move down
			moveR = r;
			moveC = c+1;
		}
	}
	
	public void setMoves(){
		if(r-1 >= 0 && !Main.MACHINE.qtable[r-1][c].isWall()){
			leftValid = true;
		} else {
			leftValid = false;
		}
		if(r+1 < Main.ROWS && !Main.MACHINE.qtable[r+1][c].isWall()){
			rightValid = true;
		} else {
			rightValid = false;
		}
		if(c-1 >= 0 && !Main.MACHINE.qtable[r][c-1].isWall()){
			upValid = true;
		} else {
			upValid = false;
		}
		if(c+1 < Main.COLUMNS && !Main.MACHINE.qtable[r][c+1].isWall()){
			downValid = true;
		} else {
			downValid = false;
		}
		if (!leftValid && !rightValid && !upValid && !downValid){
			noValidMoves = true;
		} else {
			noValidMoves = false;
		}
	}
	
	@Override
	public String toString() {
		return "Square [reward=" + reward + ", weight=" + weight
				+ ", scaleFactor=" + scaleFactor + ", direction=" + direction
				+ ", isGoal=" + isGoal + ", isWall=" + isWall + ", color=" + color + "]";
	}
	
					
	public double getLeft(int r, int c) {
		if ((r - 1) >= 0){ 
			if (!Main.MACHINE.qtable[r - 1][c].isWall()) {
				return Main.MACHINE.qtable[r - 1][c].getWeight();
			}
		}
		return 0.0;
	}

	public double getRight(int r, int c) {
		if ((r + 1) < Main.ROWS){
			if (!Main.MACHINE.qtable[r + 1][c].isWall()) {
				return Main.MACHINE.qtable[r + 1][c].getWeight();
			}
		}
		return 0.0;
	}

	public double getUp(int r, int c) {
		if ((c - 1) >= 0){
			if (!Main.MACHINE.qtable[r][c - 1].isWall()) {
				return Main.MACHINE.qtable[r][c - 1].getWeight();
			}
		}
		return 0.0;
	}

	public double getDown(int r, int c) {
		if ((c + 1) < Main.COLUMNS){
			if(!Main.MACHINE.qtable[r][c + 1].isWall()) {
				return Main.MACHINE.qtable[r][c + 1].getWeight();
			}
		}
		return 0.0;
	}
	

}
