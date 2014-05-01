package application;

/*
 * 	Written by Clinton Walker 2014
 *  Artificial Intelligence CS4523 Southern Polytechnic State University
 *  Project 3
 *  all rights reserved. 
 * */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class StartScreenController implements Initializable, ControlledScreen {
	ScreensController myController;
	
	@FXML 	AnchorPane	rootScreen;
	@FXML 	Button		startGame;
	@FXML	Button		exit;
	
	//==============================================================================
	// FXML methods 
	
	@FXML
	void startGameButtonPressed(ActionEvent event){
		myController.setScreen(Main.GAME_BOARD);	
		Main.PRIMARY_STAGE.setHeight(910);
		Main.PRIMARY_STAGE.setWidth(1000);
	}
	@FXML
	public void exit(ActionEvent event){
		Main.PRIMARY_STAGE.close();
		Platform.exit();
	}
	
	//==============================================================================
	// Required overrided methods
	
	@Override
	public void setScreenParent(ScreensController screenPage) {
		myController = screenPage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		rootScreen.setStyle("-fx-background-color: beige");
		Main.SSC = this;
	}

}
