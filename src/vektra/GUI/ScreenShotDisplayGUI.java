package vektra.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import vektra.Vektra;

public class ScreenShotDisplayGUI {

	public static GridPane create(Stage primaryStage, Vektra vektra) {
		GridPane screenshotinfo = new GridPane();
		screenshotinfo.setPadding(new Insets(10,0,0,10));
		screenshotinfo.getStylesheets().add("css/custom.css");
		screenshotinfo.setHgap(20);
		GridPane.isFillHeight(screenshotinfo);
		
		Rectangle priorityIndicator = new Rectangle();
		priorityIndicator.setFill(Color.BLACK);
		priorityIndicator.setStroke(Color.BLACK);
		priorityIndicator.setWidth(10);
		priorityIndicator.setHeight(10);
		vektra.setPriorityIndicator(priorityIndicator);

		screenshotinfo.addColumn(0, priorityIndicator);
		screenshotinfo.addRow(1, new Label());
		screenshotinfo.addRow(2, new Label());
		screenshotinfo.addRow(3, new Label());
		
		Label reportIDLabel = new Label("REPORT ID:");
		reportIDLabel.getStyleClass().add("reportidheader");
		screenshotinfo.addColumn(1, reportIDLabel);
		
		Label reportID = new Label("-");
		reportID.getStyleClass().add("reportid");
		screenshotinfo.addColumn(2, reportID);
		vektra.setReportID(reportID);
		
		Label tagLabel = new Label("TAGS:");
		tagLabel.setPrefHeight(15);
		tagLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(1, tagLabel);
		
		Label tags = new Label("-");
		tags.setPrefHeight(15);
		tags.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, tags);
		vektra.setTags(tags);
		
		Label priorityLabel = new Label("PRIORITY:");
		priorityLabel.setPrefHeight(30);
		priorityLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(2, priorityLabel);
		
		Label priority = new Label("-");
		priority.setPrefHeight(15);
		priority.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, priority);
		vektra.setPriority(priority);
		
		Label versionLabel = new Label("VERSION:");
		versionLabel.setPrefHeight(15);
		versionLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(3, versionLabel);
		
		Label version = new Label("-");
		version.setPrefHeight(15);
		version.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, version);
		vektra.setVersion(version);
		
		
		StackPane screenshotPane = new StackPane();
		screenshotPane.setStyle("-fx-border-width: 1; -fx-border-color: white;");
//		screenshotPane.setPrefHeight(325);		
		screenshotPane.setPadding(new Insets(5));		
			Image logo = new Image("logo.png");
			ImageView displayScreenshot = new ImageView(logo);
			displayScreenshot.setPreserveRatio(true);
			vektra.setDisplayScreenshot(displayScreenshot);
		screenshotPane.getChildren().add(displayScreenshot);
		
		StackPane.setAlignment(screenshotPane, Pos.CENTER);
		
		// Bottom Middle
		GridPane screenshotListPane = new GridPane();
		screenshotListPane.setPrefWidth(600);
		screenshotListPane.setPrefHeight(200);
			
			Label openScreenshotsLabel = new Label("Open Screenshots");
			openScreenshotsLabel.getStyleClass().add("openScreenShots");
			openScreenshotsLabel.setPrefWidth(200);
			
			Label openScreenshots = new Label("(0)");
			openScreenshots.setAlignment(Pos.CENTER_LEFT);
			openScreenshots.getStyleClass().add("openScreenShots");
			vektra.setOpenScreenshots(openScreenshots);
		screenshotListPane.addRow(0, openScreenshotsLabel);
		screenshotListPane.addColumn(1, openScreenshots);
		GridPane.setHalignment(openScreenshots, HPos.LEFT);
	
			// List of Screenshots to be displayed 
			FlowPane screenshotList = new FlowPane();
			//screenshotList.setPrefHeight(400);
			screenshotList.setVgap(8);
			screenshotList.setHgap(4);
		screenshotListPane.addRow(1, screenshotList);
		vektra.setScreenshotList(screenshotList);

		GridPane screenshotLayout = new GridPane();
		screenshotLayout.setPadding(new Insets(0,0,0,5));
		screenshotLayout.addRow(0, screenshotinfo);
		screenshotLayout.addRow(1, screenshotPane);
		screenshotLayout.addRow(2, screenshotListPane);

		GridPane.setHgrow(screenshotLayout, javafx.scene.layout.Priority.ALWAYS);
		GridPane.setVgrow(screenshotLayout, javafx.scene.layout.Priority.ALWAYS);
		
		return screenshotLayout;
	}

}
