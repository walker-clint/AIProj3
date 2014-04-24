package model;

/*
 * 	Written by Clinton Walker 2014
 *  Artificial Intelligence CS4523 Southern Polytechnic State University
 *  Project 3
 *  all rights reserved. 
 * */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import application.Main;
import model.Square;

public class SavedMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double			lambda,
							lambdaDecay,
							reward,
							rewardDecay,
							alpha,
							gamma,
							X,
							Y,
							seedMin,
							seedMax,
							seedReward;
	private int				startX,
							startY,
							goalX,
							goalY,
							counter				= 0,
							clock				= 0,
							speed				= 500,
							speedCounter 		= 0,
							runs				= 0,
							r					= 0,
							c					= 0;
	private String			input;
	private boolean			random 				= true,
							started				= false,
							paused 				= false,
							stepping 			= false;
	
	private Square[][]		qtable;
	
	
	public SavedMap(double _lambda, double _lambdaDecay, double _reward, double _rewardDecay, 
			double _alpha, double _gamma, double _seedMin, 
			double _seedMax, double _seedReward, int _startX, int _startY, int _goalX, int _goalY,
			int _counter, int _clock, int _runs, Square[][] _qtable) {
		lambda = _lambda;
		lambdaDecay = _lambdaDecay;
		reward = _reward;
		rewardDecay = _rewardDecay;
		alpha = _alpha;
		gamma = _gamma;
		seedMin = _seedMin;
		seedMax = _seedMax;
		seedReward = _seedReward;
		startX = _startX;
		startY = _startY;
		goalX = _goalX;
		goalY = _goalY;
		counter = _counter;
		clock = _clock;
		runs = _runs;
		qtable = _qtable;		
	}
	
	
	public boolean saveMap(SavedMap map, String fileName)
	{
		System.out.println("Write message tostring: " + map.toString());
		String path = Main.FILE_PATH + "/bin/Maps/" + fileName + ".map";
		System.out.println("Save File as: " + path);
		try {
			FileOutputStream outputStream =
					new FileOutputStream(path, false);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(map);
			byte[] msgBytes = (out.toByteArray());
			outputStream.write(msgBytes);
			outputStream.close();
			os.close();
			out.close();
			return true;
		}
		catch(Exception ex) {
			System.out.print("Write UpdateMessage Error: " + ex.toString());
			return false;
		}
	}
	
	public static SavedMap loadMap(String filename)
	{
		Path path = Paths.get(filename);
		System.out.println(path);
		try {
			
			byte[] dataBytes = Files.readAllBytes(path);
			//dataBytes = Message.decompress(dataBytes);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(dataBytes);
			ObjectInputStream objStream = new ObjectInputStream(byteStream);
			SavedMap x = (SavedMap)objStream.readObject();
			System.out.println("readfile message toString: " + x.toString());
			return x;
		}
		catch(Exception ex) {
			System.out.print(ex.toString());
			return null;				
		}	
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

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public double getRewardDecay() {
		return rewardDecay;
	}

	public void setRewardDecay(double rewardDecay) {
		this.rewardDecay = rewardDecay;
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

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
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

	public double getSeedReward() {
		return seedReward;
	}

	public void setSeedReward(double seedReward) {
		this.seedReward = seedReward;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

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

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeedCounter() {
		return speedCounter;
	}

	public void setSpeedCounter(int speedCounter) {
		this.speedCounter = speedCounter;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isStepping() {
		return stepping;
	}

	public void setStepping(boolean stepping) {
		this.stepping = stepping;
	}

	public Square[][] getQtable() {
		return qtable;
	}

	public void setQtable(Square[][] qtable) {
		this.qtable = qtable;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		String resultString = "SavedMap [lambda=" + lambda + ", lambdaDecay=" + lambdaDecay
				+ ", reward=" + reward + ", rewardDecay=" + rewardDecay
				+ ", alpha=" + alpha + ", gamma=" + gamma + ", X=" + X + ", Y="
				+ Y + ", seedMin=" + seedMin + ", seedMax=" + seedMax
				+ ", seedReward=" + seedReward + ", startX=" + startX
				+ ", startY=" + startY + ", goalX=" + goalX + ", goalY="
				+ goalY + ", counter=" + counter + ", clock=" + clock
				+ ", speed=" + speed + ", speedCounter=" + speedCounter
				+ ", runs=" + runs + ", r=" + r + ", c=" + c + ", input="
				+ input + ", random=" + random + ", started=" + started
				+ ", paused=" + paused + ", stepping=" + stepping + "\nqtable list";
		for (int r = 0; r < qtable[0].length; r++){
			for (int c = 0; c < qtable[1].length; c++){
				resultString += "\nqtable[" + r + "][" + c + "]" + qtable[r][c].toString();
			}
		}
		return resultString;
				
		
	}
	
	
}
