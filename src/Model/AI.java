package Model;

import java.util.Random;

import sun.net.www.content.audio.x_aiff;

import com.sun.javafx.css.FontUnits.Weight;

import javafx.scene.paint.Color;
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
									seedMax;			// initial seed for the Max value of the qtable weight

	
	//==============================================================================
	//constructors
	public AI() {
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}
	
	public AI(int _goalX, int _goalY, double _seedMin, double _seedMax, double _alpha, double _lambda, double _lambdaDecay, double _seedReward, double _goalReward) {
		goalX = _goalX;
		goalY = _goalY;
		seedMin = _seedMin;
		seedMax = _seedMax;
		alpha = _alpha;
		lambda = _lambda;
		lambdaDecay = _lambdaDecay;
		seedReward = _seedReward;
		goalReward = _goalReward;
		qtable = new Square[Main.ROWS][Main.COLUMNS];
	}
	
	//==============================================================================
	//methods
	
	//  http://stackoverflow.com/questions/3680637/how-to-generate-a-random-double-in-a-given-range
	public void seedQTable() {

		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalX == r && goalY == c){
					qtable[r][c] = new Square();
					qtable[r][c].setGoal(true);
					qtable[r][c].setReward(goalReward);
				} else {
					double random = new Random().nextDouble();
					double result = seedMin + (random * (seedMax - seedMin));
					qtable[r][c] = new Square();
					qtable[r][c].setWeight(result); 
					System.out.println("random: " + result + " seeded value = " + qtable[r][c].getWeight());
					qtable[r][c].setReward(seedReward);
				}
				System.out.println("QTable[" + r + "][" + c + "] toString: " + qtable[r][c].toString());
			}
		}
	}

	public void setColor(int r, int c, Color color) {
		qtable[r][c].setColor(color);
	}
	
	private void updateQTable(int r, int c, double nextWeight) {
		// Q(s,a) = Q(s,a) + alpha(reward + (gamma * Q(s',a')) - Q(s,a)))
		qtable[r][c].setWeight(
				qtable[r][c].getWeight() + 
				(alpha * (
						qtable[r][c].getReward() +
						((gamma * nextWeight) -
								qtable[r][c].getWeight())))
				);
	}
	
	public void decayLambda() {
		lambda -= lambdaDecay;
	}
	
	public void decay() {
		
	}
	
	private void setDirection(int r, int c){
		double left, right, up, down;
		left = getLeft(r, c);
		right = getRight(r, c);
		up = getUp(r, c);
		down = getDown(r, c);
		// x = r cos(theta) 
		// y = r cos(theta)
		// rayx = leftx + rightx + upx + downx
		// rayy = lefty + righty + upy + downy
		// magnitude of ray = sq root(rayx^2+rayy^2)
		// direction = arctan(rayy/rayx)
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
	
}
