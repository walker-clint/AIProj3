package application;

/*
 * Code skeleton found at:
 * http://stackoverflow.com/questions/16349877/javafx-2-0-fxml-child-windows
 * 
 * Modifications written by: Clinton Walker
*/
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessageBoxController implements Initializable {

	@FXML	AnchorPane		rootScreen;
	@FXML	TextField		popupFileNameField;
	
	@FXML	Label			fileNameLabel;
	
	@FXML	Button			yesButton;
	@FXML	Button			noButton;
	@FXML	Button			popupSaveMapButton;

	private Stage myParent;
	public	Stage messageBoxStage;

	public void showMessageBox(Stage parentStage) {
		this.myParent = parentStage;

		try {
			messageBoxStage = new Stage();
			AnchorPane page = (AnchorPane) FXMLLoader
					.load(MessageBoxController.class
							.getResource(Main.SAVE_POPUP_FXML));
			Scene scene = new Scene(page);
			messageBoxStage.setScene(scene);
			messageBoxStage.setTitle("Warning");
			messageBoxStage.initOwner(this.myParent);
			messageBoxStage.initModality(Modality.WINDOW_MODAL);
			messageBoxStage.show();
		} catch (Exception ex) {
			System.out.println("Exception foundeth in showMessageBox");
			ex.printStackTrace();
		}
	}

	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
		
		rootScreen.setStyle("-fx-background-color: orange");
	}

	@FXML	
	public void yesButtonPressed(ActionEvent event){
		yesButton.setVisible(false);
		fileNameLabel.setVisible(true);
		popupSaveMapButton.setVisible(true);
		popupFileNameField.setVisible(true);
		popupFileNameField.requestFocus();
		noButton.setText("Cancel");
	}
	
	@FXML
	public void noButtonPressed(ActionEvent event){
		Main.GBC.loadMap();
	}
	
	@FXML
	public void saveMapButtonPressed(ActionEvent event){
		if (popupFileNameField.getText().equals("") || popupFileNameField.getText().equals("Enter File Name")){
			popupFileNameField.requestFocus();
			popupFileNameField.setText("Enter File Name");
		} else {
			Main.GBC.saveMap(popupFileNameField.getText());
		}
	}
	
	

}