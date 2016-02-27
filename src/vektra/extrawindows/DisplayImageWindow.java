package vektra.extrawindows;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayImageWindow {
	private static List<Stage> windows = new ArrayList<Stage>();

	public static void setup(){
		// Do nothing but allow the variable to be declared
	}
	
	public static void show(Stage primaryStage, ImageView displayScreenshot) {
		final Stage stage = new Stage();
		stage.setOnCloseRequest((a)->{windows.remove(stage);});
		windows.add(stage);
		
		stage.setTitle("Display Screenshot");
		stage.getIcons().add(new Image("v.jpg"));
		stage.setResizable(true);
		
		stage.setWidth(Math.min(primaryStage.getWidth(), displayScreenshot.getImage().getWidth()));
		stage.setHeight(Math.min(primaryStage.getHeight(), displayScreenshot.getImage().getHeight()));
		
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

	/**
	 * Closes all of the DisplayImageWindow's that are currently open
	 */
	public static void closeAllWindows(){
		System.out.println("Windows Open: " + windows.size());
		for(Stage s : windows){
			System.out.println("\tClosing a window");
			if( s.isShowing() ){
				s.close();
			}
		}
	}
}
