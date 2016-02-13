package vektra.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupMessage {

	public static void show(String title, String text){
		ButtonType okay = new ButtonType("Okay");
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getButtonTypes().setAll(okay);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		// Change Icon
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("v.jpg"));

		// Focus button
		Button okButton = (Button)alert.getDialogPane().lookupButton(okay);
		okButton.setDefaultButton(true);
		
		// Show dialog
		alert.showAndWait();
	}
}
