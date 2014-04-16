package application;

/*
 * 	Written by Clinton Walker 2014
 *  
 * */


import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import Model.AI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

public class GameBoardController implements Initializable, ControlledScreen {
	
	//==============================================================================
	// variables
	
	ScreensController 		myController;
	
	@FXML 	AnchorPane		rootScreen;
	@FXML	Pane			pane;
	
	@FXML	Label			resultQCompleteLabel;
	@FXML	Label			currentQ0Label;
	@FXML	Label			currentRewardLabel;
	@FXML	Label			nextStateQLabel;
	@FXML	Label			currentQ1Label;
	@FXML	Label			startXLabel;
	@FXML	Label			startYLabel;
	@FXML	Label			runTimerLabel;
	@FXML	Label			numberOfRunsLabel;
	
	@FXML	TextField		lambdaField;
	@FXML	TextField		lambdaDecayField;
	@FXML	TextField		rewardField;
	@FXML	TextField		rewardDecayField;
	@FXML	TextField		alphaField;
	@FXML	TextField		gammaField;
	@FXML	TextField		startXField;
	@FXML	TextField		startYField;
	@FXML	TextField		goalXField;
	@FXML	TextField		goalYField;
	@FXML	TextField		rotateField;
	@FXML	TextField		XField;
	@FXML	TextField		YField;
	@FXML	TextField		SField;
	
	@FXML	Button			pauseButton;
	@FXML	Button			stepButton;
	@FXML	Button			random_FixedStartButton;
	@FXML	Button			startButton;
	@FXML	Button			stopButton;
	@FXML	Button			exitButton;
	@FXML	Button			speedButton;
	
	@FXML 	Rectangle		newRectangle;
	
	
	private	Timer 			timer;
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
							
	private Rectangle[][]	rectangles;
	public 	Polygon[][]		arrows;
	private Pane 			content;
	
	//==============================================================================
	// FXML Methods
	
	@FXML
	void startPressed(ActionEvent event){
		if(started){
		} else {
			if (getInput()){
				pauseButton.setVisible(true);
				startButton.setVisible(false);
				stopButton.setVisible(true);
				stepButton.setVisible(false);
				speedButton.setVisible(true);
				Main.machine = new AI(goalX, goalY, seedMin, seedMax, alpha, lambda, lambdaDecay, seedReward, reward);
				Main.machine.seedQTable();
				buildArrows();
				startLoop();
				started = true;
			} 
		}
	}
	
	@FXML 
	void buildRects(ActionEvent event){
		buildRectangles();
	}
	
	@FXML
	void buildArrs(ActionEvent event){
		if (arrows != null){
			deleteArrows(event);
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
		if(started){
			reset();
			startButton.setVisible(true);
			stopButton.setVisible(false);
			pauseButton.setVisible(false);
			stepButton.setVisible(false);
			started = false;
		}
	}
	
	@FXML
	public void exitPressed(ActionEvent event){
		reset();
		myController.setScreen(Main.START_SCREEN);	
		Main.PRIMARYSTAGE_STAGE.setHeight(420);
		Main.PRIMARYSTAGE_STAGE.setWidth(815);
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
							rectangles[r][c].setFill(Color.RED);
							r++;
							if (r >= Main.ROWS){
								r = 0;
								c++;
							}
							if (c >= Main.COLUMNS){
								c = 0;
							}
						}
					}
				});
			}
		}, 0, 1);
	}
	
	void setQtableColors(){
		X = 5;
		Y = 5;
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				Main.machine.setColor(r, c, (Color)rectangles[r][c].getFill());
			}
		}
	}
	
	void updateBoard(){
		X = Double.parseDouble(XField.getText());
		Y = Double.parseDouble(YField.getText());
		double s = Double.parseDouble(SField.getText());
		double rotation = Double.parseDouble(rotateField.getText());
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalX == r && goalY == c){
					Main.machine.setColor(r, c, (Color)rectangles[r][c].getFill());
				} else {
					arrows[r][c].setRotate(Main.machine.qtable[r][c].getDirection());
					arrows[r][c].setScaleX(.5 * Main.machine.qtable[r][c].getScaleFactor());
					arrows[r][c].setScaleY(.5 * Main.machine.qtable[r][c].getScaleFactor());
					arrows[r][c].setFill(Main.machine.qtable[r][c].getColor());
					
					
					arrows[r][c].setLayoutX((35 * r) + X); //remove after testing
					arrows[r][c].setLayoutY((35 * c) + Y); //remove after testing
					
				}
			}
		}
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
		X = Double.parseDouble(XField.getText());
		Y = Double.parseDouble(YField.getText());
		//X = 20;
		//Y = 30;
		double starX = -14.0;
		double starY = -113.0;
		double s = Double.parseDouble(SField.getText());
		double rotation = Double.parseDouble(rotateField.getText());
		arrows = new Polygon[Main.ROWS][Main.COLUMNS];
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
				if (goalX == r && goalY == c){
					arrows[r][c] = new Polygon();
					arrows[r][c].getPoints().addAll(new Double[] {35.0, 120.5, 37.9, 129.1, 46.9, 129.1, 39.7, 134.5, 42.3, 143.1,
							35.0, 139.0, 27.7, 143.1, 30.3, 134.5, 23.1, 129.1, 32.1, 129.1});
					arrows[r][c].setFill(Color.GOLD);
					arrows[r][c].setRotate(rotation);
					arrows[r][c].setScaleX(1 * s);
					arrows[r][c].setScaleY(1 * s );
					arrows[r][c].setLayoutX((35 * r) + starX);
					arrows[r][c].setLayoutY((35 * c) + starY);
					rectangles[r][c].setFill(Color.CORNFLOWERBLUE);
					} else {
					arrows[r][c] = new Polygon();
					arrows[r][c].getPoints().addAll(new Double[] {-40.0, 40.0, 40.0, 40.0, 0.0, -60.0});
					arrows[r][c].setFill(Color.BLACK);
					arrows[r][c].setRotate(rotation);
					arrows[r][c].setScaleX(.15 * s);
					arrows[r][c].setScaleY(.15 * s );
					arrows[r][c].setLayoutX((35 * r) + X);
					arrows[r][c].setLayoutY((35 * c) + Y);
				}
				pane.getChildren().add(arrows[r][c]);
			}
		}
		
	}
	
	void deleteArrows(ActionEvent event){
		for (int c = 0; c < Main.COLUMNS; c++){
			for (int r = 0; r < Main.ROWS; r++){
			
				pane.getChildren().removeAll(arrows[r][c]);
			}
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
	}
	
	boolean getInput(){
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
	
	//==============================================================================
	// getters and setters
	
	
	
	
	
	
	
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
	}

}
