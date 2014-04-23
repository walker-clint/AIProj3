package model;

import java.util.Random;
import application.Main;

/*
 * 	Written by Clinton Walker 2014
 * */

public class AI {
	//variables
	public 			Square[][] 		qtable;				// container for learning table
	private			int				goalX,				// X position of current goal
									goalY;				// Y position of current goal
	private   		double			alpha,				// value to effect speed of learning in the system
									gamma,				// value to emphasize / deemphasize the next moves value
									lambda,  			// random move chance
									lambdaDecay, 		// rate of decay of chance of a random move
									seedReward,			// initial seed value for all non-goal squares
									goalReward,			// goal value to propagate back though current path
									seedMin,			// initial seed for the Min value of the qtable weight
									seedMax,			// initial seed for the Max value of the qtable weight
									rewardDecay;

	
	//==============================================================================
	//constructors
	public AI() {
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}
	
	public AI(int _goalX, int _goalY, double _seedMin, double _seedMax, double _alpha,
			double _gamma, double _lambda, double _lambdaDecay, double _seedReward, double _goalReward, double _rewardDecay) {
		goalX = _goalX;
		goalY = _goalY;
		seedMin = _seedMin;
		seedMax = _seedMax;
		alpha = _alpha;
		gamma = _gamma;
		lambda = _lambda;
		lambdaDecay = _lambdaDecay;
		seedReward = _seedReward;
		goalReward = _goalReward;
		rewardDecay = _rewardDecay;
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}
	
	//==============================================================================
	//methods
	
	//  http://stackoverflow.com/questions/3680637/how-to-generate-a-random-double-in-a-given-range
	public void seedQTable() {
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				double random = new Random().nextDouble();
				double result = seedMin + (random * (seedMax - seedMin));
				qtable[r][c] = new Square();
				qtable[r][c].setWeight(result); 
				System.out.println("double random value: " + random + "  random: " + result + " seeded value = " + qtable[r][c].getWeight());
				qtable[r][c].setReward(seedReward);
				System.out.println("QTable[" + r + "][" + c + "] toString: " + qtable[r][c].toString());
			}
		}
	}
	
	public void initQTableAndBoard(){
		for (int r = 0; r < Main.ROWS; r++ ){
			for (int c = 0; c < Main.COLUMNS; c++){
				if (qtable[r][c].isGoal()){
					qtable[r][c].setWeight(0);
					qtable[r][c].setColor("CORNFLOWERBLUE");
					qtable[r][c].setWeight(10);
					qtable[r][c].setReward(Main.GBC.getReward());
				}else if (qtable[r][c].isWall()){
					qtable[r][c].setWeight(0);
					qtable[r][c].setColor("DARKGREY");
					qtable[r][c].setWeight(0);
				} else {
					qtable[r][c].setColor("LIGHTGRAY");
				}
			}
		}
		for (int r = 0; r < Main.ROWS; r++ ){
			for (int c = 0; c < Main.COLUMNS; c++){
				if (!qtable[r][c].isGoal() && !qtable[r][c].isWall()){
					setDirection(r, c, false);
				}
			}
		}
	}
	
	public void makeMove(int r, int c){
		
	}
	

	public void setColor(int r, int c, String color) {
		qtable[r][c].setColor(color);
	}
	
	private double updateQTable(int r, int c, double nextWeight) {
		// Q(s,a) = Q(s,a) + alpha(reward + (gamma * Q(s',a')) - Q(s,a)))
		double result =
				qtable[r][c].getWeight() + (alpha * (qtable[r][c].getReward() + ((gamma * nextWeight) - qtable[r][c].getWeight())));
		
		return result;
	}
	
	public void decayLambda() {
		lambda -= lambdaDecay;
	}
	
	public void decay() {
		
	}
	
	private void setDirection(int r, int c, boolean random){
		double left, right, up, down, leftX = 0, rightX, upY, downY, rayX, rayY, magnitude;
		if (random){
			
		} else {
			double dir = 270;
			double qVal;
			left = getLeft(r, c);
			left = updateQTable(r, c, left);
			System.out.println("qtable[" + r + "][" + c + "] left weight = " + qtable[r][c].getWeight());
			qVal = left;
			right = getRight(r, c);
			right = updateQTable(r, c, right);
			System.out.println("qtable[" + r + "][" + c + "] right weight = " + qtable[r][c].getWeight());
			up = getUp(r, c);
			up = updateQTable(r, c, up);
			System.out.println("qtable[" + r + "][" + c + "] up weight = " + qtable[r][c].getWeight());
			down = getDown(r, c);
			down = updateQTable(r, c, down);
			System.out.println("qtable[" + r + "][" + c + "] down weight = " + qtable[r][c].getWeight());
			if (right > qVal){
				dir = 90;
				qVal = right;
			} 
			if (up > qVal){
				dir = 0;
				qVal = up;
			}
			if (down > qVal){
				dir = 180;
				qVal = down;
			}
			qtable[r][c].setDirection(dir);
			qtable[r][c].setWeight(qVal);
			
			leftX = left * -1;
			rightX = right;
			rayX = Math.abs(leftX + rightX);
			upY = up;
			downY = down * -1;
			rayY = Math.abs(upY + downY);
			
			magnitude = Math.sqrt((rayX * rayX)+ (rayY * rayY)); 
			magnitude *= 10;
			qtable[r][c].setScaleFactorX(magnitude);
			qtable[r][c].setScaleFactorY(magnitude);
			
			qtable[r][c].setRotation(Math.atan2(rayY, rayX));
	
		}
	}
	
	private double getLeft(int r, int c){
		if (r > 0){
			return qtable[r-1][c].getWeight();
		}
		return 0.0;
	}
	
	private double getRight(int r, int c) {
		if (r < Main.ROWS-1){
			return qtable[r+1][c].getWeight();
		}
		return 0.0;
	}
	
	private double getUp(int r, int c) {
		if (c < 0){
			return qtable[r][c-1].getWeight();
		}
		return 0.0;
	}
	
	private double getDown(int r, int c) {
		if ( c < Main.COLUMNS-1){
			return qtable[r][c+1].getWeight();
		}
		return 0.0;
	}

	
	
	//==============================================================================
	// getters and setters
	
	public int getGoalX() {
		return goalX;
	}

	public void setGoalX(int goalX) {
		this.goalX = goalX;
	}

	public int getGoalY() {
		return goalY;
	}

	public void setGoalY(int goalY) {
		this.goalY = goalY;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getLambdaDecay() {
		return lambdaDecay;
	}

	public void setLambdaDecay(double lambdaDecay) {
		this.lambdaDecay = lambdaDecay;
	}

	public double getSeedReward() {
		return seedReward;
	}

	public void setSeedReward(double seedReward) {
		this.seedReward = seedReward;
	}

	public double getGoalReward() {
		return goalReward;
	}

	public void setGoalReward(double goalReward) {
		this.goalReward = goalReward;
	}

	public double getSeedMin() {
		return seedMin;
	}

	public void setSeedMin(double seedMin) {
		this.seedMin = seedMin;
	}

	public double getSeedMax() {
		return seedMax;
	}

	public void setSeedMax(double seedMax) {
		this.seedMax = seedMax;
	}

	public double getRewardDecay() {
		return rewardDecay;
	}

	public void setRewardDecay(double rewardDecay) {
		this.rewardDecay = rewardDecay;
	}
	
	
}
