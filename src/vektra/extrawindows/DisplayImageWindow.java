package vektra.extrawindows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayImageWindow {

	public static void show(Stage primaryStage, ImageView displayScreenshot) {
		final Stage stage = new Stage();
		stage.setTitle("Display Screenshot");
		stage.getIcons().add(new Image("v.jpg"));
		stage.setResizable(true);
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.getStylesheets().add("css/custom.css");
		layout.getStyleClass().add("aboutinfo");
		
		ImageView copiedImage = new ImageView(displayScreenshot.getImage());
		copiedImage.setPreserveRatio(true);
		copiedImage.fitWidthProperty().bind(layout.widthProperty());
		copiedImage.fitHeightProperty().bind(layout.heightProperty());
		layout.getChildren().add(copiedImage);
		
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

}
