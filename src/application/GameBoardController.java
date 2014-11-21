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
	@FXML	Label				currentLambdaLabel;
	@FXML	Label				movesLabel;
	@FXML	Label				avgMovesLabel;
	@FXML	Label				percentRandomMovesLabel;
	@FXML	Label				deltaQLabel;
	
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
	@FXML	TextField			numRunsField;
	@FXML	TextField			percentWallsField;
	
		
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
	@FXML	Button				arrowTypeButton;
	@FXML	Button				propogationTypeButton;
	@FXML	Button				lambdaEnabledButton;
	@FXML	Button				AILearningEnabledButton;
	@FXML	Button				runTestSeriesButton;
	
	@FXML 	Rectangle			newRectangle;
	
	
	private	Timer 				timer,
								clockTimer;
	private double				lambda,
								lambdaDecay,
								reward,
								rewardDecay,
								alpha,
								gamma,
								percentWalls,
								avgMoves,
								totalMoves,
								oldQValue,
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
								percentRand,
								testRunStart		= 0,
								counter				= 0,
								clock				= 0,
								speed				= 500,
								speedCounter 		= 0,
								rewardPaybackNumber	= 100,
								numberOfMoves		= 0,
								runs				= 0,
								numRuns;
	private String				input;
	private boolean				random 				= true,
								started				= false,
								paused 				= false,
								stepping 			= false,
								reseting			= true,
								propogationReward	= true,
								arrowTypeDirection	= true,
								displayChanged		= false,
								lambdaEnabled		= true,
								learning			= true,
								runTestSeries		= false;
	public	boolean				waiting				= true;
							
	private Rectangle[][]		rectangles;
	public 	Polygon[][]			arrows;
	private FileChooser			fileChooser 		= new FileChooser();
	public 	Stage				popupStage;
	public  Scene				popupScene;
	Dictionary<String, Color> 	colorTable 			= new Hashtable<String, Color>();
	private LinkedList<Move> 	moves				= new LinkedList<Move>();
	private LinkedList<Move> 	testMoves			= new LinkedList<Move>();
	//==============================================================================
	// FXML Methods
	
	@FXML
	void startPressed(ActionEvent event){
		if(!reseting){
			System.out.println("Starting without reset");
			//Main.MACHINE = new AI();
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
				stepping = false;
				pauseButton.setVisible(true);
				startButton.setVisible(false);
				stopButton.setVisible(true);
				stepButton.setVisible(false);
				resetButton.setVisible(false);
				started = true;
				if (clockTimer == null){
					clock = 0;
					runTimerLabel.setText(0 +"");
					startTimerLoop();
				} else {
					clock = 0;
					clockTimer.cancel();
					clockTimer = null;
					runTimerLabel.setText(0 +"");
					startTimerLoop();
				}
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
					moves.add(new Move(startR,startC));
				} else {
					moves.add(new Move(startR,startC));
				}
				buildArrows();
				updateBoard();
				R = startR;
				C = startC;
				
				started = true;
				reseting = false;
				
				pausePressed(event);
				startTimerLoop();
				startLoop();
			} 
		}
	}
	
	@FXML
	void saveStatePressed(ActionEvent event){
		if (fileNameField.getText().equals("") || fileNameField.getText().equals("Enter File Name") || fileNameField.getText().equals("File Saved")){
			fileNameField.requestFocus();
			fileNameField.setText("Enter File Name");
		} else {
			saveState(fileNameField.getText());
			fileNameField.setText("File Saved");
		}
	}
		
	@FXML
	void loadStatePressed(ActionEvent event){
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
		} else if (speed == 10){ 
			speedCounter = 0;
			speed = 1;
			speedButton.setText("Running");
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
	void resetRunsPressed(ActionEvent event){
		runs = 0;
		numberOfRunsLabel.setText(runs + "");
	}
	
	@FXML
	void pausePressed(ActionEvent event){
		System.out.println("Paused Pressed pause = " + paused + " before button press. Stepping = " + stepping);
		if (paused){
			paused = false;
			stepping = false;
			stepButton.setVisible(false);
			speedCounter = 0;
			currentLambdaLabel.setVisible(false);
			lambdaField.setText(lambda + "");
		} else {
			paused = true;
			stepping = false;
			stepButton.setVisible(true);
		}
		System.out.println("Paused Pressed pause = " + paused + " after button press. Stepping = " + stepping);
	}
	
	@FXML 
	public void stopPressed(ActionEvent event){
		if (started){
			if(!paused){
				pausePressed(event);
			}
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
			if(timer != null){
				timer.cancel();
				timer = null;
			}
			counter = 0;
			startTimerLoop(); 
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
	
	@FXML
	public void showPropogationPressed(ActionEvent event){
		if (propogationReward){
			propogationReward = false;
			propogationTypeButton.setText("QTable\nPropogation");
			displayChanged = true;
			updateBoard();
		} else {
			propogationReward = true;
			propogationTypeButton.setText("Reward\nPropogation");
			displayChanged = true;
			updateBoard();
		}
	}
	 
	@FXML
	public void showArrorTypePressed(ActionEvent event){
		if (arrowTypeDirection){
			arrowTypeDirection = false;
			arrowTypeButton.setText("Rotation");
			displayChanged = true;
			updateBoard();
		} else {
			arrowTypeDirection = true;
			arrowTypeButton.setText("Direction");
			displayChanged = true;
			updateBoard();
		}
	}
	
	@FXML
	public void lambdaEnabledPressed(ActionEvent event){
		if (lambdaEnabled){
			lambdaEnabled = false;
			lambdaEnabledButton.setText("Disabled");
			lambdaEnabledButton.setTextFill(Color.RED);
		} else {
			lambdaEnabled = true;
			lambdaEnabledButton.setText("Enabled");
			lambdaEnabledButton.setTextFill(Color.BLACK);
		}
	}
	
	@FXML
	public void aILearningEnabledPressed(ActionEvent event){
		if (learning){
			learning = false;
			AILearningEnabledButton.setText("AI Learning\nDisabled");
			AILearningEnabledButton.setTextFill(Color.RED);
		} else {
			learning = true;
			AILearningEnabledButton.setText("AI Learning\nEnabled");
			AILearningEnabledButton.setTextFill(Color.BLACK);
		}
	}
	
	@FXML 
	void runTestSeriesPressed(ActionEvent event){
		if (runTestSeries){
			runTestSeries = false;
			runTestSeriesButton.setTextFill(Color.BLACK);
		} else {
			runTestSeriesButton.setTextFill(Color.GREEN);
			if (testMoves.size() == 0){
				buildTestRun();
			}
			runTestSeries = true;
			if (learning){
				aILearningEnabledPressed(event);
			}
			if (lambdaEnabled){
				lambdaEnabledPressed(event);
			}
			testRunStart = 1;
			resetRunsPressed(event);
			numRuns = testMoves.size();
			numRunsField.setText(numRuns + "");
			numRuns++;
			moves.clear();
			R = testMoves.get(0).getR();
			C = testMoves.get(0).getC();
			numberOfMoves = 0;
			avgMoves = 0;
			percentRand = 0;
			totalMoves = 0;
		}
	}
	//==============================================================================
	// NON-FXML Methods
	void resetMapColors(){
		System.out.println("START RESET BOARD COLORS");
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalR == r && goalC == c){
					 Main.MACHINE.qtable[r][c].setColor("CORNFLOWERBLUE");
					 rectangles[r][c].setFill(Color.CORNFLOWERBLUE);
					// System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
				} else if ( Main.MACHINE.qtable[r][c].isWall()){
					//System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
					Main.MACHINE.qtable[r][c].setColor("DARKGREY");
					 rectangles[r][c].setFill(Color.DARKGREY);
				} else {
					//System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
					Main.MACHINE.qtable[r][c].setColor("LIGHTGRAY");
					rectangles[r][c].setFill(Color.LIGHTGRAY);
					arrows[r][c].setRotate(Main.MACHINE.qtable[r][c].getDirection());
					arrows[r][c].setScaleX(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactor());
					arrows[r][c].setScaleY(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactor());
					arrows[r][c].setLayoutX((35 * r) + offsetX); //remove after testing
					arrows[r][c].setLayoutY((35 * c) + offsetY); //remove after testing
				}
			}
		}
		//System.out.println("STOP RESET BOARD COLORS");
	}
	
	void fixGoalState(){
		if (Main.MACHINE.qtable[goalR][goalC].isWall()){
			Main.MACHINE.qtable[goalR][goalC].setWall(false);
		}
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
					//System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
				} else if ( Main.MACHINE.qtable[r][c].isWall()){
					//System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
				} else {
					//System.out.println("QTable[" + r + "][" + c + "] " + Main.MACHINE.qtable[r][c].toString());
					Main.MACHINE.qtable[r][c].updateSquareDisplay();
					arrows[r][c].setScaleX(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactor());
					arrows[r][c].setScaleY(Main.SCALE_ADJUSTMENT_FACTOR * Main.MACHINE.qtable[r][c].getScaleFactor());
					//arrows[r][c].setLayoutX((35 * r) + offsetX); //remove after testing
					//arrows[r][c].setLayoutY((35 * c) + offsetY); //remove after testing
					
				}
			}
		}
		//System.out.println("UPDATE BOARD STOP");
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
	
	void buildTestRun(){
		for (int r = 0; r < Main.ROWS; r++){
			if(!Main.MACHINE.qtable[r][0].isWall() && !Main.MACHINE.qtable[r][0].isGoal()){
				testMoves.add(new Move(r, 0));
			}
			if(!Main.MACHINE.qtable[r][Main.COLUMNS-1].isWall() && !Main.MACHINE.qtable[r][Main.COLUMNS-1].isGoal()){
				testMoves.add(new Move(r, Main.COLUMNS-1));
			}
		}
		for (int c = 0; c < Main.COLUMNS; c++){
			if(!Main.MACHINE.qtable[0][c].isWall() && !Main.MACHINE.qtable[0][c].isGoal()){
				testMoves.add(new Move(0, c));
			}
			if(!Main.MACHINE.qtable[Main.ROWS-1][c].isWall() && !Main.MACHINE.qtable[Main.ROWS-1][c].isGoal()){
				testMoves.add(new Move(Main.ROWS-1, c));
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
					arrows[r][c].setScaleX(.005 * Main.SCALE_ADJUSTMENT_FACTOR);
					arrows[r][c].setScaleY(.005 * Main.SCALE_ADJUSTMENT_FACTOR);
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
					arrows[r][c].setScaleX(.005 * Main.SCALE_ADJUSTMENT_FACTOR);
					arrows[r][c].setScaleY(.005 * Main.SCALE_ADJUSTMENT_FACTOR );
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
		if (clockTimer != null){
			clockTimer.cancel();
			clockTimer = null;
		}
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
		lambdaField.setText("90");
		lambdaDecay = 0;
		lambdaDecayField.setText("0.01");
		reward = 0;
		rewardField.setText("1");
		rewardDecay = 0;
		rewardDecayField.setText("0.001");
		alpha = 0;
		alphaField.setText("0.3");
		gamma = 0;
		gammaField.setText("0.7");
		goalR = 0;
		goalRField.setText("10");
		goalC = 0;
		goalCField.setText("10");
		started = false;
		startButton.setVisible(true);
		paused= false;
		stepping = false;
		pauseButton.setVisible(false);
		stepButton.setVisible(false);
		stopButton.setVisible(false);
		resetButton.setVisible(false);
		numberOfMoves = 0;
		numRuns = 0;
		numberOfRunsLabel.setText(numRuns + "");
		movesLabel.setText(numberOfMoves + "");
		runs = 0;
		counter = 0;
		clock = 0;
		runTimerLabel.setText(0 +"");
		percentRand = 0;
		totalMoves = 0;
		avgMoves = 0;
		avgMovesLabel.setText(avgMoves + "");
		percentRandomMovesLabel.setText(0+"%");
	}
	
	void saveState(String fileName){
		SavedMap state = new SavedMap(lambda, lambdaDecay, reward, rewardDecay, alpha, gamma, seedMin, seedMax, seedReward, startR, startC, R, C, numRuns, numberOfMoves, goalR, goalC, counter, clock, runs, Main.MACHINE.qtable);
		state.saveMap(state, fileName);
	}
	
	void loadState(){
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
				SavedMap state = SavedMap.loadMap(fileNameString);
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
				initLoadedState(state);
			}
		}
		Main.PRIMARY_STAGE.requestFocus();
		Main.POPUP_WINDOW.messageBoxStage.close();
	}
	
	void initLoadedState(SavedMap state){
		paused = true;
		goalR = state.getGoalX();
		goalC = state.getGoalY();
		seedMin = state.getSeedMin();
		seedMax = state.getSeedMax();
		alpha = state.getAlpha();
		gamma = state.getGamma();
		lambda = state.getLambda();
		lambdaDecay = state.getLambdaDecay();
		seedReward = state.getSeedReward();
		rewardDecay = state.getRewardDecay();
		reward = state.getReward();
		counter = state.getCounter();
		clock = state.getClock();
		startR = state.getStartX();
		startC = state.getStartY();
		runs = state.getRuns();
		numRuns = state.getNumRuns();
		R = state.getR();
		C = state.getC();
		numberOfMoves = state.getNumMoves();
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
		movesLabel.setText(numberOfMoves + "");
		numRunsField.setText(numRuns + "");
		Main.MACHINE = new AI(goalR, goalC, startR, startC, seedMin, seedMax, alpha, gamma, lambda, lambdaDecay, seedReward, reward, rewardDecay);
		for (int r = 0; r < Main.ROWS; r++){
			for (int c = 0; c < Main.COLUMNS; c++){
				Main.MACHINE.qtable[r][c] = state.getQtable(r,c);
			}
		}
		fixGoalState();
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
		input = numRunsField.getText();
		try {
			numRuns = Integer.parseInt(input);
			if (numRuns < 0){
				numRunsField.requestFocus();
				return false;
			}
			numRuns++;
		} catch (Exception e) {
			numRunsField.requestFocus();
			return false;
		}
		input = percentWallsField.getText();
		try {
			percentWalls = Integer.parseInt(input);
			if (percentWalls < 0 || percentWalls > 60){
				
				percentWallsField.requestFocus();
				return false;
			}
			percentWalls *= .01;
		} catch (Exception e) {
			numRunsField.requestFocus();
			return false;
		}
		return true;
	}
	
	public void makeWalls(){
		int blockX, blockY;
		int numBlocks = (int)(percentWalls * (Main.COLUMNS * Main.ROWS));
		//System.out.println("the number of blocks = " + numBlocks);
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
				//System.out.println("blockX: " + blockX + "  blockY: " + blockY + "isWall: " + Main.MACHINE.qtable[blockX][blockY].isWall() + " Color: " +  Main.MACHINE.qtable[blockX][blockY].getColor());
			}
		}
		for (int r = 0; r < Main.ROWS; r++){
			for (int c = 0; c < Main.COLUMNS; c++){
				Main.MACHINE.qtable[r][c].setMoves();
				if (Main.MACHINE.qtable[r][c].isNoValidMoves() && !Main.MACHINE.qtable[r][c].isGoal()){
					Main.MACHINE.qtable[r][c].setWall(true);
					Main.MACHINE.qtable[r][c].setColor("DARKGREY");
					rectangles[r][c].setFill(colorTable.get(Color.DARKGREY));
					System.out.println("Map fixed at [" + r + "][" + c +"]");
				}
			}
		}
	}
	
	private boolean getFixedStartLocation(){
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
		return false;
	}
	
	private void setStartPos() {
		Random num = new Random();
		boolean settingStartPos = true;
		while (settingStartPos){
			startR = num.nextInt(Main.ROWS);
			startC = num.nextInt(Main.COLUMNS);
			System.out.println("startR: " + startR + "  startC: " + startC);
			if(Main.MACHINE.qtable[startR][startC].isGoal() || Main.MACHINE.qtable[startR][startC].isWall()){
				System.out.println("Start Move: [" + startR + "][" + startC + "] is not a valid start position");
			} else {
				//Main.MACHINE.qtable[startR][startC].setColor("CORNFLOWERBLUE");
				//rectangles[startR][startC].setFill(colorTable.get(Color.CORNFLOWERBLUE));
				R = startR;
				C = startC;
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
		colorTable.put("DARKSLATEBLUE", Color.DARKSLATEBLUE);
		colorTable.put("DARKCYAN", Color.DARKCYAN);
		System.out.println("DARKGREY= " + "DARKGREY" + " from dictionary: " + colorTable.get("DARKGREY"));
		System.out.println("BLACK= " + "BLACK" + " from dictionary: " + colorTable.get("BLACK"));
		System.out.println("LIGHTGRAY= " + "LIGHTGRAY" + " from dictionary: " + colorTable.get("LIGHTGRAY"));
		System.out.println("GOLD= " + "GOLD" + " from dictionary: " + colorTable.get("GOLD"));
		System.out.println("RED= " + "RED" + " from dictionary: " + colorTable.get("RED"));
		System.out.println("CORNFLOWERBLUE= " + "CORNFLOWERBLUE" + " from dictionary: " + colorTable.get("CORNFLOWERBLUE"));
		System.out.println("BLUEVIOLET= " + "BLUEVIOLET" + " from dictionary: " + colorTable.get("BLUEVIOLET"));
		System.out.println("DARKSLATEBLUE= " + "DARKSLATEBLUE" + " from dictionary: " + colorTable.get("DARKSLATEBLUE"));
		System.out.println("DARKCYAN= " + "DARKCYAN" + " from dictionary: " + colorTable.get("DARKCYAN"));
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
	
	

	public boolean isLambdaEnabled() {
		return lambdaEnabled;
	}

	public void setLambdaEnabled(boolean lambdaEnabled) {
		this.lambdaEnabled = lambdaEnabled;
	}

	public boolean isPropogationReward() {
		return propogationReward;
	}

	public void setPropogation(boolean propogationReward) {
		this.propogationReward = propogationReward;
	}

	public boolean isArrowTypeDirection() {
		return arrowTypeDirection;
	}

	public void setShowArrowType(boolean arrowTypeDirection) {
		this.arrowTypeDirection = arrowTypeDirection;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
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
	
	@FXML
	public void onCloseWindow(ActionEvent event) {
		Platform.exit();
	}
	
	
	void startLoop(){
		timer = new Timer();
		speedCounter = 0;
		timer.schedule(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						speedCounter++;
						if ((speedCounter == speed && !paused) || stepping){ // one second clock
							//System.out.println("pre-move location: [" + R + "][" + C + "]");
							if (!random && !getFixedStartLocation()){
								
							} else {
								int 	nextR, nextC;
							
								Main.MACHINE.qtable[R][C].setColor("DARKSLATEBLUE");
								rectangles[R][C].setFill(Color.DARKSLATEBLUE);
								if (learning){
									oldQValue = Main.MACHINE.qtable[R][C].getWeight();
									currentQ0Label.setText(oldQValue + "");
									currentQ1Label.setText(oldQValue + "");
									currentRewardLabel.setText(Main.MACHINE.qtable[R][C].getReward() + "");
									Main.MACHINE.makeMove(R, C);
									
									nextR = Main.MACHINE.getNextR();
									nextC = Main.MACHINE.getNextC();
									nextStateQLabel.setText(Main.MACHINE.qtable[nextR][nextC].getWeight() + "");
									Main.MACHINE.qtable[R][C].updateWeight(nextR, nextC);
									resultQCompleteLabel.setText(Main.MACHINE.qtable[R][C].getWeight() + "");
									deltaQLabel.setText(oldQValue - Main.MACHINE.qtable[R][C].getWeight() + "");
									//System.out.println("Goal is found= " + Main.MACHINE.isFoundGoal());
									if(!currentLambdaLabel.isVisible())	{
										currentLambdaLabel.setVisible(true);
									}
									lambdaField.setText(Main.MACHINE.getLambda() + "");
									if(Main.MACHINE.isRandom()){
										//Main.MACHINE.qtable[R][C].updateWeight(nextR, nextC);
										//Main.MACHINE.qtable[R][C].updateSquareDisplay();
										percentRand++;
									}
									Main.MACHINE.qtable[R][C].updateSquareDisplay();
								} else {
									Main.MACHINE.makeMove(R, C);
									currentQ0Label.setText("---");
									currentQ1Label.setText("---");
									nextStateQLabel.setText("---");
									resultQCompleteLabel.setText("---");
									deltaQLabel.setText("---");
									nextR = Main.MACHINE.getNextR();
									nextC = Main.MACHINE.getNextC();
									Main.MACHINE.qtable[R][C].updateSquareDisplay();
								}
								R = nextR;
								C = nextC;
								//System.out.println("move location: [" + R + "][" + C + "]");
								if (!Main.MACHINE.qtable[R][C].isGoal()){
									Main.MACHINE.qtable[R][C].setColor("BLUEVIOLET");
									rectangles[R][C].setFill(Color.BLUEVIOLET);
								}
								moves.add(new Move(R,C));
								numberOfMoves++;
								movesLabel.setText(numberOfMoves + "");
								if (displayChanged){
									updateBoard();
									displayChanged = false;
								}
								if(Main.MACHINE.isFoundGoal() || (R == goalR && C == goalC)){
									runs++;
									totalMoves += numberOfMoves;
									avgMoves = totalMoves/runs;
									avgMovesLabel.setText(avgMoves + "");
									System.out.println( "100/totMoves: " + (100/totalMoves) + " total moves: " + totalMoves + " randomMoves: " + percentRand + " percentRand * (100/totMoves) :" + (100/totalMoves)*percentRand);
									
									percentRandomMovesLabel.setText((int) ((100/totalMoves)*percentRand) + "%");
									numberOfMoves = 0;
									numberOfRunsLabel.setText("" + runs);
									//unwind the reward back through the linked list or queue
									if (learning && moves.size() > 2){
										int endUnwind;
										if (moves.size() > rewardPaybackNumber + 2){
											endUnwind = moves.size() - 2 - rewardPaybackNumber;
										} else {
											endUnwind = moves.size() - 2;
										}
										for (int i = moves.size() - 2; i >= endUnwind; i--){
											int locR = moves.get(i).getR();
											int lastR = moves.get(i+1).getR();
											int locC = moves.get(i).getC();
											int lastC = moves.get(i+1).getC();
											System.out.print("Unwind i= " + i + " ::\n" + moves.get(i).toString() + "  start reward: " + Main.MACHINE.qtable[locR][locC].getReward() 
													+ "\n\t Start Weight= " + Main.MACHINE.qtable[locR][locC].getWeight() + "\n" + moves.get(i+1).toString());
											Main.MACHINE.qtable[locR][locC].updateReward(lastR, lastC);
											//Main.MACHINE.qtable[locR][locC].updateWeight(lastR, lastC);
											Main.MACHINE.qtable[locR][locC].updateSquareDisplay();
											System.out.print("  rewardvalue: " + Main.MACHINE.qtable[locR][locC].getReward() 
													+ "  startReward after decay " + Main.MACHINE.qtable[locR][locC].getReward() 
													+ "\n\t end Weight= " + Main.MACHINE.qtable[locR][locC].getWeight()+ "\n");
											
										}
									}
									resetMapColors();
									moves.clear();
									//choose new starting point
									if(runTestSeries){
										if (testRunStart < testMoves.size()){
											R = testMoves.get(testRunStart).getR();
											C = testMoves.get(testRunStart).getC();
										}
										testRunStart++;
									} else if (random){
										setStartPos();
									} else {
										int fixedR = -1, fixedC = -1;
										boolean settingStartPos = true, sR = false, sC = false;
										while (settingStartPos){
											input = startRField.getText();
											try {
												fixedR = Integer.parseInt(input);
												sR = true;
												if (fixedR < 0 || fixedR >= Main.ROWS){
													startRField.requestFocus();
													sR = false;
												}
												
											} catch (Exception e) {
												startRField.requestFocus();
												sR = false;
											}
											input = startCField.getText();
											try {
												fixedC = Integer.parseInt(input);
												sC = true;
												if (fixedC < 0 || fixedC >= Main.COLUMNS){
													startCField.requestFocus();
													sC = false;
												}
											} catch (Exception e) {
												startCField.requestFocus();
												sC = false;
											}
											if (sR && sC){
												settingStartPos = false;
												R = fixedR;
												C = fixedC;
											} else {
												setStartPos();
												if (sR){
													R = fixedR;
												}
												if (sC){
													C = fixedC;
												}
												settingStartPos = false;
											}
										}
									}
									moves.add(new Move(startR,startC));
									System.out.println("New Start Pos= [" + startR + "][" + startC + "]" );
									startR = R;
									startC = C;
									rectangles[startR][startC].setFill(Color.BLUEVIOLET);
									
									//erase linked list of moves
									
									//decay lambda
									Main.MACHINE.decayLambda();
									
									Main.MACHINE.setFoundGoal(false);
									updateBoard();
									
									if(runs >= numRuns - 1){
										errorStop();
									}
								}
								if (stepping){
									stepping = false;
								}
								speedCounter = 0;
							}
						}
					}
				});
			}
		}, 0, 1);
	}
	
	void startTimerLoop(){
		clockTimer = new Timer();
		counter = 0;
		
		clockTimer.schedule(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						counter++;
						if (counter == 1000){
							counter = 0;
							clock++;
							runTimerLabel.setText("" + clock);
						}
					}
				});
			}
		}, 0, 1);
	}
}
