package vektra;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class PopupWarningConfirmation {

	public static boolean show(String title, String text, String content){
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(text);
		alert.setContentText(content);
		
		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(yes,cancel);

		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == yes);
	}
}
