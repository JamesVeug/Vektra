package vektra.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupError {

	public static void show(String title, String text){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getButtonTypes().setAll(new ButtonType("Okay"));
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		// Change Icon
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("v.jpg"));

		alert.showAndWait();
	}
}
