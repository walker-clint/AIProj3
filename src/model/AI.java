package model;

import java.awt.Checkbox;
import java.util.Random;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

import application.Main;

/*
 * 	Written by Clinton Walker 2014
 * */

public class AI {
	// variables
	public Square[][] qtable; // container for learning table
	private int goalR, // X position of current goal
			goalC, // Y position of current goal
			nextR, nextC;
	private double alpha, // value to effect speed of learning in the system
			gamma, // value to emphasize / deemphasize the next moves value
			lambda, // random move chance
			lambdaDecay, // rate of decay of chance of a random move
			seedReward, // initial seed value for all non-goal squares
			goalReward, // goal value to propagate back though current path
			seedMin, // initial seed for the Min value of the qtable weight
			seedMax, // initial seed for the Max value of the qtable weight
			rewardDecay;
	private boolean moveLeft, moveRight, moveUp, moveDown, foundGoal, random, leftValid, rightValid, upValid, downValid;

	// ==============================================================================
	// constructors
	public AI() {
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}

	public AI(int _goalR, int _goalC, int _startR, int _startC,
			double _seedMin, double _seedMax, double _alpha, double _gamma,
			double _lambda, double _lambdaDecay, double _seedReward,
			double _goalReward, double _rewardDecay) {
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

	// ==============================================================================
	// methods

	// http://stackoverflow.com/questions/3680637/how-to-generate-a-random-double-in-a-given-range
	public void seedQTable() {
		for (int c = 0; c < Main.COLUMNS; c++) {
			for (int r = 0; r < Main.ROWS; r++) {
				double rand = new Random().nextDouble();
				double result = seedMin + (rand * (seedMax - seedMin));
				qtable[r][c] = new Square(r,c);
				qtable[r][c].setWeight(result);
				//System.out.println("double random value: " + rand
				//		+ "  random number: " + result + " seeded value = "
				//		+ qtable[r][c].getWeight());
				qtable[r][c].setReward(seedReward);
				//System.out.println("QTable[" + r + "][" + c + "] toString: "
				//		+ qtable[r][c].toString());
			}
		}
	}

	public void initQTableAndBoard() {
		for (int r = 0; r < Main.ROWS; r++) {
			for (int c = 0; c < Main.COLUMNS; c++) {
				if (r == goalR && c == goalC) {
					System.out.println("Is Goal: [" + r + "][" + c + "]");
					qtable[r][c].setGoal(true);
					qtable[r][c].setWeight(0.1);
					qtable[r][c].setColor("CORNFLOWERBLUE");
					qtable[r][c].setReward(Main.GBC.getReward());
				} else if (qtable[r][c].isWall()) {
					System.out.println("Is Wall: [" + r + "][" + c + "]");
					qtable[r][c].setWeight(0);
					qtable[r][c].setColor("DARKGREY");
					qtable[r][c].setWeight(0);
				} else {
					qtable[r][c].setColor("LIGHTGRAY");
					qtable[r][c].setMoves();
					qtable[r][c].setDirection();
				}
				System.out.println("test");
			}
		}
	}

	private void checkRandom() {
		double result = new Random().nextInt(100);
		
		if (result > lambda) {
			random = false;
		} else {
			random = true;
		}
		//System.out.println("Check random move in model.AI: " + result
			//	+ " Lambda= " + lambda + "Random? " + random);

	}

	public void makeMove(int r, int c) {
		//System.out.println("RC [" + r + "][" + c + "]");
		// checkRandom();
		checkRandom();
		setDirection(r, c, random);
	}

	public void setColor(int r, int c, String color) {
		qtable[r][c].setColor(color);
	}

	public void decayLambda() {
		lambda -= lambdaDecay;
		if(lambda < 5){
			lambda = 5;
		}
	}

	public double decayReward(double value) {
		return value *= rewardDecay;
	}

	private void setDirection(int r, int c, boolean random) {
		double left, right, up, down;
		if (random) {
			boolean selectingRandomMove = true;
			while (selectingRandomMove) {
				Random rNum = new Random();
				int move = rNum.nextInt(4);
				//System.out.println("Random move is true: move is: " + move
				//		+ " 0 = left: 1 = right: 2 = up: 3 = down");
				switch (move) {
					case 0:
						if ((r - 1 < 0) || qtable[(r - 1)][c].isWall()) {
							//System.out.println("Left move is a Wall = ["
							//		+ (r - 1) + "][" + c + "]");
						} else {
							selectingRandomMove = false;
							moveLeft = true;
							nextR = (r - 1);
							nextC = c;
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							//System.out.println("Left move is Valid = [" + nextR
							//		+ "][" + nextC + "]  foundGoal: "
							//		+ foundGoal);
						}
						break;
					case 1:
						if ((r + 1) >= Main.ROWS || qtable[(r + 1)][c].isWall()) {
							//System.out.println("Right move is a Wall = ["
							//		+ (r + 1) + "][" + c + "]");
						} else {
							selectingRandomMove = false;
							moveRight = true;
							nextR = (r + 1);
							nextC = c;
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							//System.out.println("Right move is Valid = ["
							//		+ nextR + "][" + nextC + "]  foundGoal: "
							//		+ foundGoal);
						}
						break;
					case 2:
						if ((c - 1) < 0 || qtable[r][(c - 1)].isWall()) {
							//System.out.println("Up move is a Wall = [" + r
							//		+ "][" + (c - 1) + "]");
						} else {
							selectingRandomMove = false;
							moveUp = true;
							nextR = r;
							nextC = (c - 1);
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							//System.out.println("Up move is Valid = [" + nextR
							//		+ "][" + nextC + "]  foundGoal: "
							//		+ foundGoal);
						}
						break;
					case 3:
						if ((c + 1) >= Main.COLUMNS || qtable[r][(c + 1)].isWall()) {
							//System.out.println("Down move is a Wall = [" + r
							//		+ "][" + (c + 1) + "]");
						} else {
							selectingRandomMove = false;
							moveDown = true;
							nextR = r;
							nextC = (c + 1);
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							//System.out.println("Up move is Valid = [" + nextR
							//		+ "][" + nextC + "]  foundGoal: "
							//		+ foundGoal);
						}
						break;
					default:
						System.out
								.println("Error random move switch entering default case :: move: "
										+ move);
						break;
					}
			}
		} else {
			qtable[r][c].getDirection();
			nextR = qtable[r][c].getMoveR();
			nextC = qtable[r][c].getMoveC();
			foundGoal = qtable[r][c].isMoveIsGoal();
		}	
	}

	
	
	public double getLeft(int r, int c) {
		if (r > 0) {
			leftValid = !qtable[r - 1][c].isWall();
			return qtable[r - 1][c].getWeight();
		}
		leftValid = false;
		return 0.0;
	}

	public double getRight(int r, int c) {
		if (r < Main.ROWS - 1) {
			rightValid = !qtable[r + 1][c].isWall();
			return qtable[r + 1][c].getWeight();
		}
		rightValid = false;
		return 0.0;
	}

	public double getUp(int r, int c) {
		if (c > 0) {
			upValid = !qtable[r][c - 1].isWall();
			return qtable[r][c - 1].getWeight();
		}
		upValid = false;
		return 0.0;
	}

	public double getDown(int r, int c) {
		if (c < Main.COLUMNS - 1) {
			downValid = !qtable[r][c + 1].isWall();
			return qtable[r][c + 1].getWeight();
		}
		downValid = false;
		return 0.0;
	}

	// ==============================================================================
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

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

}
