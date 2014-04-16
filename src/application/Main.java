package application;
	
/*
 * 	Written by Clinton Walker 2014
 * */

import java.io.File;
import java.io.IOException;

import Model.AI;

import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;



public class Main extends Application {
	
	final 	static 	String 						START_SCREEN 			= "StartScreen";
	final 	static 	String 						START_SCREEN_FXML		= "StartScreen.fxml";
	final 	static 	String 						GAME_BOARD 				= "GameBoard";
	final 	static 	String 						GAME_BOARD_FXML			= "GameBoard.fxml";
	public	static	StartScreenController		SSC;
	public	static 	GameBoardController			GBC;
	public  static	String						FILEPATH;
	public  static  int							ROWS					= 20;
	public  static  int							COLUMNS					= 20;
			static	Stage						PRIMARYSTAGE_STAGE;
			static  Scene						mainScene;
			static	Group						root;
			static  AI 							machine;
	
	//==============================================================================
			
	@Override
	public void start(Stage primaryStage) {
		PRIMARYSTAGE_STAGE = primaryStage;
		
		boolean startLoad, gameLoad;
		ScreensController mainContainer = new ScreensController();
		System.out.println("toStringcall::: " + mainContainer.toString());
		
		startLoad 	= mainContainer.loadScreen(Main.START_SCREEN, Main.START_SCREEN_FXML);
		gameLoad	= mainContainer.loadScreen(Main.GAME_BOARD, Main.GAME_BOARD_FXML);
		
		System.out.println("toStringcall::: " + mainContainer.toString());
		System.out.println("Start loaded? " + startLoad + " gameloaded? " + gameLoad);
		
		mainContainer.setScreen(Main.START_SCREEN);
		PRIMARYSTAGE_STAGE.setHeight(420);
		PRIMARYSTAGE_STAGE.setWidth(800);
		//PRIMARYSTAGE_STAGE.setResizable(false);
		root = new Group();
		root.getChildren().addAll(mainContainer);
		Scene scene = new Scene(root);
		mainScene = scene;
		primaryStage.setScene(scene);
		primaryStage.show();
		
		File file1 = new File(".");  
		try {
			System.out.println("Current dir : " + file1.getCanonicalPath());
			String path = file1.getCanonicalPath();
			System.out.println("the path is " + path);
			FILEPATH = path;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("problem getting canonical path");
		}
		
		machine = new AI();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
