package vektra;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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


/**
 * Primary class for the Vektra Bug Reporter
 * This class includes all GUI aspects for the main window that gets used by all users.
 * No Bug modification or SQL queries are made in this
 * @author James
 *
 */
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
	
	/**
	 * Starting method that creates the main window and loads the primary font.
	 */
	public void start(Stage primaryStage) throws Exception {
		
		// Required in order to be able to use it in css's
		@SuppressWarnings("unused")
		final Font font = Font.loadFont(Vektra.class.getClass().getResourceAsStream("/fonts/Microstyle Bold Extended ATT.ttf"), 20);
		
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
		

		bugs = new TableView<BugItem>();
		bugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setupTable();
		
		
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
		screenshotListPane.setPrefWidth(600);
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
		login();
		
		//popupLoading("Loading:", "Uploading Report");
	}
	

	/**
	 * Sets up the table with all the bugs we currently have sorted
	 * HACK HACK HACK
	 * Recreates all the columns
	 */
	private void setupTable() {
		
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
		
		//bugs = new TableView<BugItem>();
		bugs.getColumns().clear();
		bugs.getColumns().addAll(idColumn, statusColumn);		
		bugs.setPrefHeight(400);
		bugs.setPrefWidth(320);
		bugs.getStylesheets().add("css/buglist.css");
		bugs.getProperties().put(TableViewSkinBase.REFRESH, Boolean.TRUE);
		bugs.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
	}


	/**
	 * Given an amount of data that was received from a database.
	 * We want to update the visual representation of the data on the GUI
	 * 
	 * @param loadedData New data loaded off a website
	 * @param length time taken to load the data
	 */
	public void refreshData(final ObservableList<BugItem> loadedData, long length) {
		System.out.println("Refresh Time: " + length);
		
		// Don't do anything if they are the same
		if( importedData == null || importedData.isEmpty() ){
			System.out.println("NO CHANGE");
			return;
		}
		
		/*for(BugItem i : loadedData){
			System.out.println(i);
		}*/

		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				setupTable();
				
				// Assign new values in the table
				bugs.setItems(loadedData);

				
				// Reselect
				deselectBug();
				if( selectedBug != null ){
					System.out.println("Reselecting bug");
					
					// Get new bug from new data
					int index = loadedData.indexOf(selectedBug);
					if( index == -1 ){
						// Not in new data
						selectedBug = null;
					}
					else{
						selectedBug = loadedData.get(index);
					
						bugs.getSelectionModel().select(selectedBug);
						selectBug(selectedBug);
					}
				}
			}
			
		});
		Platform.runLater(t);
		
		

		// Save local data
		importedData = loadedData;
		
		
	}

	/**
	 * Deselects the current bug in the bug list
	 */
	protected void deselectBug() {

    	screenshotList.getChildren().clear();
    	displayScreenshot.setImage(null);
	}

	/**
	 * Selects the given bug parameter from the bugs list and displays it on the screen visually.
	 * @param bug What to select from the list
	 */
	public void selectBug(BugItem bug) {
		 //System.out.println("bugid " + item.ID);
		if( bug != null ){
			//System.out.println("Selected Bug: ");
            //System.out.println("id = " + item.getID());
            //System.out.println("message = " + item.getMessage());
            selectedBug = bug;
            openScreenshots.setText("Open Screenshots (" + bug.imageMap.size() + ")");
            message.setText(bug.message);
            reportID.setText(String.valueOf(bug.ID));
            
            int i = 0;
            String tagString = "";
            for(String tag : bug.tags){ 
            	tagString += tag;
            	if( (++i) < bug.tags.size() ){
            		tagString += ", ";
            	}
            }
            
            tags.setText(tagString);
            priority.setText(bug.priority);
            
            whoLogged.setText(bug.who);
            loggedDate.setText(bug.date);

            // Clear images and add new ones if there are some
        	screenshotList.getChildren().clear();
        	displayScreenshot.setImage(null);
            if( !bug.getImages().isEmpty() ){

            	i = 0;
            	ScreenShotListListener listener = new ScreenShotListListener();
            	for( String link : bug.imageMap.keySet() ){
            		
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

	/**
	 * Sets up the menu bar on startup with the required file/edit/view... etc items.
	 * @param mainLayout Layout to add the items to.
	 * @param primaryStage What stage to add the menu to.
	 */
	private void setupMenu(BorderPane mainLayout, Stage primaryStage) {
		
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Menu file = new Menu("File");
		loginMenuItem = new MenuItem("Login");
		loginMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				login();
			}
			
		});
		signoutMenuItem = new MenuItem("Sign Out");
		signoutMenuItem.setVisible(false);
		signoutMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				signOut();
			}
			
		});
		MenuItem quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
                closeRequest.closeEverything();
				Platform.exit();
                System.exit(0);
			}
			
		});
		file.getItems().add(loginMenuItem);
		file.getItems().add(signoutMenuItem);
		file.getItems().add(quitMenuItem);
		
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
		MenuItem editReport = new MenuItem("Edit Report");
		report.getItems().add(editReport);
		editReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				EditReport.display(selectedBug);
			}
			
		});
		MenuItem deleteReport = new MenuItem("Delete Report");
		report.getItems().add(deleteReport);
		deleteReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				deleteCurrentBug();
			}
			
		});
		
		menuBar.getMenus().add(file);
		menuBar.getMenus().add(edit);
		menuBar.getMenus().add(view);
		menuBar.getMenus().add(report);

		mainLayout.setTop(menuBar);
		
	}

	/**
	 * Signs out of the database
	 * If we are not signed in, it will still reset all GUI appropriately.
	 */
	protected void signOut() {
		
		// Disconnect from server
		if( SQLData.isConnected() ){
			
			// Stop refreshing
			closeRequest.removeThread();
			SQLData.close();

			// Tell the user we logged in!
			PopupMessage.show("Sign Out","Logged Out Successfully!\nGood Bye" + SQLData.getUsername() + "!");
		}

		// Change GUI
		loginMenuItem.setVisible(true);
		signoutMenuItem.setVisible(false);
		loggedInName.setText("-");
		
	}

	/**
	 * Calls LoginDialog which grabs the information from the user and attempts to log in with it.
	 * If the log in is successful. A refresh thread will begin and a message will appear.
	 */
	protected void login() {
		
		
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
		
		//Platform.runLater(refreshThread);
		//Platform.setImplicitExit(false);
		
		loggedInName.setText(SQLData.getUsername());		
		loginMenuItem.setVisible(false);
		signoutMenuItem.setVisible(true);
		
		// Tell the user we logged in!
		PopupMessage.show("Login","Login Successful!\nWelcome " + SQLData.getUsername() + "!");
		
	}

	/**
	 * When clicking on each of the screenshits in the screenshot list. 
	 * This is triggered and will then select the screenshot and display the correct image in the main area whiel deseleting the other.
	 * @param v Image clicked on, on the GUI
	 */
	public void selectImage(ImageView v){
		
		// Reset the old image if we have one
		if( selectedListImage != null ){
			scaleImageView(selectedListImage,75,75);
		}
		
		// Scale the new image to 100x100
		scaleImageView(v,100,100);
		selectedListImage = v;
		
		// Display the new image
    	displayScreenshot.setImage(v.getImage());
    	scaleImageView(displayScreenshot, 400, 400);
	}
	
	/**
	 * Pressing the delete button or menuitem will call this and will ask for verification if we can or should deleete it.
	 * Removes from the database!
	 */
	public void deleteCurrentBug() {
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

	/**
	 * Scales the given view to the given width and height for display.
	 * @param view ImageView we want to scale
	 * @param desiredWidth Width to assign the image to
	 * @param desiredHeight height to assign the image to
	 */
	private void scaleImageView(ImageView view, int desiredWidth, int desiredHeight) {		
		view.setFitWidth(desiredWidth);
		view.setFitHeight(desiredHeight);
	}
	
	/**
	 * When the Create Report menu item is pressed. It will call the create report class to create a new bug report.  
	 * @author James
	 *
	 */
	private class MenuItemCreateReport implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent arg0) { CreateReport.display(-1); }
	}
	
	/**
	 * When the Edit Report menu item is pressed. It will call the edit report class to edit the currently selected report
	 * @author James
	 *
	 */
	private class EditReportButtonPressed implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent arg0) { EditReport.display(selectedBug);	}
	}
	
	/**
	 * When the Delete Report menu item is pressed. It will ask for verification before deleteing.  
	 * @author James
	 *
	 */
	private class DeleteReport implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent t) { deleteCurrentBug(); }
	}
	
	/**
	 * When the Create Report menu item is pressed. It will call the create report method  
	 * @author James
	 *
	 */
	private class OpenID implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("OpenID Pressed");
		}
	}
	
	/**
	 * When the refresh button is pressed. It will reset the timer on the refresh thread which will then refresh the data.
	 * @author James
	 *
	 */
	private class Refresh implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			if( refreshThread == null || !refreshThread.isAlive() ){
				PopupError.show("Can not refresh!", "Not logged in?");
			}
			
			refreshThread.resetTime();
		}
	}

	/**
	 * Listens for a click of the mouse on the screenshot list.
	 * This gets the image selected by the mouse and returns it to the selectImage method
	 * @author James
	 *
	 */
	private class ScreenShotListListener implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent arg0) {
			ImageView v = (ImageView)arg0.getSource();
			selectImage(v);
		}
		
		
	}
	
	/**
	 * Listens for the user to click on a bug in the bug list table.
	 * Then selects the bug that was clicked on and displays it's information visually.
	 * @author James
	 *
	 */
	private class BugListListener implements EventHandler<MouseEvent> {


		@Override
		public void handle(MouseEvent t) {
			//System.out.println("Clicked Table View");
			@SuppressWarnings("rawtypes")
			TableCell c = (TableCell) t.getSource();
            int index = c.getIndex();
            //System.out.println("index " + index);
    		BugItem item = importedData.get(index);
    		selectBug(item);
		}

	}
	
	/**
	 * Constantly running thread in the background checking if the database has any new updates
	 * @author James
	 *
	 */
	private class RefreshThread extends Thread {
		
		// Next possible time we can refresh
		private long time = 0;
		
		// Boolean to allow us to continue running or not.
		private boolean running = true;
		
		@Override
		public void run() {
			while(running){
				
				// Only check every so often
				if( time < System.currentTimeMillis() ){
					
					// Record when we tried getting the new data
					long start = System.currentTimeMillis();
					
					// Get the data from the database
					ObservableList<BugItem> loadedData = SQLData.getData();
					
					// Get the end time
					long end = System.currentTimeMillis();
					
					// Get how long it took to pull the data
					long length = end-start;
					
					// Refresh the GUI
					refreshData(loadedData,length);
					
					// Reset the timer
					time = System.currentTimeMillis() + 1000;
				}
				
			}
			System.out.println("No longer refreshing!");
		}
		
		/**
		 * Resets the timer allowing us to pull the data next interation.
		 */
		public void resetTime() {
			time = 0;
		}

		/**
		 * Stops the thread from running and kills it.
		 */
		public void stopRunning(){
			running = false;
		}
	}
	
	/**
	 * When we want to close the program. We want to make sure we disconnect from everything else.
	 * @author James
	 *
	 */
	private class WindowCloseRequest implements EventHandler<WindowEvent>{
		
		// The thread that is constantly running in the background
		private RefreshThread thread;
		
		/**
		 * Closes everything
		 */
		public void closeEverything() {
			if( thread != null ){
				thread.stopRunning();
			}
			
			// Wait for it to close
			while(thread != null && thread.isAlive() ){
				//System.out.println("Waiting for refresh Thread to die...");
			}
			
			// Close the connection to the database
			SQLData.close();
		}

		/**
		 * Assigns a new thread to this window for us to close later
		 * @param t
		 */
		public void setThread(RefreshThread t){
			thread = t;
		}
		
		/**
		 * Stops and removes the thread
		 */
		public void removeThread(){
			thread.stopRunning();
			thread = null;
		}


		/**
		 * 
		 * Window is closing!
		 * @see javafx.event.EventHandler#handle(javafx.event.Event)
		 */
		@Override
		public void handle(WindowEvent arg0) {
			closeEverything();
		}
	}

	
	
	
	public static void main(String[] args){
		launch(args);
	}
}
