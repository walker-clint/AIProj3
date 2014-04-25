package model;

import java.awt.Checkbox;
import java.util.Random;

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
	private boolean moveLeft, moveRight, moveUp, moveDown, foundGoal, random;

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
				qtable[r][c] = new Square();
				qtable[r][c].setWeight(result);
				System.out.println("double random value: " + rand
						+ "  random number: " + result + " seeded value = "
						+ qtable[r][c].getWeight());
				qtable[r][c].setReward(seedReward);
				System.out.println("QTable[" + r + "][" + c + "] toString: "
						+ qtable[r][c].toString());
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
					qtable[r][c].setScaleFactorX(0);
					qtable[r][c].setScaleFactorY(0);
				} else {
					qtable[r][c].setColor("LIGHTGRAY");
					double dir = 270, value = getLeft(r, c), leftX, rightX, upY, downY, rayX, rayY, magnitude;
					moveLeft = true;
					if(value < getRight(r, c)){
						value = getRight(r, c);
						dir = 90;
						moveRight = true;
						moveLeft = false;
					}
					if (value < getUp(r, c)){
						value = getUp(r, c);
						dir = 0;
						moveUp = true;
						moveLeft = false;
						moveRight = false;
					}
					if (value < getDown(r, c)){
						value = getDown(r, c);
						dir = 180;
						moveDown = true;
						moveUp = false;
						moveLeft = false;
						moveRight = false;
					}
					qtable[r][c].setDirection(dir);
					leftX = getLeft(r, c) * -1;
					rightX = getRight(r, c);
					rayX = Math.abs(leftX + rightX);
					upY = getUp(r, c);
					downY = getDown(r, c) * -1;
					rayY = Math.abs(upY + downY);

					magnitude = Math.sqrt((rayX * rayX) + (rayY * rayY));
					magnitude *= 10;
					qtable[r][c].setScaleFactorX(magnitude);
					qtable[r][c].setScaleFactorY(magnitude);
					qtable[r][c].setRotation(Math.atan2(rayY, rayX));
					System.out.println("Normal: [" + r + "][" + c + "] dir:" + dir + " mag: " + magnitude);
				}
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
		System.out.println("Check random move in model.AI: " + result
				+ " Lambda= " + lambda + "Random? " + random);

	}

	public void makeMove(int r, int c) {
		System.out.println("RC [" + r + "][" + c + "]");
		// checkRandom();
		checkRandom();
		setDirection(r, c, random);
		/*if (!random){
			updateQTable(r, c, nextR, nextC);
		}*/
		updateQTable(r, c, nextR, nextC);
	}

	public void setColor(int r, int c, String color) {
		qtable[r][c].setColor(color);
	}

	public double updateQTable(int r, int c, int rnext, int cnext) {
		System.out.print("Q(s,a) = Q(s,a) + alpha(reward + (gamma * Q(s',a')) - Q(s,a))");
		double result = 0, leftX,rightX,upY,downY,magnitude, rayX,rayY;
		result = (qtable[r][c].getWeight() + (alpha * (qtable[rnext][cnext].getReward() + ((gamma * qtable[rnext][cnext].getWeight()) - qtable[r][c].getWeight()))));
		System.out.print("\n" + result + "=" + qtable[r][c].getWeight() + alpha + " + ( " + qtable[r][c].getReward() + " + (" + gamma + "*" + qtable[rnext][cnext].getWeight() + ") - " + qtable[r][c].getWeight() + ")" );
		System.out.print("\n");
		return result;
	}
	
	public double updateRewardTable(int r, int c, int rnext, int cnext){
		double leftX,rightX,upY,downY,magnitude, rayX,rayY, value;
		value = qtable[r][c].getReward() + (qtable[rnext][cnext].getReward() * Main.GBC.getRewardDecay() - qtable[r][c].getReward());
		System.out.println("reward update value: " + value + " for location: [" + r + "][" + c +"]");
		return value;
		// reward = reward + lastSpaceReware * decay - this reward
		//qtable[r][c].setReward( qtable[r][c].getReward() + (qtable[rnext][cnext].getReward() * Main.GBC.getRewardDecay() - qtable[r][c].getReward()));
		/*leftX = getLeft(r, c) * -1;
		rightX = getRight(r, c);
		rayX = Math.abs(leftX + rightX);
		upY = getUp(r, c);
		downY = getDown(r, c) * -1;
		rayY = Math.abs(upY + downY);

		magnitude = Math.sqrt((rayX * rayX) + (rayY * rayY));
		magnitude *= 10;
		qtable[r][c].setScaleFactorX(magnitude);
		qtable[r][c].setScaleFactorY(magnitude);
		qtable[r][c].setRotation(Math.atan2(rayY, rayX));
		
		double dir = 270;
		double qVal;
		qVal = getLeft(r, c); 
		if (getRight(r, c) > qVal) {
			dir = 90;
			qVal = getRight(r, c);
		}
		if (getUp(r, c) > qVal) {
			dir = 0;
			qVal = getUp(r, c);
			
		}
		if (getDown(r, c) > qVal) {
			dir = 180;
		}
		qtable[r][c].setDirection(dir);*/
		//System.out.println("\t\tNormal: [" + r + "][" + c + "] dir:" + qtable[r][c].getDirection() + " mag: " + magnitude + " rotation: " + qtable[r][c].getRotation());
		
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
		double left, right, up, down, leftX = 0, rightX, upY, downY, rayX, rayY, magnitude;
		if (random) {
			boolean selectingRandomMove = true, validMove = false;
			while (selectingRandomMove) {
				Random rNum = new Random();
				int move = rNum.nextInt(4);
				System.out.println("Random move is true: move is: " + move
						+ " 0 = left: 1 = right: 2 = up: 3 = down");
				switch (move) {
					case 0:
						if ((r - 1 < 0) || qtable[(r - 1)][c].isWall()) {
							System.out.println("Left move is a Wall = ["
									+ (r - 1) + "][" + c + "]");
						} else {
							selectingRandomMove = false;
							moveLeft = true;
							nextR = (r - 1);
							nextC = c;
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							System.out.println("Left move is Valid = [" + nextR
									+ "][" + nextC + "]  foundGoal: "
									+ foundGoal);
						}
						break;
					case 1:
						if ((r + 1) >= Main.ROWS || qtable[(r + 1)][c].isWall()) {
							System.out.println("Right move is a Wall = ["
									+ (r + 1) + "][" + c + "]");
						} else {
							selectingRandomMove = false;
							moveRight = true;
							nextR = (r + 1);
							nextC = c;
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							System.out.println("Right move is Valid = ["
									+ nextR + "][" + nextC + "]  foundGoal: "
									+ foundGoal);
						}
						break;
					case 2:
						if ((c - 1) < 0 || qtable[r][(c - 1)].isWall()) {
							System.out.println("Up move is a Wall = [" + r
									+ "][" + (c - 1) + "]");
						} else {
							selectingRandomMove = false;
							moveUp = true;
							nextR = r;
							nextC = (c - 1);
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							System.out.println("Up move is Valid = [" + nextR
									+ "][" + nextC + "]  foundGoal: "
									+ foundGoal);
						}
						break;
					case 3:
						if ((c + 1) >= Main.COLUMNS || qtable[r][(c + 1)].isWall()) {
							System.out.println("Down move is a Wall = [" + r
									+ "][" + (c + 1) + "]");
						} else {
							selectingRandomMove = false;
							moveDown = true;
							nextR = r;
							nextC = (c + 1);
							if (qtable[nextR][nextC].isGoal()) {
								foundGoal = true;
							}
							System.out.println("Up move is Valid = [" + nextR
									+ "][" + nextC + "]  foundGoal: "
									+ foundGoal);
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
			//double dir = 270;
			double qVal;
			left = getLeft(r, c);
			// left = updateQTable(r, c, left);
			System.out.println("qtable[" + r + "][" + c + "] left weight = "
					+ left);
			qVal = left;
			moveLeft = true;
			moveRight = false;
			moveDown = false;
			moveUp = false;
			right = getRight(r, c);
			// right = updateQTable(r, c, right);
			System.out.println("qtable[" + r + "][" + c + "] right weight = "
					+ right);
			up = getUp(r, c);
			// up = updateQTable(r, c, up);
			System.out.println("qtable[" + r + "][" + c + "] up weight = "
					+ up);
			down = getDown(r, c);
			// down = updateQTable(r, c, down);
			System.out.println("qtable[" + r + "][" + c + "] down weight = "
					+ down);
			if (right > qVal) {
				//dir = 90;
				moveRight = true;
				moveLeft = false;
				moveDown = false;
				moveUp = false;
				qVal = right;
			}
			if (up > qVal) {
				//dir = 0;
				moveUp = true;
				moveRight = false;
				moveLeft = false;
				moveDown = false;
				qVal = up;
			}
			if (down > qVal) {
				//dir = 180;
				qVal = down;
				moveDown = true;
				moveUp = false;
				moveLeft = false;
				moveRight = false;
			}
			System.out.println("Left:" + moveLeft + " right: " + moveRight + " up: " + moveUp + " down: " + moveDown);
			if (moveLeft) {
				if (qtable[(r - 1)][c].isWall()) {
					System.out.println("Left move is a Wall = [" + (r - 1)
							+ "][" + c + "]");
				} else {
					moveLeft = true;
					nextR = (r - 1);
					nextC = c;
					if (qtable[nextR][nextC].isGoal()) {
						foundGoal = true;
					}
					System.out.println("Left move is Valid = [" + nextR + "]["
							+ nextC + "]  foundGoal: " + foundGoal);
				}
			}
			if (moveRight) {
				if (qtable[(r + 1)][c].isWall()) {
					System.out.println("Right move is a Wall = [" + (r + 1)
							+ "][" + c + "]");
				} else {
					moveRight = true;
					nextR = (r + 1);
					nextC = c;
					if (qtable[nextR][nextC].isGoal()) {
						foundGoal = true;
					}
					System.out.println("Right move is Valid = [" + nextR + "]["
							+ nextC + "]  foundGoal: " + foundGoal);
				}
			}
			if (moveUp) {
				if (qtable[r][(c - 1)].isWall()) {
					System.out.println("Up move is a Wall = [" + r + "]["
							+ (c - 1) + "]");
				} else {
					moveUp = true;
					nextR = r;
					nextC = (c - 1);
					if (qtable[nextR][nextC].isGoal()) {
						foundGoal = true;
					}
					System.out.println("Up move is Valid = [" + nextR + "]["
							+ nextC + "]  foundGoal: " + foundGoal);
				}
				if (moveDown) {
					if (qtable[r][(c + 1)].isWall()) {
						System.out.println("Down move is a Wall = [" + r + "]["
								+ (c + 1) + "]");
					} else {
						moveDown = true;
						nextR = r;
						nextC = (c + 1);
						if (qtable[nextR][nextC].isGoal()) {
							foundGoal = true;
						}
						System.out.println("Up move is Valid = [" + nextR
								+ "][" + nextC + "]  foundGoal: " + foundGoal);
					}
				}
			}
			/*qtable[r][c].setDirection(dir);
			//qtable[r][c].setWeight(qVal);

			leftX = left * -1;
			rightX = right;
			rayX = Math.abs(leftX + rightX);
			upY = up;
			downY = down * -1;
			rayY = Math.abs(upY + downY);

			magnitude = Math.sqrt((rayX * rayX) + (rayY * rayY));
			magnitude *= 10;
			qtable[r][c].setScaleFactorX(magnitude);
			qtable[r][c].setScaleFactorY(magnitude);
			qtable[r][c].setRotation(Math.atan2(rayY, rayX));*/

		}
	}

	
	
	private double getLeft(int r, int c) {
		if (r > 0) {
			return qtable[r - 1][c].getWeight();
		}
		return 0.0;
	}

	private double getRight(int r, int c) {
		if (r < Main.ROWS - 1) {
			return qtable[r + 1][c].getWeight();
		}
		return 0.0;
	}

	private double getUp(int r, int c) {
		if (c > 0) {
			return qtable[r][c - 1].getWeight();
		}
		return 0.0;
	}

	private double getDown(int r, int c) {
		if (c < Main.COLUMNS - 1) {
			return qtable[r][c + 1].getWeight();
		}
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

}
