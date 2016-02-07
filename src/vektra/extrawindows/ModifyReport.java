package vektra.extrawindows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vektra.BugItem;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;

public class ModifyReport {
	protected static Stage primaryStage;
	
	protected static TextArea text;
	protected static final String exampleText = "Type stuff in here";

	protected static CheckBox GAMEPLAY;
	protected static CheckBox VISUAL;
	protected static CheckBox AUDIO;
	protected static CheckBox BREAKING;
	
	protected static TextField enterLink;
	protected static HBox screenshotList;
	private static Map<String,Image> images;
	private static Map<Image,String> links;
	
	protected static ToggleGroup priorityGroup;
	protected static RadioButton LOW;
	protected static RadioButton MEDIUM;
	protected static RadioButton HIGH;
	
	
	protected static ComboBox<String> statusSelection;
	protected static GridPane bottomPane;
	
	protected static int bugID;

	public static void display(int maxID) {
		bugID = maxID;
		Stage stage = new Stage();
		stage.setTitle("Create Report");
		stage.setWidth(800);
		stage.setHeight(600);
		stage.getIcons().add(new Image("v.jpg"));
		
		VBox mainLayout = new VBox();
		mainLayout.getStylesheets().add("css/custom.css");
		
		GridPane headerPane = new GridPane();
		headerPane.setPrefHeight(30);
		headerPane.getStyleClass().add("createReport_Header");
			Label reportLabel = new Label("REPORT ID: ");
			Label reportIDLabel = new Label(String.valueOf(maxID));
		headerPane.addColumn(0, reportLabel);
		headerPane.addColumn(1, reportIDLabel);
		
		
		GridPane optionPane = new GridPane();
		//optionPane.setPrefHeight(125);
		optionPane.getStyleClass().add("createReport_Options");
		optionPane.setHgap(10); //horizontal gap in pixels => that's what you are asking for
		optionPane.setVgap(10); //vertical gap in pixels
		optionPane.setPadding(new Insets(10, 10, 10, 10)); //margins around the whole grid
			Label priorityLabel = new Label("PRIORITY: ");
			priorityLabel.getStyleClass().add("createReport_Options_Headers");
			optionPane.addRow(0, priorityLabel);
			
			LOW = new RadioButton(); 
			Label LOWLabel = new Label("LOW");
			LOWLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(1, LOW);
			optionPane.addColumn(2, LOWLabel);
			
			MEDIUM = new RadioButton ();
			Label MEDIUMLabel = new Label("MEDIUM");
			MEDIUMLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(3, MEDIUM);
			optionPane.addColumn(4, MEDIUMLabel);
			
			HIGH = new RadioButton ();
			Label HIGHLabel = new Label("HIGH");
			HIGHLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(5, HIGH);
			optionPane.addColumn(6, HIGHLabel);
			optionPane.addColumn(7, new Pane());
			optionPane.addColumn(8, new Pane());
			priorityGroup = new ToggleGroup();
			priorityGroup.getToggles().addAll(LOW,MEDIUM,HIGH);
			
			Label tagLabel = new Label("TAG AS: ");
			tagLabel.getStyleClass().add("createReport_Options_Headers");
			optionPane.addRow(1, tagLabel);
			
			GAMEPLAY = new CheckBox();
			Label GAMEPLAYLabel = new Label("GAMEPLAY");
			GAMEPLAYLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(1, GAMEPLAY);
			optionPane.addColumn(2, GAMEPLAYLabel);
			
			VISUAL = new CheckBox();
			Label VISUALLabel = new Label("VISUAL");
			VISUALLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(3, VISUAL);
			optionPane.addColumn(4, VISUALLabel);
			
			AUDIO = new CheckBox();
			Label AUDIOLabel = new Label("AUDIO");
			AUDIOLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(5, AUDIO);
			optionPane.addColumn(6, AUDIOLabel);
			
			BREAKING = new CheckBox();
			Label BREAKINGLabel = new Label("BREAKING");
			BREAKINGLabel.getStyleClass().add("createReport_Options_Text");
			optionPane.addColumn(7, BREAKING);
			optionPane.addColumn(8, BREAKINGLabel);
			
			images = new HashMap<String,Image>();
			links = new HashMap<Image,String>();
			GridPane screenShotPane  = new GridPane();
			screenShotPane.setVgap(5); //vertical gap in pixels
			screenShotPane.setPadding(new Insets(10, 10, 10, 10)); //margins around the whole grid
			
				GridPane screenShotUploadPane  = new GridPane();
				screenShotPane.setHgap(5); //vertical gap in pixels
				Label screenshotLabel = new Label("ADD SCREENSHOT(S): ");
				screenshotLabel.getStyleClass().add("createReport_Options_Headers");
				screenShotUploadPane.addRow(0, screenshotLabel);
				
				Button openScreenshotButton = new Button("OPEN");
				openScreenshotButton.getStyleClass().add("button_extra");
				openScreenshotButton.setPrefWidth(100);
				screenShotUploadPane.addColumn(1, openScreenshotButton);
				
				screenshotList = new HBox();
				screenshotList.setStyle("-fx-border-color: #ECECEC;");
				screenshotList.setPrefSize(500,100);
				screenshotList.setSpacing(10);
				
				Label linkLabel = new Label("  LINK: ");
				linkLabel.getStyleClass().add("createReport_Options_Headers");
				screenShotUploadPane.addColumn(2, linkLabel);
				
				enterLink = new TextField();
				enterLink.setPrefWidth(200);
				screenShotUploadPane.addColumn(3, enterLink);
				
				Button uploadScreenshotButton = new Button("UPLOAD");
				uploadScreenshotButton.getStyleClass().add("button_extra");
				uploadScreenshotButton.setPrefWidth(100);
				uploadScreenshotButton.setOnAction(new UploadLinkButtonPressed());
				screenShotUploadPane.addColumn(4, uploadScreenshotButton);
				
			screenShotPane.addRow(0, screenShotUploadPane);
			screenShotPane.addRow(1, screenshotList);
			
			GridPane reportDescriptionpane = new GridPane();
				reportDescriptionpane.setPadding(new Insets(0, 10, 0, 10)); //margins around the whole grid
				Label reportDescriptionLabel = new Label("REPORT DESCRIPTION: ");
				reportDescriptionLabel.getStyleClass().add("createReport_Options_Headers");
			reportDescriptionpane.getChildren().add(reportDescriptionLabel);
		
		bottomPane = new GridPane();
		bottomPane.setPadding(new Insets(10, 10, 0, 10)); //margins around the whole grid
			text = new TextArea();
			text.setPromptText("Server");
			text.setPrefHeight(300);
			text.getStyleClass().add("createReport_Message");
		bottomPane.addRow(0,text);
		//pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("pink"), new CornerRadii(0), new Insets(0))));
		
			Pane statusPane = new Pane();
			statusPane.getStyleClass().add("createReport_Message");
			//statusPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), new CornerRadii(0), new Insets(0))));
			
				HBox statusInnerPane = new HBox();
					statusInnerPane.setPadding(new Insets(5)); //margins around the whole grid
					statusInnerPane.getStyleClass().add("createReport_Options");
					statusInnerPane.setPrefHeight(10);
					//statusInnerPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("black"), new CornerRadii(0), new Insets(0))));
					statusInnerPane.setAlignment(Pos.CENTER_LEFT);
					statusInnerPane.setSpacing(10);
				
					Label statusLabel = new Label("STATUS:");
					statusLabel.getStyleClass().add("createReport_Options_Headers");
				statusInnerPane.getChildren().add(statusLabel);
					
					statusSelection = new ComboBox<String>();
					statusSelection.getItems().addAll("Pending","WIP","Fixed");
					statusSelection.getStyleClass().add("createReport_Options_Text");
					statusSelection.setValue("Pending");
				statusInnerPane.getChildren().add(statusSelection);
			statusPane.getChildren().add(0,statusInnerPane);			
		bottomPane.addRow(1,statusInnerPane);
		
		
			
		
		mainLayout.getChildren().add(headerPane);
		mainLayout.getChildren().add(optionPane);
		mainLayout.getChildren().add(screenShotPane);
		mainLayout.getChildren().add(reportDescriptionpane);
		mainLayout.getChildren().add(bottomPane);
		mainLayout.getChildren().add(statusPane);
		
		
		
		Scene scene = new Scene(mainLayout);
		stage.setScene(scene);
		stage.show();
		
		primaryStage = stage;
	}
	
	protected static void addImage(String link, Image image){
		images.put(link, image);
		links.put(image, link);
		
		ImageView v = new ImageView(image);
		v.setOnMouseClicked(new ImageClickedListener());
		v.setFitWidth(100);
		v.setFitHeight(100);
		screenshotList.getChildren().add(v);
	}

	protected static BugItem getBug(){
		return new BugItem(bugID, getSelectedTags(), getPriority(), statusSelection.getValue(), null, text.getText(), null, images);
	}

	
	protected static void addImages(Map<String, Image> imageMap) {
		for(Entry<String, Image> p : imageMap.entrySet()){
			addImage(p.getKey(),p.getValue());
		}
	}
	
	protected static void setConfirmButton(Node createReport) {
		
		bottomPane.addColumn(0,createReport);
		GridPane.setHalignment(createReport, HPos.RIGHT);
		GridPane.setValignment(createReport, VPos.CENTER);
	}
	
	protected static String getPriority() {
		if( priorityGroup.getSelectedToggle() == LOW ){
			return "LOW";
		}
		else if( priorityGroup.getSelectedToggle() == MEDIUM ){
			return "MEDIUM";
		}
		else if( priorityGroup.getSelectedToggle() == HIGH ){
			return "HIGH";
		}
		return "UNSELECTED";
	}

	protected static Set<String> getSelectedTags() {
		Set<String> tags = new HashSet<String>();
		
		if( GAMEPLAY.isSelected() ){
			tags.add("GAMEPLAY");
		}
		if( VISUAL.isSelected() ){
			tags.add("VISUAL");
		}
		if( AUDIO.isSelected() ){
			tags.add("AUDIO");
		}
		if( BREAKING.isSelected() ){
			tags.add("BREAKING");
		}
		
		
		
		return tags;
	}
	
	protected static class ImageClickedListener implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent arg0) {
			ImageView v = (ImageView)arg0.getSource();
			boolean delete = PopupConfirmation.show("Remove Image", "Are you sure you want to remove this screenshot form the report?");
			if( delete ){
				String link = links.get(v);
				images.remove(link);
				links.remove(v);
				screenshotList.getChildren().remove(v);
			}
		}
		
	}

	protected static class UploadLinkButtonPressed implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String link = enterLink.getText();	
			System.out.println("Link: " + link);
			
			if( images.keySet().contains(link) ){
				PopupError.show("Can not upload image", "Link already uploaded!");
				return;
			}
			
			try{
				Image image = new Image(link);
				addImage(link,image);
				enterLink.setText("");
			}catch(IllegalArgumentException e){
				PopupError.show("Could not load image", "The provided link can not be converted to an image!");
				System.out.println("Can not load image '" + link + "'");
			}
			
		}

	}
}
