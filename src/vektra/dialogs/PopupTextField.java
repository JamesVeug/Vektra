package vektra.dialogs;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupTextField {

	public static String show(String title, String text, String textFieldContent){
		TextInputDialog dialog = new TextInputDialog(textFieldContent);
		dialog.setTitle(title);
		dialog.setHeaderText(text);

		// Change Icon
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("v.jpg"));

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if( result.isPresent() ){
			return result.get();
		}
		else{
			return null;
		}
	}
}
