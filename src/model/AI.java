package model;

import java.awt.Checkbox;
import java.util.Random;

import application.Main;

/*
 * 	Written by Clinton Walker 2014
 * */

public class AI {
	//variables
	public 			Square[][] 		qtable;				// container for learning table
	private			int				goalR,				// X position of current goal
									goalC,				// Y position of current goal
									nextR,
									nextC;
	private   		double			alpha,				// value to effect speed of learning in the system
									gamma,				// value to emphasize / deemphasize the next moves value
									lambda,  			// random move chance
									lambdaDecay, 		// rate of decay of chance of a random move
									seedReward,			// initial seed value for all non-goal squares
									goalReward,			// goal value to propagate back though current path
									seedMin,			// initial seed for the Min value of the qtable weight
									seedMax,			// initial seed for the Max value of the qtable weight
									rewardDecay;
	private   		boolean			moveLeft,
									moveRight,
									moveUp,
									moveDown,
									foundGoal,
									random;

	
	//==============================================================================
	//constructors
	public AI() {
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}
	
	public AI(int _goalR, int _goalC, int _startR, int _startC, double _seedMin, double _seedMax, double _alpha,
			double _gamma, double _lambda, double _lambdaDecay, double _seedReward, double _goalReward, double _rewardDecay) {
		goalR = _goalR;
		goalC = _goalC;
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
		moveLeft = false;
		moveRight = false;
		moveUp = false;
		moveDown = false;
		foundGoal = false;
		random = false;
		nextR = _startR;
		nextC = _startC;
		
	}
	
	//==============================================================================
	//methods
	
	//  http://stackoverflow.com/questions/3680637/how-to-generate-a-random-double-in-a-given-range
	public void seedQTable() {
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				double rand = new Random().nextDouble();
				double result = seedMin + (rand * (seedMax - seedMin));
				qtable[r][c] = new Square();
				qtable[r][c].setWeight(result); 
				System.out.println("double random value: " + rand + "  random number: " + result + " seeded value = " + qtable[r][c].getWeight());
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
	
	private void checkRandom(){
		double r = new Random().nextDouble();
		double result = lambda + (r * (100 - lambda));
		if(result > lambda){
			random = false;
		}
		random = true;
		System.out.println("Check random move in model.AI: " + result + " Lambda= " + lambda + "Random? " + random);
		
	}
	
	public void makeMove(int r, int c){
		System.out.println("RC [" + r + "][" + c + "]");
		//checkRandom();
		
		setDirection(r, c, random);
		if (moveLeft){
			nextR -= 1;
			if (nextR < 0){
				//error
				System.out.println("nextR " + nextR + " in AI is less than: 0 ");
				Main.GBC.errorStop();
			}
		}
		if (moveRight){
			nextR += 1;
			if(nextR >= Main.ROWS){
				//error
				System.out.println("nextR " + nextR + " in AI is greater than: Main.Rows " + Main.ROWS);
				Main.GBC.errorStop();
			}
		}
		if(moveUp){
			nextC -= 1;
			if(nextC < 0){
				System.out.println("nextC " + nextC + " in AI is less than: 0 ");
				Main.GBC.errorStop();
			}
		}
		if(moveDown){
			nextC += 1;
			if(nextC >= Main.COLUMNS){
				//error
				System.out.println("nextC " + nextC + " in AI is greater than: Main.Rows " + Main.COLUMNS);
				Main.GBC.errorStop();
			}
		}
	}
	

	public void setColor(int r, int c, String color) {
		qtable[r][c].setColor(color);
	}
	
	private double updateQTable(int r, int c, double nextWeight) {
		// Q(s,a) = Q(s,a) + alpha(reward + (gamma * Q(s',a')) - Q(s,a)))
		double result = qtable[r][c].getWeight() + (alpha * (qtable[r][c].getReward() + ((gamma * nextWeight) - qtable[r][c].getWeight())));
		
		return result;
	}
	
	public void decayLambda() {
		lambda -= lambdaDecay;
	}
	
	public double decayReward(double value) {
		return value *= rewardDecay;
	}
	
	private void setDirection(int r, int c, boolean random){
		double left, right, up, down, leftX = 0, rightX, upY, downY, rayX, rayY, magnitude;
		if (random){
			Random rNum = new Random();
			boolean selectingRandomMove = true, validMove = false;
			while (selectingRandomMove){
				int move = rNum.nextInt(4);
				System.out.println("Random move is true: move is: " + move + " 0 = left: 1 = right: 2 = up: 3 = down");
				if(r-1 < 0){
					validMove = false;
					System.out.println("r = " + r + " r-1 = " + (r-1) + "Validmove = " + validMove);
				} else if((r+1) >= Main.ROWS){
					System.out.println("r = " + r + " r+1 = " + (r+1) + "Validmove = " + validMove);
					validMove = false;
				} else if((c-1) < 0){
					System.out.println("c = " + c + " c-1 = " + (c-1) + "Validmove = " + validMove);
					validMove = false;
				} else if((c+1) >= Main.COLUMNS){
					System.out.println("c = " + c + " c+1 = " + (r+1) + "Validmove = " + validMove);
					validMove = false;
				} else {
					System.out.println("Validmove = " + validMove);
					validMove = true;
				}
				if (validMove){
					moveLeft = false;
					moveRight = false;
					moveUp = false;
					moveDown = false;
					switch (move) {
					case 0:
						selectingRandomMove = false;
						moveLeft = true;
						nextR = (r-1);
						nextC = c;
						break;
					case 1:
						selectingRandomMove = false;
						moveRight = true;
						nextR = (r+1);
						nextC = c;
						break;
					case 2:
						selectingRandomMove = false;
						moveUp = true;
						nextR = r;
						nextC = (c-1);
						break;
					case 3:
						selectingRandomMove = false;
						moveDown = true;
						nextR = r;
						nextC = (c+1);
						break;
					default:
						System.out.println("Error random move switch entering default case :: move: " + move);
						break;
					}
				}
			}
		} else {
			double dir = 270;
			double qVal;
			left = getLeft(r, c);
			left = updateQTable(r, c, left);
			System.out.println("qtable[" + r + "][" + c + "] left weight = " + qtable[r][c].getWeight());
			qVal = left;
			moveLeft = true;
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
				moveRight = true;
				moveLeft = false;
				qVal = right;
			} 
			if (up > qVal){
				dir = 0;
				moveUp = true;
				moveRight = false;
				moveLeft = false;
				qVal = up;
			}
			if (down > qVal){
				dir = 180;
				qVal = down;
				moveDown = true;
				moveUp = false;
				moveLeft = false;
				moveRight = false;
			}
			
			if(moveLeft){
				
			}
			if(moveRight){
				
			}
			if(moveUp){
				
			}
			if(moveDown){
				
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
	
	public int getGoalR() {
		return goalR;
	}

	public void setGoalR(int goalR) {
		this.goalR = goalR;
	}

	public int getGoalC() {
		return goalC;
	}

	public void setGoalC(int goalC) {
		this.goalC = goalC;
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

	public int getNextR() {
		return nextR;
	}

	public void setNextR(int nextR) {
		this.nextR = nextR;
	}

	public int getNextC() {
		return nextC;
	}

	public void setNextC(int nextC) {
		this.nextC = nextC;
	}

	public boolean isFoundGoal() {
		return foundGoal;
	}

	public void setFoundGoal(boolean foundGoal) {
		this.foundGoal = foundGoal;
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
