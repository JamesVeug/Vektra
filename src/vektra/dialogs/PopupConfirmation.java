package vektra.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class PopupConfirmation {

	public static boolean show(String title, String text){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		ButtonType yes = new ButtonType("Ok");
		ButtonType cancel = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(yes,cancel);
		return alert.showAndWait().get() == yes;
	}
}
