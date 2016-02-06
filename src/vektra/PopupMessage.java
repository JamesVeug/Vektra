package vektra;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class PopupMessage {

	public static void show(String title, String text){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getButtonTypes().setAll(new ButtonType("Okay"));
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		alert.showAndWait();
	}
}
