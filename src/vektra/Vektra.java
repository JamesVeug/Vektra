package vektra;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.javafx.scene.control.skin.TableViewSkinBase;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import vektra.dialogs.LoginDialog;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;
import vektra.extrawindows.CreateReport;
import vektra.extrawindows.EditReport;



public class Vektra extends Application{
	@SuppressWarnings("unused")
	private Stage primaryStage;
	
	private ObservableList<BugItem> importedData;
	private BugItem selectedBug;
	
	private Label loggedInLabel;
	private Label loggedInName;
	private TableView<BugItem> bugs;
	private Label reportID;
	private Label tags;
	private Label priority;
	private StackPane screenshotPane;
	private ImageView displayScreenshot;
	private Image logo;
	private FlowPane screenshotList;
	private ImageView selectedListImage;
	private Label openScreenshots;
	private TextArea message;
	private Label whoLogged;
	private Label loggedDate;
	private MenuItem loginMenuItem;
	private MenuItem signoutMenuItem;
	
	private RefreshThread refreshThread;
	private WindowCloseRequest closeRequest;
	
	
	@SuppressWarnings("unchecked")
	public void start(Stage primaryStage) throws Exception {
		
		// Required in order to be able to use it in css's
		@SuppressWarnings("unused")
		final Font font = Font.loadFont(new FileInputStream(new File("src/fonts/Microstyle Bold Extended ATT.ttf")), 20);
		
		this.primaryStage = primaryStage;
		primaryStage.setTitle("VEKTRA - Bug Reporter");
		primaryStage.setWidth(1024);
		primaryStage.setHeight(800);
		primaryStage.getIcons().add(new Image("v.jpg"));
		
		
		BorderPane mainLayout = new BorderPane();
		mainLayout.getStylesheets().add("css/custom.css");
		//mainLayout.setBackground(black);

		
		GridPane layout = new GridPane();
		
		//layout.setVgap(10);
		//layout.setPadding(new Insets(0, 10, 0, 10));
		
		// Top
		GridPane options = new GridPane();
		options.setStyle("-fx-border-width: 1, 1; -fx-border-color: #FFFFFF");
		options.setPrefHeight(100);
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.setPadding(new Insets(5,5,5,5));
		buttonGrid.setVgap(5);
		
		Button createReport = new Button("CREATE\nREPORT");
		createReport.setTextAlignment(TextAlignment.CENTER);
		createReport.setOnAction(new MenuItemCreateReport());
		createReport.getStyleClass().add("button_create");
		createReport.setPrefWidth(85);
		createReport.setPrefHeight(65);
		
		Button editReport = new Button("EDIT\nREPORT");
		editReport.setTextAlignment(TextAlignment.CENTER);
		editReport.setOnAction(new EditReportButtonPressed());
		editReport.getStyleClass().add("button_edit");
		editReport.setPrefWidth(85);
		editReport.setPrefHeight(50);
		
		Button deleteReport = new Button("DELETE\nREPORT");
		deleteReport.setTextAlignment(TextAlignment.CENTER);
		deleteReport.setOnAction(new DeleteReport());
		deleteReport.getStyleClass().add("button_delete");
		deleteReport.setPrefWidth(85);
		deleteReport.setPrefHeight(50);
		
		buttonGrid.addRow(0, createReport);
		buttonGrid.addRow(1, editReport);
		buttonGrid.addRow(2, deleteReport);
		
		
		GridPane extraButtonGrid = new GridPane();
		extraButtonGrid.setPadding(new Insets(5,5,5,0));
		extraButtonGrid.setVgap(5);
		
		Button openID = new Button("OPEN ID");
		openID.setOnAction(new OpenID());
		openID.getStyleClass().add("button_extra");
		openID.setPrefWidth(150);
		openID.setPrefHeight(50);
		Button refresh = new Button("REFRESH");
		refresh.setOnAction(new Refresh());
		refresh.getStyleClass().add("button_extra");
		refresh.setPrefWidth(150);
		refresh.setPrefHeight(50);
		
		extraButtonGrid.addRow(0, openID);
		extraButtonGrid.addRow(1, refresh);
		
		
		GridPane loggedGrid = new GridPane();
		loggedGrid.alignmentProperty().set(Pos.TOP_RIGHT);
			loggedInLabel = new Label("User: ");
			loggedInLabel.getStyleClass().add("loggedLabel");
			loggedInName = new Label("-");
			loggedInName.getStyleClass().add("loggedLabel");
		loggedGrid.addRow(0, loggedInLabel);
		loggedGrid.addColumn(1, loggedInName);
		
		
		options.addColumn(0, buttonGrid);
		options.addColumn(1, extraButtonGrid);
		options.addColumn(2, loggedGrid);
		
		
		
		
		
		// Bottom Left ( BUG LIST )
		TableColumn<BugItem, Integer> idColumn = new TableColumn<BugItem, Integer>("REPORT ID");
		idColumn.getProperties().put(TableViewSkinBase.REFRESH, Boolean.TRUE);
		idColumn.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
		idColumn.setPrefWidth(60);
		idColumn.setCellValueFactory(new PropertyValueFactory<BugItem, Integer>("ID"));
		idColumn.setCellFactory(new Callback<TableColumn<BugItem, Integer>, TableCell<BugItem, Integer>>() {
	        public TableCell<BugItem, Integer> call(TableColumn<BugItem, Integer> param) {
	            return new TableCell<BugItem, Integer>() {

	                @Override
	                public void updateItem(Integer item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new BugListListener());
	                        setText(String.valueOf(item));
	                    }
	                }
	            };
	        }
	    });
		
		TableColumn<BugItem, String> statusColumn = new TableColumn<BugItem, String>("STATUS");
		statusColumn.getProperties().put(TableViewSkinBase.REFRESH, Boolean.TRUE);
		statusColumn.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
		statusColumn.setPrefWidth(70);
		statusColumn.setCellValueFactory(new PropertyValueFactory<BugItem, String>("status"));
		statusColumn.setCellFactory(new Callback<TableColumn<BugItem, String>, TableCell<BugItem, String>>() {
	        public TableCell<BugItem, String> call(TableColumn<BugItem, String> param) {
	            return new TableCell<BugItem, String>() {

	                @Override
	                public void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new BugListListener());
	                        setText(item);
	                    }
	                }
	            };
	        }
	    });
		//statusColumn.get.setStyle("-fx-text-fill: red; ");
		
		bugs = new TableView<BugItem>();
		bugs.getColumns().addAll(idColumn, statusColumn);
		bugs.setPrefHeight(400);
		bugs.setPrefWidth(320);
		bugs.getStylesheets().add("css/buglist.css");
		bugs.getProperties().put(TableViewSkinBase.REFRESH, Boolean.TRUE);
		bugs.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
		
		// Middle area
		GridPane screenshotinfo = new GridPane();
		screenshotinfo.setPadding(new Insets(10,0,0,10));
		screenshotinfo.getStylesheets().add("css/custom.css");
		screenshotinfo.setHgap(20);
		//screenshotinfo.setPrefHeight(150);
		
		
		Label reportIDLabel = new Label("REPORT ID:");
		reportIDLabel.getStyleClass().add("reportidheader");
		screenshotinfo.addColumn(0, reportIDLabel);
		
		reportID = new Label("-");
		reportID.getStyleClass().add("reportid");
		screenshotinfo.addColumn(1, reportID);
		
		Label tagLabel = new Label("TAGS:");
		tagLabel.setPrefHeight(30);
		tagLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(1, tagLabel);
		
		tags = new Label("-");
		tags.setPrefHeight(30);
		tags.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(1, tags);
		
		Label PriorityLabel = new Label("PRIORITY:");
		PriorityLabel.setPrefHeight(30);
		PriorityLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(2, PriorityLabel);
		
		priority = new Label("-");
		priority.setPrefHeight(30);
		priority.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(1, priority);
		
		
		screenshotPane = new StackPane();
		screenshotPane.setStyle("-fx-border-width: 1; -fx-border-color: white;");
		screenshotPane.setPrefHeight(325);		
		screenshotPane.setPadding(new Insets(5));		
			logo = new Image("logo.png");
			displayScreenshot = new ImageView(logo);
			displayScreenshot.setPreserveRatio(true);
		screenshotPane.getChildren().add(displayScreenshot);
		
		StackPane.setAlignment(screenshotPane, Pos.CENTER);
		
		// Bottom Middle
		GridPane screenshotListPane = new GridPane();
		screenshotListPane.setPrefHeight(200);
		
		openScreenshots = new Label("Open Screenshots(0)");
		openScreenshots.getStyleClass().add("openScreenShots");
		screenshotListPane.addRow(0, openScreenshots);

		// List of Screenshots to be displayed 
		screenshotList = new FlowPane();
		screenshotList.setPrefHeight(400);
		screenshotList.setVgap(8);
		screenshotList.setHgap(4);
		//screenshotList.setOnMouseClicked(new ScreenShotListListener());
		screenshotListPane.addRow(1, screenshotList);

		GridPane screenshotlayout = new GridPane();
		screenshotlayout.setPadding(new Insets(0,0,0,5));
		screenshotlayout.addRow(0, screenshotinfo);
		screenshotlayout.addRow(1, screenshotPane);
		screenshotlayout.addRow(2, screenshotListPane);
		
		// Bottom right
		
		GridPane messagePane = new GridPane();
			messagePane.setPadding(new Insets(5,5,0,5));
			
			Label label = new Label("REPORT DESCRIPTION:");
			label.getStyleClass().add("reportDescription");
			messagePane.addRow(0, label);
			
			message = new TextArea("No Text Here");
			message.setPrefHeight(575);
			message.setEditable(false);
			messagePane.addRow(1, message);
			messagePane.setPrefHeight(200);
			
			GridPane extraInfoPane = new GridPane();
				extraInfoPane.setPadding(new Insets(0,0,0,10));
				extraInfoPane.setStyle("-fx-background-color: #ECECEC;");
				Label loggedByLabel = new Label("LOGGED BY:");
				loggedByLabel.setPrefSize(120,50);
				loggedByLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				whoLogged = new Label("-");
				whoLogged.setPrefSize(100,50);
				whoLogged.getStyleClass().add("extraMessageInfo");
				
				Label dateLabel = new Label("DATE:");
				dateLabel.setPrefSize(70,50);
				dateLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				loggedDate = new Label("-");
				loggedDate.setPrefSize(200,50);
				loggedDate.getStyleClass().add("extraMessageInfo");
				extraInfoPane.addColumn(0, loggedByLabel);
				extraInfoPane.addColumn(1, whoLogged);
				extraInfoPane.addColumn(2, dateLabel);
			extraInfoPane.addColumn(3, loggedDate);
		messagePane.addRow(2, extraInfoPane);


		//layout.addRow(0,options);
		layout.addColumn(0,bugs);
		layout.addColumn(1,screenshotlayout);
		layout.addColumn(2,messagePane);

		mainLayout.setCenter(options);
		mainLayout.setBottom(layout);
		Scene scene = new Scene(mainLayout,Paint.valueOf("green"));

		closeRequest = new WindowCloseRequest();
		primaryStage.setOnCloseRequest(closeRequest);/*new EventHandler<WindowEvent>(){
			@Override public void handle(WindowEvent arg0) { 
				closeRequest.
				SQLData.close();
			}	
		});*/
		
		setupMenu(mainLayout, primaryStage);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Ask to log in!
		loginMenuItemPressed();
		
		//popupLoading("Loading:", "Uploading Report");
	}
	

	private static void popupLoading(String title, String text){
		
		
		Group root = new Group();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Progress Controls");
 
        final Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(50);
         
        final ProgressBar pb = new ProgressBar(0);
        final ProgressIndicator pi = new ProgressIndicator(0);
 
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                pb.setProgress(new_val.doubleValue()/50);
                pi.setProgress(new_val.doubleValue()/50);
                
                if( new_val.doubleValue() == 100 ){
                	System.out.println("DONE");
                }
                else{
                	System.out.println(new_val);
                }
            }
        });
 
        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(slider, pb, pi);
        scene.setRoot(hb);
        stage.show();
	}
	
	public void refreshData(ObservableList<BugItem> loadedData, long length) {
		System.out.println("Refresh Time: " + length);
		
		// Don't do anything if they are the same
		if( importedData != null && importedData.equals(loadedData) ){
			//System.out.println("NO CHANGE");
			return;
		}
		
		for(BugItem i : loadedData){
			System.out.println(i);
		}
		
				
		// Assign new values in the table
		//bugs.getItems().removeAll(bugs.getItems());
		//FXCollections.copy(bugs.getItems(), loadedData);
		/*for(BugItem i : loadedData){
			bugs.getItems().add(i);
		}*/
		bugs.setItems(loadedData);
		
		
		
		//bugs.itemsProperty().
		
		// Reselect
		if( selectedBug != null ){
			bugs.getSelectionModel().select(selectedBug);
		}
		

		// Save local data
		importedData = loadedData;
		
	}
	

	/**
	 * Loads all the bugs from a database
	 * @return
	 */
	@SuppressWarnings("unused")
	private ObservableList<BugItem> getSetTestBugs(){
		ObservableList<BugItem> list = FXCollections.observableArrayList();
		
		//int iD, List<String> tags, int priority, String status,
		//String who, String message, String date
		String image1Link = "test.jpg";
		Map<String, Image> images1 = new HashMap<String, Image>(); images1.put(image1Link, new Image(image1Link));
		list.add(new BugItem(0, toList("Gameplay","Visual"),"High", "WIP","Joure","Where is the Turkey?","27 July 07 1991", images1));
		
		String image2Link = "test2.jpg";
		Map<String, Image> images2 = new HashMap<String, Image>(); images2.put(image2Link, new Image(image2Link));
		list.add(new BugItem(1, toList("Gameplay"),"Low", "PENDING","Josh","Why can't I feel my toes?","2 June 17 2001", images2));
		
		return list;
	}

	private Set<String> toList(String... string) {
		Set<String> s = new HashSet<String>();
		s.addAll(Arrays.asList(string));
		return s;
	}

	private void setupMenu(BorderPane mainLayout, Stage primaryStage) {
		
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Menu file = new Menu("File");
		loginMenuItem = new MenuItem("Login");
		loginMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				loginMenuItemPressed();
			}
			
		});
		signoutMenuItem = new MenuItem("Sign Out");
		signoutMenuItem.setVisible(false);
		signoutMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				signOutMenuItemPressed();
			}
			
		});
		file.getItems().add(loginMenuItem);
		file.getItems().add(signoutMenuItem);
		
		Menu edit = new Menu("Edit");
		Menu view = new Menu("View");
		Menu report = new Menu("Report");
		MenuItem createReport = new MenuItem("Create Report");
		report.getItems().add(createReport);
		createReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				int maxID = importedData == null ? -1 : importedData.get(importedData.size()-1).ID;
				CreateReport.display(maxID);
			}
			
		});
		
		menuBar.getMenus().add(file);
		menuBar.getMenus().add(edit);
		menuBar.getMenus().add(view);
		menuBar.getMenus().add(report);

		mainLayout.setTop(menuBar);
		
	}

	protected void signOutMenuItemPressed() {
		
		// Disconnect from server
		if( SQLData.isConnected() ){
			
			// Stop refreshing
			closeRequest.removeThread();
			SQLData.close();
		}

		// Change GUI
		loginMenuItem.setVisible(true);
		signoutMenuItem.setVisible(false);
		loggedInName.setText("-");
		

		// Tell the user we logged in!
		PopupMessage.show("Sign Out","Logged Out Successfully!\nGood Bye" + SQLData.getUsername() + "!");
	}

	protected void loginMenuItemPressed() {
		
		
		boolean loggedIn = LoginDialog.show();
		if( !loggedIn ){
			return;
		}
		
		//
		// We connected!
		//
		
		// Start a new refreshing thread to get the data
		
		// Make sure the current thread is dead!
		if( refreshThread != null ){
			refreshThread.stopRunning();
			while( refreshThread.isAlive() ){
				
			}
		}
		
		// Start thread
		refreshThread = new RefreshThread();
		refreshThread.start();
		
		// Change UI to reflect being logged in
		closeRequest.setThread(refreshThread);
		loggedInName.setText(SQLData.getUsername());		
		loginMenuItem.setVisible(false);
		signoutMenuItem.setVisible(true);
		
		// Tell the user we logged in!
		PopupMessage.show("Login","Login Successful!\nWelcome " + SQLData.getUsername() + "!");
		
	}

	private class MenuItemCreateReport implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			//int maxID = importedData == null ? -1 : importedData.get(importedData.size()-1).ID;
			CreateReport.display(-1);
		}

	}
	
	private class EditReportButtonPressed implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("Edit Report Pressed");
			EditReport.display(selectedBug);
		}

	}
	
	private class DeleteReport implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent t) {
			System.out.println("Delete Report Pressed");
			if( !SQLData.isConnected() ){
				PopupMessage.show("Delete Report", "Must be logged in before attempting to Delete!");
				return;
			}
			else if( selectedBug == null ){
				PopupMessage.show("Delete Report", "Must select a Bug before attempting to Delete!");
				return;
			}
			
    		BugItem item = selectedBug;
    		if( item == null || !PopupConfirmation.show("Delete Report","BugID: " + item.ID + "\nAre you sure you want to delete this report?")){
    			return;
    		}
    		
    		//
    		// DELETE
    		//
    		
    		boolean isDeleted = SQLData.delete(item);
    		if( isDeleted ){
    			PopupMessage.show("Delete Compelted", "Successfully deleted Bug with ID '" + item.ID + "'");
    		}
    		else{
    			PopupError.show("Delete Failed", "Could not delete the Bug with ID '" + item.ID + "'");
    		}
		}
	}
	
	private class OpenID implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("OpenID Pressed");
		}
	}
	
	private class Refresh implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			if( refreshThread == null || !refreshThread.isAlive() ){
				PopupError.show("Can not refresh!", "Not logged in?");
			}
			
			refreshThread.resetTime();
		}
	}
	
	public void selectImage(ImageView v){
		if( selectedListImage != null ){
			scaleImageView(selectedListImage,75,75);
			/*selectedListImage.setScaleX(0.75);
			selectedListImage.setScaleY(0.75);// getImage()*/
		}
		

		scaleImageView(v,100,100);
		selectedListImage = v;
		

    	displayScreenshot.setImage(v.getImage());
    	scaleImageView(displayScreenshot, 400, 400);
	}
	
	private class ScreenShotListListener implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent arg0) {
			ImageView v = (ImageView)arg0.getSource();
			selectImage(v);
		}
		
		
	}

	private void scaleImageView(ImageView v, int desiredWidth, int desiredHeight) {
		double imageW = v.getImage().getWidth();
		double imageH = v.getImage().getHeight();
		
		double widthScale = desiredWidth/imageW;
		double heightScale = desiredHeight/imageH;
		
		v.setFitWidth(desiredWidth);
		v.setFitHeight(desiredHeight);
		/*v.set
		v.setScaleX(widthScale);
		v.setScaleY(heightScale);*/
		
	}
	
	private class BugListListener implements EventHandler<MouseEvent> {


		@Override
		public void handle(MouseEvent t) {
			//System.out.println("Clicked Table View");
			@SuppressWarnings("rawtypes")
			TableCell c = (TableCell) t.getSource();
            int index = c.getIndex();
            //System.out.println("index " + index);
    		BugItem item = importedData.get(index);
            //System.out.println("bugid " + item.ID);
    		if( item != null ){
    			//System.out.println("Selected Bug: ");
	            //System.out.println("id = " + item.getID());
	            //System.out.println("message = " + item.getMessage());
	            selectedBug = item;
	            openScreenshots.setText("Open Screenshots (" + item.images.size() + ")");
	            message.setText(item.getMessage());
	            reportID.setText(String.valueOf(item.getID()));
	            
	            int i = 0;
	            String tagString = "";
	            for(String tag : item.getTags()){ 
	            	tagString += tag;
	            	if( (++i) < item.getTags().size() ){
	            		tagString += ", ";
	            	}
	            }
	            
	            tags.setText(tagString);
	            priority.setText(item.getPriority());
	            
	            whoLogged.setText(item.who);
	            loggedDate.setText(item.date);

	            // Clear images and add new ones if there are some
            	screenshotList.getChildren().clear();
            	displayScreenshot.setImage(null);
	            if( !item.images.isEmpty() ){
	            	/*if( image != null && image.getImage().equals(logo)){
		            	image.fitWidthProperty().bind(screenshotPane.widthProperty());
		    			image.fitHeightProperty().bind(screenshotPane.heightProperty());	
	            	}*/
	            	
	            	
	            	i = 0;
	            	ScreenShotListListener listener = new ScreenShotListListener();
	            	for( String link : item.imageMap.keySet() ){
	            		
	            		Image image = new Image(link);
	            		ImageView v = new ImageView(image);
	            		v.preserveRatioProperty();
	            		v.setOnMouseClicked(listener);
	            		screenshotList.getChildren().add(v);
	            		if( i++ == 0 ){
	            			selectImage(v);
	            		}
	            		else{
		            		scaleImageView(v,75,75);
	            		}
	            	}
	            }
    		}
		}

	}
	
	private class RefreshThread extends Thread {
		private long time = 0;
		private boolean running = true;
		
		@Override
		public void run() {
			while(running){
				
				if( time < System.currentTimeMillis() ){
					long start = System.currentTimeMillis();
					ObservableList<BugItem> loadedData = SQLData.getData();
					long end = System.currentTimeMillis();
					long length = end-start;
					
					refreshData(loadedData,length);
					time = System.currentTimeMillis() + 1000;
				}
				
			}
			System.out.println("No longer refreshing!");
		}
		
		public void resetTime() {
			time = 0;
		}

		public void stopRunning(){
			running = false;
		}
	}
	
	private class WindowCloseRequest implements EventHandler<WindowEvent>{
		
		private RefreshThread refreshThreads;
		
		public WindowCloseRequest(){
			
		}
		
		
		public void setThread(RefreshThread t){
			refreshThreads = t;
		}
		
		public void removeThread(){
			refreshThreads.stopRunning();
			refreshThreads = null;
		}


		/**
		 * 
		 * Window is closing!
		 * @see javafx.event.EventHandler#handle(javafx.event.Event)
		 */
		@Override
		public void handle(WindowEvent arg0) {
			if( refreshThreads != null ){
				refreshThreads.stopRunning();
			}
			
			// Wait for it to close
			while(refreshThreads != null && refreshThreads.isAlive() ){
				
			}
			
			SQLData.close();
		}
	}

	
	
	
	public static void main(String[] args){
			
		
		launch(args);
	}
}
