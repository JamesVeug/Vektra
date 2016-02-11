package vektra.dialogs;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import vektra.SQLData;

public class LoginDialog {
	public static boolean hasConnected = false;

	public static boolean show(){
		hasConnected = false;
		
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<Pair<String, String>>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Please enter your login details!");

		// Set the icon (must be included in the project).
		//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		final ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		final TextField table = new TextField();
		table.setPromptText("Server");
		final TextField username = new TextField();
		username.setPromptText("Username");
		final PasswordField password = new PasswordField();
		password.setPromptText("Password");
		

		

		grid.add(new Label("Server:"), 0, 0);
		grid.add(table, 1, 0);
		grid.add(new Label("Username:"), 0, 1);
		grid.add(username, 1, 1);
		grid.add(new Label("Password:"), 0, 2);
		grid.add(password, 1, 2);

		// Enable/Disable login button depending on whether a username was entered.
		final Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		
		table.setText("stardrop_test");
		username.setText("idonotexist");
		password.setText("source");
		loginButton.setDisable(false);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				loginButton.setDisable(newValue.trim().isEmpty());
			}
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				table.requestFocus();
			}
			
		});

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(new Callback<ButtonType,Pair<String,String>>(){

			@Override
			public Pair<String, String> call(ButtonType dialogButton) {
				if (dialogButton == loginButtonType) {
					System.out.println("Pressed: " + username.getText() + ", " + password.getText());
			        return new Pair<String, String>(username.getText(), password.getText());
			    }
			    return null;
			}
			
		});

		// Try logging in
		Optional<Pair<String, String>> result = dialog.showAndWait();
		result.ifPresent(new Consumer<Pair<String,String>>(){


			@Override
			public void accept(Pair<String, String> pair) {
				if( SQLData.connect(table.getText(), pair.getKey(), pair.getValue()) ){
					
					hasConnected = true;
				}
				else{
					//
					// Didn't connect
					//
					System.out.println("FAILED!");
					PopupError.show("Login","Login Failed!\nPlease double check your login information.");
					
				}
			}
			
		});	
		
		
		return hasConnected;
	}
}
