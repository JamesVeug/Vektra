package vektra.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PopupWarning {

	public static void show(String title, String text, String content){
		List<String> contentList = new ArrayList<String>();
		contentList.add(content);
		
		show(title, text, contentList);
	}

	public static void show(String title, String text, List<String> errorMessages) {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle(title);
				alert.setHeaderText(text);
				
				String message = "";
				for(int i = 0; i < errorMessages.size(); i++){
					message += "#" + (i+1) + " " + errorMessages.get(i) + "\n";
				}
				alert.setContentText(message);
		
				// Change Icon
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("v.jpg"));
				
				ButtonType okay = new ButtonType("Okay");
				alert.getButtonTypes().setAll(okay);
		
				alert.show();				
			}
		});
		Platform.runLater(t);
//		Optional<ButtonType> result = alert.showAndWait();
		//return (result.get() == yes);
	}
}
