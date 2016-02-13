package vektra.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupConfirmation {

	public static boolean show(String title, String text){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		// Change Icon
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("v.jpg"));

		ButtonType yes = new ButtonType("Ok");
		ButtonType cancel = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(yes,cancel);
		return alert.showAndWait().get() == yes;
	}
}
