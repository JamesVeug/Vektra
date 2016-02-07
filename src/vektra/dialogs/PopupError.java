package vektra.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class PopupError {

	public static void show(String title, String text){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getButtonTypes().setAll(new ButtonType("Okay"));
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		alert.showAndWait();
	}
}
