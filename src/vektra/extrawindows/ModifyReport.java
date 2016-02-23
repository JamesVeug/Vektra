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
import javafx.stage.Modality;
import javafx.stage.Stage;
import vektra.BugImage;
import vektra.BugItem;
import vektra.Priority;
import vektra.Status;
import vektra.Tag;
import vektra.Version;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;
import vektra.resources.OnlineResources;

public class ModifyReport {
	protected static Stage primaryStage;
	
	protected static TextArea text;
	protected static final String exampleText = "Type stuff in here";
	protected static final String exampleVersion = "v0.11b";
	protected static final String exampleLink = "http://tinyurl.com/ml7lv4o";

	protected static CheckBox GAMEPLAY;
	protected static CheckBox VISUAL;
	protected static CheckBox AUDIO;
	protected static CheckBox BREAKING;
	
	protected static TextField enterLink;
	protected static HBox screenshotList;
	private static Map<String,BugImage> bugimages;
	private static Map<Image,String> images;
	
	protected static ToggleGroup priorityGroup;
	protected static RadioButton LOW;
	protected static RadioButton MEDIUM;
	protected static RadioButton HIGH;
	

	protected static ComboBox<vektra.Stage> stageVersion;
	protected static TextField version;
	protected static ComboBox<Status> statusSelection;
	protected static GridPane bottomPane;
	
	protected static int bugID;

	public static void display(String title, int maxID) {
		bugID = maxID;
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setWidth(800);
		stage.setHeight(600);
		stage.getIcons().add(new Image("v.jpg"));
		stage.initModality(Modality.APPLICATION_MODAL);
		
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
			
			bugimages = new HashMap<String,BugImage>();
			images = new HashMap<Image,String>();
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
				openScreenshotButton.setDisable(true);
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
				enterLink.setPromptText(exampleLink);
				enterLink.getStyleClass().add("createReport_Options_Text");
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
			text.setPromptText(exampleText);
			text.setPrefHeight(300);
			text.getStyleClass().add("createReport_Message");
		bottomPane.addRow(0,text);
		
			Pane statusPane = new Pane();
			statusPane.getStyleClass().add("createReport_Message");
			
				HBox statusInnerPane = new HBox();
					statusInnerPane.setPadding(new Insets(5)); //margins around the whole grid
					statusInnerPane.getStyleClass().add("createReport_Options");
					statusInnerPane.setPrefHeight(10);
					statusInnerPane.setAlignment(Pos.CENTER_LEFT);
					statusInnerPane.setSpacing(10);
				
					Label statusLabel = new Label("STATUS:");
					statusLabel.getStyleClass().add("createReport_Options_Headers");
			statusInnerPane.getChildren().add(statusLabel);
					
					statusSelection = new ComboBox<Status>();
					statusSelection.getItems().addAll(Status.statusList);
					statusSelection.getStyleClass().add("createReport_Options_Text");
			statusInnerPane.getChildren().add(statusSelection);
				
				Label versionLabel = new Label("VERSION:");
				versionLabel.getStyleClass().add("createReport_Options_Headers");
			statusInnerPane.getChildren().add(versionLabel);
				
				stageVersion = new ComboBox<vektra.Stage>();
				stageVersion.getItems().addAll(vektra.Stage.stageList);
				stageVersion.getStyleClass().add("createReport_Options_Text");
			statusInnerPane.getChildren().add(stageVersion);
			
				version = new TextField();
				version.getStyleClass().add("createReport_Options_Text");
				version.setPromptText(exampleVersion);
			statusInnerPane.getChildren().add(version);
			statusPane.getChildren().add(statusInnerPane);			
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
	
	protected static boolean checkForErrors(){
		
		if( text.getText().isEmpty() ){
			PopupError.show("Could not proceed", "Please enter a message for the bug!");
			return false;
		}
		else if( getSelectedTags(bugID).isEmpty() ){
			PopupError.show("Could not proceed", "Please select a valid tag related to the message!");
			return false;
		}
		else if( getPriority() == Priority.NULL ){
			PopupError.show("Could not proceed", "Please select a Priority related to the message!");
			return false;
		}
		else if( getVersion() == null ){
			PopupError.show("Could not proceed", "Please select the Version of the game!");
			return false;
		}
		
		// Acceptable
		return true;
	}
	
	private static Version getVersion() {
		return new Version(version.getText(), stageVersion.getValue());
	}

	protected static void addImage(String link, BugImage image){
		bugimages.put(link, image);
		images.put(image.getImage(), link);
		
		ImageView v = new ImageView(image.getImage());
		v.setOnMouseClicked(new ImageClickedListener());
		v.setFitWidth(100);
		v.setFitHeight(100);
		screenshotList.getChildren().add(v);
	}
	
	protected static void removeImage(ImageView view){
		// Finishe remove so removing via mosue click works
		String removedLink = images.remove(view.getImage());
		BugImage removedImage = bugimages.remove(removedLink);
		
		System.out.println("RemovedImage: " + removedImage);
		System.out.println("RemovedLink: " + removedLink);
		
		screenshotList.getChildren().remove(view);
	}

	protected static BugItem getBug(){
		return new BugItem(bugID, getSelectedTags(bugID), getPriority(), statusSelection.getValue(), null, text.getText(), null, getVersion(), bugimages);
	}

	
	protected static void addImages(Map<String, BugImage> imageMap) {
		for(Entry<String, BugImage> p : imageMap.entrySet()){
			addImage(p.getKey(),p.getValue());
		}
	}
	
	protected static void setConfirmButton(Node createReport) {
		
		bottomPane.addColumn(0,createReport);
		GridPane.setHalignment(createReport, HPos.RIGHT);
		GridPane.setValignment(createReport, VPos.CENTER);
	}
	
	protected static Priority getPriority() {
		if( priorityGroup.getSelectedToggle() == LOW ){
			return Priority.get("LOW");
		}
		else if( priorityGroup.getSelectedToggle() == MEDIUM ){
			return Priority.get("MEDIUM");
		}
		else if( priorityGroup.getSelectedToggle() == HIGH ){
			return Priority.get("HIGH");
		}
		return Priority.get("UNKNOWN");
	}

	protected static Set<Tag> getSelectedTags(int bugid) {
		Set<Tag> tags = new HashSet<Tag>();
		
		if( GAMEPLAY.isSelected() ){
			tags.add(new Tag(-1,"GAMEPLAY"));
		}
		if( VISUAL.isSelected() ){
			tags.add(new Tag(-2,"VISUAL"));
		}
		if( AUDIO.isSelected() ){
			tags.add(new Tag(-3,"AUDIO"));
		}
		if( BREAKING.isSelected() ){
			tags.add(new Tag(-4,"BREAKING"));
		}
		
		
		
		return tags;
	}
	
	protected static class ImageClickedListener implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent arg0) {
			ImageView v = (ImageView)arg0.getSource();
			boolean delete = PopupConfirmation.show("Remove Image", "Are you sure you want to remove this screenshot from the report?");
			if( delete ){
				removeImage(v);
			}
		}
		
	}

	protected static class UploadLinkButtonPressed implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String link = enterLink.getText();	
			System.out.println("Link: " + link);
			
			if( bugimages.keySet().contains(link) ){
				PopupError.show("Can not upload image", "Link already uploaded!");
				return;
			}
			
			try{
				BugImage image = OnlineResources.getImage(link);
				if( image == null || image.getImage() == null ){
					PopupError.show("Upload Image", "Unable to download image!");
					return;
				}
				else{
					addImage(link,image);
				}
				enterLink.setText("");
			}catch(IllegalArgumentException e){
				PopupError.show("Could not load image", "The provided link can not be converted to an image!");
				System.out.println("Can not load image '" + link + "'");
			}
			
		}

	}
}
