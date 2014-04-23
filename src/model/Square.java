package Model;

/*
 *	Written by Clinton Walker 2014
 *
 *  Container cLass for holding the QMap information
 * */

import javafx.scene.paint.Color;

public class Square {
	//==============================================================================
	// variables
	
	private 	double			reward,
								weight,
								scaleFactor,
								direction,
								rotation;
	private		boolean			isGoal;
	private 	Color			color;
	
	//==============================================================================
	// constructors
	
	public Square(){
		reward = 0;
		weight = 0;
		scaleFactor = 1;
		direction = 0;
		isGoal = false;
		color = Color.WHITE;
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
		this.scaleFactor = scaleFactor;
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
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public double getRotation() {
		return rotation;
	}
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	
	//==============================================================================
	// methods
	





	@Override
	public String toString() {
		return "Square [reward=" + reward + ", weight=" + weight
				+ ", scaleFactor=" + scaleFactor + ", direction=" + direction
				+ ", isGoal=" + isGoal + ", color=" + color + "]";
	}
	
					
	
	

}
