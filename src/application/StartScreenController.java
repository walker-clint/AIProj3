package application;

import java.net.URL;
import java.util.ResourceBundle;

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
	
	
	@FXML
	void startGameButtonPressed(ActionEvent event){
		myController.setScreen(Main.GAME_BOARD);	
		Main.PRIMARYSTAGE_STAGE.setHeight(910);
		Main.PRIMARYSTAGE_STAGE.setWidth(1000);
	}
	@FXML
	public void exit(ActionEvent event){
		Main.PRIMARYSTAGE_STAGE.close();
	}
	
	//==============================================================================
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
