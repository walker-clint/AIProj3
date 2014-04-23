package application;
	
/*
 /*
 * 	Written by Clinton Walker 2014
 *  Artificial Intelligence CS4523 Southern Polytechnic State University
 *  Project 3
 *  all rights reserved. 
 * */
import java.io.File;
import java.io.IOException;
import model.AI;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;



public class Main extends Application {
	
	final 	static 	String 						START_SCREEN 			= "StartScreen";
	final 	static 	String 						START_SCREEN_FXML		= "StartScreen.fxml";
	final 	static 	String 						GAME_BOARD 				= "GameBoard";
	final 	static 	String 						GAME_BOARD_FXML			= "GameBoard.fxml";
	final	static	String						SAVE_POPUP_FXML			= "savePopUP.fxml";
	public	static	StartScreenController		SSC;
	public	static 	GameBoardController			GBC;
	public  static	String						FILE_PATH;
	public  static  int							ROWS					= 20;
	public  static  int							COLUMNS					= 20;
	public  static  double						SCALE_ADJUSTMENT_FACTOR	= 1;
	public  static  MessageBoxController		POPUP_WINDOW			= new MessageBoxController();
			static	Stage						PRIMARY_STAGE;
			static  Scene						MAIN_SCENE;
			static	Group						ROOT;
			static  AI 							MACHINE;
	
	//==============================================================================
			
	@Override
	public void start(Stage primaryStage) {
		PRIMARY_STAGE = primaryStage;
		
		boolean startLoad, gameLoad;
		ScreensController mainContainer = new ScreensController();
		System.out.println("toStringcall::: " + mainContainer.toString());
		
		startLoad 	= mainContainer.loadScreen(Main.START_SCREEN, Main.START_SCREEN_FXML);
		gameLoad	= mainContainer.loadScreen(Main.GAME_BOARD, Main.GAME_BOARD_FXML);
		
		System.out.println("toStringcall::: " + mainContainer.toString());
		System.out.println("Start loaded? " + startLoad + " gameloaded? " + gameLoad);
		
		mainContainer.setScreen(Main.START_SCREEN);
		PRIMARY_STAGE.setHeight(420);
		PRIMARY_STAGE.setWidth(800);
		//PRIMARYSTAGE_STAGE.setResizable(false);
		ROOT = new Group();
		ROOT.getChildren().addAll(mainContainer);
		Scene scene = new Scene(ROOT);
		MAIN_SCENE = scene;
		primaryStage.setScene(scene);
		primaryStage.show();
		
		File file1 = new File(".");  
		try {
			System.out.println("Current dir : " + file1.getCanonicalPath());
			String path = file1.getCanonicalPath();
			System.out.println("the path is " + path);
			FILE_PATH = path;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("problem getting canonical path");
		}
		
		MACHINE = new AI();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
