package vektra.extrawindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vektra.Vektra;

public class AboutWindow {

	public static void show() {
		
		final Stage stage = new Stage();
		stage.setTitle("About");
		stage.getIcons().add(new Image("v.jpg"));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.getStylesheets().add("css/custom.css");
		layout.getStyleClass().add("aboutinfo");
		
		
		VBox iconPane = new VBox();
		iconPane.setPadding(new Insets(10));
		iconPane.setAlignment(Pos.CENTER);
			ImageView view = new ImageView(new Image("v.jpg"));
		iconPane.getChildren().add(view);
		
		
		VBox infoPane = new VBox();
		infoPane.setPadding(new Insets(10));
		infoPane.setAlignment(Pos.CENTER);
		
			Node[] rows = {new Label("Vektra-Bug Reporter"),
							new Label("Version: " + Vektra.VERSION),
							new Label("Copywrited by Vektra 2016"),
							new Label(""),
							new Label("Report issues to:"),
							new Label("james.veugelaers@outlook.co.nz")
			};
		
		
			// Add types
			for(Node n : rows){
				n.getStyleClass().add("aboutinfo");
			}
		infoPane.getChildren().addAll(rows);
		
		layout.getChildren().addAll(iconPane, infoPane);
		
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

}
