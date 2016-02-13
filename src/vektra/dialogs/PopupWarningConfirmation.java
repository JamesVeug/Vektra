package vektra.dialogs;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupWarningConfirmation {

	public static boolean show(String title, String text, String content){
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(text);
		alert.setContentText(content);

		// Change Icon
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("v.jpg"));
		
		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(yes,cancel);

		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == yes);
	}
}
