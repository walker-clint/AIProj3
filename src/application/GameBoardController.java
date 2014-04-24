package application;

/*
 * 	Written by Clinton Walker 2014
 *  Artificial Intelligence CS4523 Southern Polytechnic State University
 *  Project 3
 *  all rights reserved. 
 * */


import java.io.File;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import model.AI;
import model.SavedMap;
import model.Move;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GameBoardController implements Initializable, ControlledScreen {
	
	//==============================================================================
	// variables
	
	ScreensController 			myController;
		
	@FXML 	AnchorPane			rootScreen;
	@FXML	Pane				pane;
	@FXML 	Pane				popup;
	
	@FXML	Label				resultQCompleteLabel;
	@FXML	Label				currentQ0Label;
	@FXML	Label				currentRewardLabel;
	@FXML	Label				nextStateQLabel;
	@FXML	Label				currentQ1Label;
	@FXML	Label				startRLabel;
	@FXML	Label				startCLabel;
	@FXML	Label				runTimerLabel;
	@FXML	Label				numberOfRunsLabel;
	
	@FXML	TextField			lambdaField;
	@FXML	TextField			lambdaDecayField;
	@FXML	TextField			rewardField;
	@FXML	TextField			rewardDecayField;
	@FXML	TextField			alphaField;
	@FXML	TextField			gammaField;
	@FXML	TextField			startRField;
	@FXML	TextField			startCField;
	@FXML	TextField			goalRField;
	@FXML	TextField			goalCField;
	@FXML	TextField			rotateField;
	@FXML	TextField			RField;
	@FXML	TextField			CField;
	@FXML	TextField			SField;
	@FXML	TextField			fileNameField;
	@FXML	TextField			seedMaxField;
	@FXML	TextField			seedMinField;
	
		
	@FXML	Button				pauseButton;
	@FXML	Button				stepButton;
	@FXML	Button				random_FixedStartButton;
	@FXML	Button				startButton;
	@FXML	Button				stopButton;
	@FXML	Button				exitButton;
	@FXML	Button				speedButton;
	@FXML	Button				saveMapButton;
	@FXML	Button				loadMapButton;
	@FXML	Button				resetButton;

	
	@FXML 	Rectangle			newRectangle;
	
	
	private	Timer 				timer;
	private double				lambda,
								lambdaDecay,
								reward,
								rewardDecay,
								alpha,
								gamma,
								offsetX				= 20,
								offsetY				= 30,
								seedMin,
								seedMax,
								seedReward          = 0;
    private int					startR,
								startC,
								goalR,
								goalC,
								R,
								C,
								counter				= 0,
								clock				= 0,
								speed				= 500,
								speedCounter 		= 0,
								runs				= 0;
	private String				input;
	private boolean				random 				= true,
								started				= false,
								paused 				= false,
								stepping 			= false,
								reseting			= true;
	public	boolean				waiting				= true;
							
	private Rectangle[][]		rectangles;
	public 	Polygon[][]			arrows;
	private FileChooser			fileChooser 		= new FileChooser();
	public 	Stage				popupStage;
	public  Scene				popupScene;
	Dictionary<String, Color> 	colorTable 			= new Hashtable<String, Color>();
	private LinkedList<Move> 	moves				= new LinkedList<Move>();
	
	//==============================================================================
	// FXML Methods
	
	@FXML
	void startPressed(ActionEvent event){
		if(!reseting){
			System.out.println("Starting without reset");
			Main.MACHINE = new AI();
			if(getInput()){
				Main.MACHINE.setAlpha(alpha);
				Main.MACHINE.setGamma(gamma);
				Main.MACHINE.setGoalR(goalR);
				Main.MACHINE.setGoalC(goalC);
				Main.MACHINE.setGoalReward(reward);
				Main.MACHINE.setLambda(lambda);
				Main.MACHINE.setLambdaDecay(lambdaDecay);
				Main.MACHINE.setRewardDecay(rewardDecay);
				paused = false;
				pauseButton.setVisible(true);
				startButton.setVisible(false);
				stopButton.setVisible(true);
				stepButton.setVisible(false);
				resetButton.setVisible(false);
				
				started = true;
				pausePressed(event);
			}
			
		} else {
			if (getInput()){
				System.out.println("Starting with reset");
				buildColorTable();
				pauseButton.setVisible(true);
				startButton.setVisible(false);
				stopButton.setVisible(true);
				stepButton.setVisible(false);
				resetButton.setVisible(false);
				Main.MACHINE = new AI(goalR, goalC, startR, startC, seedMin, seedMax, alpha, gamma, lambda, lambdaDecay, seedReward, 0, rewardDecay);
				Main.MACHINE.seedQTable();
				makeWalls();
				Main.MACHINE.initQTableAndBoard();
				Main.MACHINE.qtable[goalR][goalC].setReward(reward);
				if(random){
					setStartPos();
				}
				buildArrows();
				updateBoard();
				R = startR;
				C = startC;
				
				started = true;
				reseting = false;
				runs = 0;
				counter = 0;
				clock = 0;
				startLoop();
			} 
		}
	}
	
	@FXML
	void saveMapPressed(ActionEvent event){
		if (fileNameField.getText().equals("") || fileNameField.getText().equals("Enter File Name")){
			fileNameField.requestFocus();
			fileNameField.setText("Enter File Name");
		} else {
			saveMap(fileNameField.getText());
		}
	}
	
	@FXML
	void loadMapPressed(ActionEvent event){
		try {
		 	Main.POPUP_WINDOW.showMessageBox(Main.PRIMARY_STAGE); 
		 	System.out.println("Load map in try screen name " + Main.SAVE_POPUP_FXML);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Load map in catch screen name " + Main.SAVE_POPUP_FXML);
		}
		
	}
	
	@FXML 
	void buildRects(ActionEvent event){
		if(rectangles != null){
			deleteRectangles();
		}
		buildRectangles();
	}
	
	@FXML
	void buildArrs(ActionEvent event){
		if (arrows != null){
			deleteArrows();
		}
		
		buildArrows();
	}
	
	@FXML 
	void updateArrowsPressed(ActionEvent event){
		updateBoard();
		event.consume();
	}
	
	@FXML
	void setSpeed(ActionEvent event){
		if (speed == 1000){
			speedCounter = 0;
			speed = 500;
			speedButton.setText("Normal");
		} else if (speed == 500){
			speedCounter = 0;
			speed = 100;
			speedButton.setText("Fast");
		} else if (speed == 100){
			speedCounter = 0;
			speed = 10;
			speedButton.setText("Very Fast");
		} else {
			speedCounter = 0;
			speed = 1000;
			speedButton.setText("Slow");
		}
	}
	
	@FXML
	void stepPressed(ActionEvent event){
		if (paused){
			stepping = true;
		} else {
			stepping = false;
		}
	}
	
	@FXML
	void pausePressed(ActionEvent event){
		if (paused){
			paused = false;
			stepping = false;
			stepButton.setVisible(false);
			speedCounter = 0;
		} else {
			paused = true;
			stepping = false;
			stepButton.setVisible(true);
		}
	}
	
	@FXML 
	public void stopPressed(ActionEvent event){
		if (started){
			pausePressed(event);
			startButton.setVisible(true);
			stopButton.setVisible(false);
			pauseButton.setVisible(false);
			stepButton.setVisible(false);
			started = false;
			resetButton.setVisible(true);
			reseting = false;
		}
		
	}
	
	@FXML
	public void resetPressed(ActionEvent event){
		if(!started){
			reset();
			startButton.setVisible(true);
			stopButton.setVisible(false);
			pauseButton.setVisible(false);
			stepButton.setVisible(false);
			started = false;
			reseting = true;
		}
	}
	
	@FXML
	public void exitPressed(ActionEvent event){
		reset();
		myController.setScreen(Main.START_SCREEN);	
		Main.PRIMARY_STAGE.setHeight(420);
		Main.PRIMARY_STAGE.setWidth(815);
	}
	
	@FXML
	public void setRandom(ActionEvent event){
		if (random){
			random = false;
			random_FixedStartButton.setText("Fixed");
			startRLabel.setVisible(true);
			startCLabel.setVisible(true);
			startRField.setVisible(true);
			startRField.setFocusTraversable(true);
			startCField.setVisible(true);
			startCField.setFocusTraversable(true);
		} else {
			random = true;
			random_FixedStartButton.setText("Random");
			startRLabel.setVisible(false);
			startCLabel.setVisible(false);
			startRField.setVisible(false);
			startRField.setFocusTraversable(false);
			startCField.setVisible(false);
			startCField.setFocusTraversable(false);
		}
	}
	
	
	//==============================================================================
	// NON-FXML Methods
	
	void startLoop(){
		timer = new Timer();
		counter = 0;
		
		timer.schedule(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						counter++;
						speedCounter++;
						if (counter == 1000){
							counter = 0;
							clock++;
							runTimerLabel.setText("" + clock);
						}
						if ((speedCounter == speed && !paused) || stepping){ // one second clock
							System.out.println("pre-move location: [" + R + "][" + C + "]");
							Main.MACHINE.makeMove(R, C);
							if(Main.MACHINE.isFoundGoal()){
								runs++;
								//unwind the reward back through the linked list or queue
								//choose new starting point 
								//erase linked list of moves
								//decay lambda
								Main.MACHINE.decayLambda();
							}
							R = Main.MACHINE.getNextR();
							C = Main.MACHINE.getNextC();
							System.out.println("move location: [" + R + "][" + C + "]");
							Main.MACHINE.qtable[R][C].setColor("DARKSLATEBLUE");
							rectangles[R][C].setFill(Color.DARKSLATEBLUE);
							moves.add(new Move(R,C));
							numberOfRunsLabel.setText("" + runs);
							Move nextMove = new Move(R, C);
							moves.add(nextMove);
							if (stepping){
								stepping = false;
							}
						}
					}
				});
			}
		}, 0, 1);
	}
	
	
	
	void saveMap(String fileName){
		SavedMap map = new SavedMap(lambda, lambdaDecay, reward, rewardDecay, alpha, gamma, seedMin, seedMax, seedReward,
					startR, startC, goalR, goalC, counter, clock, runs, Main.MACHINE.qtable);
		map.saveMap(map, fileName);
	}
	
	void updateBoard(){
		//X = Double.parseDouble(XField.getText());
		//Y = Double.parseDouble(YField.getText());
		//double s = Double.parseDouble(SField.getText());
		//double rotation = Double.parseDouble(rotateField.getText());
		System.out.println("UPDATE BOARD START");
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalR == r && goalC == c){
					System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
				} else if ( Main.MACHINE.qtable[r][c].isWall()){
					System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
				} else {
					System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
					
					arrows[r][c].setRotate(Main.MACHINE.qtable[r][c].getDirection());
					arrows[r][c].setScaleX(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactorX());
					arrows[r][c].setScaleY(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactorY());
					arrows[r][c].setLayoutX((35 * r) + offsetX); //remove after testing
					arrows[r][c].setLayoutY((35 * c) + offsetY); //remove after testing
					
				}
			}
		}
		System.out.println("UPDATE BOARD STOP");
	}
	
	void buildRectangles(){
		rectangles = new Rectangle[Main.ROWS][Main.COLUMNS];
		
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				rectangles[r][c] = RectangleBuilder.create()
						.x((35 * r) + 5)
						.y((35 * c) + 5)
						.width(30)
						.height(30)
						.fill(Color.LIGHTGRAY)
						.build();
				//pane.getChildren().add(rectangles[r][c]);
				//content.getChildren().add(rectangles[r][c]);
				pane.getChildren().add(rectangles[r][c]);
			}
		}
	}
	
	void buildArrows(){
		//X = Double.parseDouble(XField.getText());
		//Y = Double.parseDouble(YField.getText());
		//X = 20;
		//Y = 30;
		double starX = -14.0;
		double starY = -113.0;
		//double s = Double.parseDouble(SField.getText());
		//double rotation = Double.parseDouble(rotateField.getText());
		arrows = new Polygon[Main.ROWS][Main.COLUMNS];
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalR == r && goalC == c){
					arrows[r][c] = new Polygon();
					arrows[r][c].getPoints().addAll(new Double[] {35.0, 120.5, 37.9, 129.1, 46.9, 129.1, 39.7, 134.5, 42.3, 143.1,
							35.0, 139.0, 27.7, 143.1, 30.3, 134.5, 23.1, 129.1, 32.1, 129.1});
					arrows[r][c].setFill(Color.GOLD);
					arrows[r][c].setRotate(0);
					arrows[r][c].setScaleX(1);
					arrows[r][c].setScaleY(1);
					arrows[r][c].setLayoutX((35 * r) + starX);
					arrows[r][c].setLayoutY((35 * c) + starY);
					rectangles[r][c].setFill(Color.CORNFLOWERBLUE);
				} else if(startR == r && startC == c){
					arrows[r][c] = new Polygon();
					arrows[r][c].getPoints().addAll(new Double[] {-40.0, 40.0, 40.0, 40.0, 0.0, -60.0});
					arrows[r][c].setFill(Color.BLACK);
					arrows[r][c].setRotate(0);
					arrows[r][c].setScaleX(.15 * Main.SCALE_ADJUSTMENT_FACTOR);
					arrows[r][c].setScaleY(.15 * Main.SCALE_ADJUSTMENT_FACTOR);
					arrows[r][c].setLayoutX((35 * r) + offsetX);
					arrows[r][c].setLayoutY((35 * c) + offsetY);
					rectangles[r][c].setFill(Color.BLUEVIOLET);
				} else if (Main.MACHINE.qtable[r][c].isWall()){
					//do not make an arrow here
					rectangles[r][c].setFill(colorTable.get(Main.MACHINE.qtable[r][c].getColor()));
				} else {	
					arrows[r][c] = new Polygon();
					arrows[r][c].getPoints().addAll(new Double[] {-40.0, 40.0, 40.0, 40.0, 0.0, -60.0});
					arrows[r][c].setFill(Color.BLACK);
					arrows[r][c].setRotate(0);
					arrows[r][c].setScaleX(.15 * Main.SCALE_ADJUSTMENT_FACTOR);
					arrows[r][c].setScaleY(.15 * Main.SCALE_ADJUSTMENT_FACTOR );
					arrows[r][c].setLayoutX((35 * r) + offsetX);
					arrows[r][c].setLayoutY((35 * c) + offsetY);
				}
				if (arrows[r][c] != null){
					pane.getChildren().add(arrows[r][c]);
				}
			}
		}
		
	}
	
	void deleteArrows(){
		if (arrows != null){
			for (int c = 0; c < Main.COLUMNS; c++){
				for (int r = 0; r < Main.ROWS; r++){
				
					pane.getChildren().removeAll(arrows[r][c]);
				}
			}
		}
	}
	
	void deleteRectangles(){
		if (rectangles != null){
		}
	}
	
	void reset(){
		startButton.setText("Start");
		pauseButton.setVisible(false);
		stepButton.setVisible(false);
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		if (Main.MACHINE != null){
			Main.MACHINE = null;
		}
		deleteArrows();
		deleteRectangles();
		buildRectangles();
		lambda = 0;
		lambdaField.setText("0");
		lambdaDecay = 0;
		lambdaDecayField.setText("0");
		reward = 0;
		rewardField.setText("0");
		rewardDecay = 0;
		rewardDecayField.setText("0");
		alpha = 0;
		alphaField.setText("0");
		gamma = 0;
		gammaField.setText("0");
		goalR = 0;
		goalRField.setText("0");
		goalC = 0;
		goalCField.setText("0");
		started = false;
		startButton.setVisible(true);
		paused= false;
		stepping = false;
		pauseButton.setVisible(false);
		stepButton.setVisible(false);
		stopButton.setVisible(false);
		resetButton.setVisible(false);
	}
	
	
	void loadMap(){
		//start file load process
		//display file name in message log
		//show send message button
		
		String fileNameString = "";
		File file = fileChooser.showOpenDialog(Main.PRIMARY_STAGE);
		if (file != null) {
			//openFile(file);
			fileNameString = file.getAbsolutePath();
			System.out.println("filename = " + fileNameString);
			if (fileNameString != null){
				SavedMap map = SavedMap.loadMap(fileNameString);
				reset();
				buildColorTable();
				started = true;
				paused = true;
				stepping = false;
				reseting = false;
				stepButton.setVisible(true);
				pauseButton.setVisible(true);
				startButton.setVisible(false);
				stopButton.setVisible(true);
				resetButton.setVisible(false);
				initLoadedMap(map);
			}
		}
		Main.PRIMARY_STAGE.requestFocus();
		Main.POPUP_WINDOW.messageBoxStage.close();
	}
	void initLoadedMap(SavedMap map){
		paused = true;
		goalR = map.getGoalX();
		goalC = map.getGoalY();
		seedMin = map.getSeedMin();
		seedMax = map.getSeedMax();
		alpha = map.getAlpha();
		gamma = map.getGamma();
		lambda = map.getLambda();
		lambdaDecay = map.getLambdaDecay();
		seedReward = map.getSeedReward();
		reward = map.getReward();
		counter = map.getCounter();
		clock = map.getClock();
		startR = map.getStartX();
		startC = map.getStartY();
		runs = map.getRuns();
		goalRField.setText(goalR + "");
		goalCField.setText(goalC + "");
		alphaField.setText(alpha + "");
		gammaField.setText(gamma + "");
		lambdaField.setText(lambda + "");
		lambdaDecayField.setText(lambdaDecay + "");
		rewardField.setText(reward + "");
		rewardDecayField.setText(rewardDecay +"");
		runTimerLabel.setText(clock + "");
		numberOfRunsLabel.setText(runs + "");
		
		Main.MACHINE = new AI(goalR, goalC, startR, startC, seedMin, seedMax, alpha, gamma, lambda, lambdaDecay, seedReward, reward, rewardDecay);
		for (int r = 0; r < Main.ROWS; r++){
			for (int c = 0; c < Main.COLUMNS; c++){
				Main.MACHINE.qtable[r][c] = map.getQtable()[r][c];
			}
		}
		buildArrows();
		updateBoard();
		pauseButton.requestFocus();
		startLoop();
	}
	
	boolean getInput(){
		
		input = seedMinField.getText();
		try {
			seedMin = Double.parseDouble(input);
		} catch (Exception e) {
			seedMinField.requestFocus();
			return false;
		}
		input = seedMaxField.getText();
		try {
			seedMax = Double.parseDouble(input);
		} catch (Exception e) {
			seedMaxField.requestFocus();
			return false;
		}
		
		//get lambda
		input = lambdaField.getText();
		try {
			lambda = Double.parseDouble(input);
		} catch (Exception e) {
			lambdaField.requestFocus();
			return false;
		}
		//get lambda decay
		input = lambdaDecayField.getText();
		try {
			lambdaDecay = Double.parseDouble(input);
		} catch (Exception e) {
			lambdaDecayField.requestFocus();
			return false;
		}
		// get reward
		input = rewardField.getText();
		try {
			reward = Double.parseDouble(input);
		} catch (Exception e) {
			rewardField.requestFocus();
			return false;
		}
		// get reward decay
		input = rewardDecayField.getText();
		try {
			rewardDecay = Double.parseDouble(input);
		} catch (Exception e) {
			rewardDecayField.requestFocus();
			return false;
		}
		// get alpha
		input = alphaField.getText();
		try {
			alpha = Double.parseDouble(input);
		} catch (Exception e) {
			alphaField.requestFocus();
			return false;
		}
		// get gamma
		input = gammaField.getText();
		try {
			gamma = Double.parseDouble(input);
		} catch (Exception e) {
			gammaField.requestFocus();
			return false;
		}
		// if fixed get start location
		if (!random){
			input = startRField.getText();
			try {
				startR = Integer.parseInt(input);
				if (startR < 0 || startR >= Main.ROWS){
					startRField.requestFocus();
					return false;
				}
			} catch (Exception e) {
				startRField.requestFocus();
				return false;
			}
			input = startCField.getText();
			try {
				startC = Integer.parseInt(input);
				if (startC < 0 || startC >= Main.COLUMNS){
					startCField.requestFocus();
					return false;
				}
			} catch (Exception e) {
				startCField.requestFocus();
				return false;
			}
		} 
		// get goal position
		input = goalRField.getText();
		try {
			goalR = Integer.parseInt(input);
			if (goalR < 0 || goalR >= Main.ROWS){
				goalRField.requestFocus();
				return false;
			}
		} catch (Exception e) {
			goalRField.requestFocus();
			return false;
		}
		input = goalCField.getText();
		try {
			goalC = Integer.parseInt(input);
			if (goalC < 0 || goalC >= Main.COLUMNS){
				goalCField.requestFocus();
				return false;
			}
		} catch (Exception e) {
			goalCField.requestFocus();
			return false;
		}
		return true;
	}
	
	public void makeWalls(){
		int blockX, blockY;
		int numBlocks = (int)(0.1 * (Main.COLUMNS * Main.ROWS));
		System.out.println("the number of blocks = " + numBlocks);
		for (int i = 0; i < numBlocks; i++){
			Random num = new Random();
			
			blockX = num.nextInt(Main.ROWS);
			blockY = num.nextInt(Main.COLUMNS);
			
			if(Main.MACHINE.qtable[blockX][blockY].isGoal() || Main.MACHINE.qtable[blockX][blockY].isWall()){
				--i;
			} else {
				
				Main.MACHINE.qtable[blockX][blockY].setWall(true);
				Main.MACHINE.qtable[blockX][blockY].setColor("DARKGREY");
				rectangles[blockX][blockY].setFill(colorTable.get(Color.DARKGREY));
				System.out.println("blockX: " + blockX + "  blockY: " + blockY + "isWall: " + Main.MACHINE.qtable[blockX][blockY].isWall() + " Color: " +  Main.MACHINE.qtable[blockX][blockY].getColor());
			}
		}
	}
	
	private void setStartPos() {
		Random num = new Random();
		boolean settingStartPos = true;
		while (settingStartPos){
			startR = num.nextInt(Main.ROWS);
			startC = num.nextInt(Main.COLUMNS);
			System.out.println("startR: " + startR + "  startC: " + startC);
			if(!Main.MACHINE.qtable[startR][startC].isGoal() || !Main.MACHINE.qtable[startR][startC].isWall()){
				Main.MACHINE.qtable[startR][startC].setColor("CORNFLOWERBLUE");
				settingStartPos = false;
			}
		}
	}
	
	private void buildColorTable(){
		colorTable.put("DARKGREY", Color.DARKGREY);
		colorTable.put("BLACK", Color.BLACK);
		colorTable.put("LIGHTGRAY", Color.LIGHTGRAY);
		colorTable.put("GOLD", Color.GOLD);
		colorTable.put("RED", Color.RED);
		colorTable.put("CORNFLOWERBLUE", Color.CORNFLOWERBLUE);
		colorTable.put("BLUEVIOLET", Color.BLUEVIOLET);
		colorTable.put("ALICEBLUE", Color.ALICEBLUE);
		System.out.println("DARKGREY= " + "DARKGREY" + " from dictionary: " + colorTable.get("DARKGREY"));
		System.out.println("BLACK= " + "BLACK" + " from dictionary: " + colorTable.get("BLACK"));
		System.out.println("LIGHTGRAY= " + "LIGHTGRAY" + " from dictionary: " + colorTable.get("LIGHTGRAY"));
		System.out.println("GOLD= " + "GOLD" + " from dictionary: " + colorTable.get("GOLD"));
		System.out.println("RED= " + "RED" + " from dictionary: " + colorTable.get("RED"));
		System.out.println("CORNFLOWERBLUE= " + "CORNFLOWERBLUE" + " from dictionary: " + colorTable.get("CORNFLOWERBLUE"));
		System.out.println("BLUEVIOLET= " + "BLUEVIOLET" + " from dictionary: " + colorTable.get("BLUEVIOLET"));
		System.out.println("DARKSLATEBLUE= " + "DARKSLATEBLUE" + " from dictionary: " + colorTable.get("DARKSLATEBLUE"));
	}
	
	public void errorStop(){
		if (paused){
			paused = false;
			stepping = false;
			stepButton.setVisible(false);
			speedCounter = 0;
		} else {
			paused = true;
			stepping = false;
			stepButton.setVisible(true);
		}
	}
	
	//==============================================================================
	// getters and setters
	
	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}
	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	//==============================================================================
	@Override
	public void setScreenParent(ScreensController screenPage) {
		myController = screenPage;
	}

	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rootScreen.setStyle("-fx-background-color: beige");
		Main.GBC = this;
		buildRectangles();
		seedMinField.requestFocus();
		Main.PRIMARY_STAGE.setY(10);
	}

}
