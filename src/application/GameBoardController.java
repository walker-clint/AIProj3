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
	@FXML	Label				startXLabel;
	@FXML	Label				startYLabel;
	@FXML	Label				runTimerLabel;
	@FXML	Label				numberOfRunsLabel;
	
	@FXML	TextField			lambdaField;
	@FXML	TextField			lambdaDecayField;
	@FXML	TextField			rewardField;
	@FXML	TextField			rewardDecayField;
	@FXML	TextField			alphaField;
	@FXML	TextField			gammaField;
	@FXML	TextField			startXField;
	@FXML	TextField			startYField;
	@FXML	TextField			goalXField;
	@FXML	TextField			goalYField;
	@FXML	TextField			rotateField;
	@FXML	TextField			XField;
	@FXML	TextField			YField;
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
								X,
								Y,
								seedMin,
								seedMax,
								seedReward          = 0;
    private int					startX,
								startY,
								goalX,
								goalY,
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
	private LinkedList<Move> 	moves				= new LinkedList<>();
	
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
				Main.MACHINE.setGoalX(goalX);
				Main.MACHINE.setGoalY(goalY);
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
				Main.MACHINE = new AI(goalX, goalY, seedMin, seedMax, alpha, gamma, lambda, lambdaDecay, seedReward, 0, rewardDecay);
				Main.MACHINE.seedQTable();
				makeWalls();
				Main.MACHINE.initQTableAndBoard();
				Main.MACHINE.qtable[goalX][goalY].setReward(reward);
				if(random){
					setStartPos();
				}
				buildArrows();
				updateBoard();
				startLoop();
				started = true;
				reseting = false;
				runs = 0;
				counter = 0;
				clock = 0;
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
			startXLabel.setVisible(true);
			startYLabel.setVisible(true);
			startXField.setVisible(true);
			startXField.setFocusTraversable(true);
			startYField.setVisible(true);
			startYField.setFocusTraversable(true);
		} else {
			random = true;
			random_FixedStartButton.setText("Random");
			startXLabel.setVisible(false);
			startYLabel.setVisible(false);
			startXField.setVisible(false);
			startXField.setFocusTraversable(false);
			startYField.setVisible(false);
			startYField.setFocusTraversable(false);
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
							runs++;
							speedCounter = 0;
							if (stepping){
								stepping = false;
							}
							numberOfRunsLabel.setText("" + runs);
							//Move nextMove = new Move(X, Y);
							//moves.add(e)
						}
					}
				});
			}
		}, 0, 1);
	}
	
	
	
	void saveMap(String fileName){
		SavedMap map = new SavedMap(lambda, lambdaDecay, reward, rewardDecay, alpha, gamma, seedMin, seedMax, seedReward,
					startX, startY, goalX, goalY, counter, clock, runs, Main.MACHINE.qtable);
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
				if (goalX == r && goalY == c){
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
		//X = scrollPane.getLayoutX() + 5.0;
		//Y = scrollPane.getLayoutY() + 5.0;
		X = 5;
		Y = 5;
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				rectangles[r][c] = RectangleBuilder.create()
						.x((35 * r) + X)
						.y((35 * c) + Y)
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
				if (goalX == r && goalY == c){
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
				} else if(startX == r && startY == c){
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
		goalX = 0;
		goalXField.setText("0");
		goalY = 0;
		goalYField.setText("0");
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
		goalX = map.getGoalX();
		goalY = map.getGoalY();
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
		startX = map.getStartX();
		startY = map.getStartY();
		runs = map.getRuns();
		goalXField.setText(goalX + "");
		goalYField.setText(goalY + "");
		alphaField.setText(alpha + "");
		gammaField.setText(gamma + "");
		lambdaField.setText(lambda + "");
		lambdaDecayField.setText(lambdaDecay + "");
		rewardField.setText(reward + "");
		rewardDecayField.setText(rewardDecay +"");
		runTimerLabel.setText(clock + "");
		numberOfRunsLabel.setText(runs + "");
		
		Main.MACHINE = new AI(goalX, goalY, seedMin, seedMax, alpha, gamma, lambda, lambdaDecay, seedReward, reward, rewardDecay);
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
			input = startXField.getText();
			try {
				startX = Integer.parseInt(input);
				if (startX < 0 || startX >= Main.ROWS){
					startXField.requestFocus();
					return false;
				}
			} catch (Exception e) {
				startXField.requestFocus();
				return false;
			}
			input = startYField.getText();
			try {
				startY = Integer.parseInt(input);
				if (startY < 0 || startY >= Main.COLUMNS){
					startYField.requestFocus();
					return false;
				}
			} catch (Exception e) {
				startYField.requestFocus();
				return false;
			}
		} 
		// get goal position
		input = goalXField.getText();
		try {
			goalX = Integer.parseInt(input);
			if (goalX < 0 || goalX >= Main.ROWS){
				goalXField.requestFocus();
				return false;
			}
		} catch (Exception e) {
			goalXField.requestFocus();
			return false;
		}
		input = goalYField.getText();
		try {
			goalY = Integer.parseInt(input);
			if (goalY < 0 || goalY >= Main.COLUMNS){
				goalYField.requestFocus();
				return false;
			}
		} catch (Exception e) {
			goalYField.requestFocus();
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
			startX = num.nextInt(Main.ROWS);
			startY = num.nextInt(Main.COLUMNS);
			System.out.println("startX: " + startX + "  startY: " + startY);
			if(!Main.MACHINE.qtable[startX][startY].isGoal() || !Main.MACHINE.qtable[startX][startY].isWall()){
				Main.MACHINE.qtable[startX][startY].setColor("CORNFLOWERBLUE");
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
		System.out.println("DARKGREY= " + "DARKGREY" + " from dictionary: " + colorTable.get("DARKGREY"));
		System.out.println("BLACK= " + "BLACK" + " from dictionary: " + colorTable.get("BLACK"));
		System.out.println("LIGHTGRAY= " + "LIGHTGRAY" + " from dictionary: " + colorTable.get("LIGHTGRAY"));
		System.out.println("GOLD= " + "GOLD" + " from dictionary: " + colorTable.get("GOLD"));
		System.out.println("RED= " + "RED" + " from dictionary: " + colorTable.get("RED"));
		System.out.println("CORNFLOWERBLUE= " + "CORNFLOWERBLUE" + " from dictionary: " + colorTable.get("CORNFLOWERBLUE"));
		System.out.println("BLUEVIOLET= " + "BLUEVIOLET" + " from dictionary: " + colorTable.get("BLUEVIOLET"));
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
