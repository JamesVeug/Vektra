package vektra.dialogs;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.media.MediaErrorEvent;
import javafx.stage.Stage;

public class PopupError {

	public static void show(String title, String text){
		List<String> errors = new ArrayList<String>();
		errors.add(text);
		
		show(title,errors);
	}

	public static void show(String title, List<String> errorMessages) {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.getButtonTypes().setAll(new ButtonType("Okay"));
				alert.setTitle(title);
				alert.setHeaderText(null);
				
				if( errorMessages.size() > 1 ){
					String error = "";
					for(int i = 0; i < errorMessages.size(); i++){
						error = "#" + (i+1) + "\t" + errorMessages.get(i); 
					}
					alert.setContentText(error);
				}
				else{
					// Just display the error
					alert.setContentText(errorMessages.get(0));
				}

				// Change Icon
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("v.jpg"));

				alert.showAndWait();
			}
			
		});
		

		Platform.runLater(t);
	}
}
