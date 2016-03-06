package vektra;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vektra.GUI.BugListGUI;
import vektra.GUI.BugMessageGUI;
import vektra.GUI.ReportOptionsGUI;
import vektra.GUI.ScreenShotDisplayGUI;
import vektra.dialogs.LoginDialog;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;
import vektra.extrawindows.AboutWindow;
import vektra.extrawindows.CreateReport;
import vektra.extrawindows.DisplayImageWindow;
import vektra.extrawindows.EditReport;
import vektra.extrawindows.ModifyReport;
import vektra.resources.FilterConfiguration;
import vektra.resources.LocalResources;
import vektra.resources.R;


/**
 * Primary class for the Vektra Bug Reporter
 * This class includes all GUI aspects for the main window that gets used by all users.
 * No Bug modification or SQL queries are made in this
 * @author James
 *
 */
public class Vektra extends Application{
	
	public static Application APPLICATION;
	public static final String VERSION = "0.18";
	
	private Stage primaryStage;
	
	private ObservableList<BugItem> importedData;
	private BugItem selectedBug;
	
	private Button createReport;
	private Button editReport;
	private Button deleteReport;
	private Button reportLogin;
	private Button refresh;
	
	private Label loggedInName;
	private Label loggedInCurrentDate;
	private Label loggedInPing;
	private TableView<BugItem> bugs;
	private Rectangle priorityIndicator;
	private Label reportID;
	private Label tags;
	private Label priority;
	private Label version;
	private ImageView displayScreenshot;
	private FlowPane screenshotList;
	private ImageView selectedListImage;
	private Label openScreenshots;
	private TextArea message;
	private TableView<Comment> comments;
	private Comment selectedComment;
	private Button submitComment;
	private TextField enterComment;
	private Label whoLogged;
	private Label loggedDate;
	private Label whoUpdated;
	private Label updatedDate;
	private MenuItem loginMenuItem;
	private MenuItem signoutMenuItem;
	
	private RefreshThread refreshThread;
	private WindowCloseRequest closeRequest;
	
	private void setupStage(Stage primaryStage) {
		primaryStage.setTitle("VEKTRA - Bug Reporter v" + VERSION);
		primaryStage.setWidth(1024);
		primaryStage.setHeight(800);
		primaryStage.getIcons().add(new Image("v.jpg"));
	}

	/**
	 * Starting method that creates the main window and loads the primary font.
	 */
	public void start(Stage primaryStage) throws Exception {
		APPLICATION = this;
		
		DisplayImageWindow.setup();
		
		// Required in order to be able to use it in css's
		@SuppressWarnings("unused")
		final Font fontBold = Font.loadFont(Vektra.class.getClass().getResourceAsStream("/fonts/Microstyle Bold Extended ATT.ttf"), 20);
		final Font fontStandared = Font.loadFont(Vektra.class.getClass().getResourceAsStream("/fonts/Microstyle Extended ATT.ttf"), 20);
		
		this.primaryStage = primaryStage;
		setupStage(primaryStage);
		
		GridPane reportOptions = ReportOptionsGUI.create(primaryStage,this);
		Pane bugs = BugListGUI.create(primaryStage,this);
		GridPane screenshotList = ScreenShotDisplayGUI.create(primaryStage,this);
		GridPane messages = BugMessageGUI.create(primaryStage,this);
		MenuBar menu = setupMenu(primaryStage);
		
		GridPane mainLayout = new GridPane();
		GridPane.setColumnSpan(menu,3);
		mainLayout.addRow(0, reportOptions);
		GridPane.setColumnSpan(reportOptions,3);
		mainLayout.addRow(1, bugs);
		mainLayout.addColumn(1, screenshotList);
		mainLayout.addColumn(2, messages);
		
		BorderPane outter = new BorderPane();
		outter.getStylesheets().add("css/custom.css");
		outter.setTop(menu);
		outter.setCenter(mainLayout);
	    
		Scene scene = new Scene(outter);

		closeRequest = new WindowCloseRequest();
		primaryStage.setOnCloseRequest(closeRequest);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		// Ask to log in!
		login();
	}
	
	public void SubmitCommentButtonPressed(String commentWritten) {
		System.out.println("Submitting comment!");
		String text = enterComment.getText();
		if( text.isEmpty() ){
			return;
		}
		else if( submitComment.isDisabled() ){
			return;
		}
		
		boolean submitted = SQLData.submitComment(text,selectedBug);
		if( submitted ){
			enterComment.setText("");
			refreshThread.partialUpdate();
		}
		else{
			PopupError.show("Submit Comment", "Could not submit comment");
		}
	}

	
	
	/**
	 * Divides a timestamp into an array of integers
	 * Year
	 * Month
	 * Day
	 * Hour
	 * Minute
	 * Second
	 * @param item Timestamp from the database to split
	 * @return Array containing the entire timestamp
	 */
	private int[] splitDate(String item) {
		String[] date = item.split("\\s|[-/:]");
        
		int[] numbered = new int[date.length];
		numbered[0] = Integer.parseInt(date[0]); // year
		numbered[1] = Integer.parseInt(date[1]); // Month
		numbered[2] = Integer.parseInt(date[2]); // Day
		numbered[3] = Integer.parseInt(date[3]); // Hour
		numbered[4] = Integer.parseInt(date[4]); // Minute
		
		// Seconds sometimes have decimals. We want to remove them
        if( date[5].contains(".") ){
        	date[5] = date[5].substring(0, date[5].indexOf("."));
        }
		numbered[5] = Integer.parseInt(date[5]); // Second
		
		return numbered;
	}

	/**
	 * Given an amount of data that was received from a database.
	 * We want to update the visual representation of the data on the GUI
	 * 
	 * @param loadedData New data loaded off a website
	 * @param length time taken to load the data
	 * @param fullUpdate 
	 * @return 
	 */
	public Thread refreshData(final ObservableList<BugItem> loadedData, final String currentTime, final long length, final boolean fullUpdate) {
		System.out.println("Refresh Time: " + length);

		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {				
//				int[] date = splitDate(currentTime);
//				Calendar c = new Calendar.Builder().setDate(date[0], date[1], date[2]).setTimeOfDay(date[3], date[4], date[5]).build();
//
//				String displayDate = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " +
//									c.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.getDefault()) + " @ " +
//									c.getDisplayName(Calendar.HOUR, Calendar.LONG, Locale.getDefault()) + ":" +
//									c.getDisplayName(Calendar.MINUTE, Calendar.LONG, Locale.getDefault()) + ":" +
//									c.getDisplayName(Calendar.SECOND, Calendar.LONG, Locale.getDefault());
				
				// Update Basic GUI
				loggedInCurrentDate.setText(currentTime);
				loggedInPing.setText(String.valueOf(length) + "ms");
				
				// Don't do anything if they are the same
				if( loadedData == null || loadedData.isEmpty() ){
					return;
				}
				
				// Assign new values in the table
				int bugsEdited = 0;
				if( fullUpdate ){
					
					// Save local data
					importedData = loadedData;
					bugsEdited = loadedData.size();
				}
				else{
					System.out.println("PARTIAL update");
					
					setRefreshButtonText("REFRESHING...\nUpdating Data");
					
					// Modify the data in the currently save
					for(BugItem newBug : loadedData){
						int index = importedData.indexOf(newBug);
						
						// Only highlight if it isn't the bug we are currently selecting
						if( selectedBug != null && selectedBug.ID != newBug.ID ){
							newBug.hasBeenUpdated = true;
						}
						
						if( index == -1 ){
							if( newBug.message == null ){
								System.out.println("Ignoring null Bug " + newBug.ID);
								continue;
							}
							System.out.println("Adding bug " + newBug.ID);
							
							// Add new item to bug
							importedData.add(0,newBug);
							
							bugsEdited++;
							
							// Add local images
							R.addImages(newBug.getImages());
						}
						else if( newBug.message == null ){
							System.out.println("Deleting bug " + newBug.ID);
							if( selectedBug != null && selectedBug.equals(newBug) ){
								System.out.println("Deselecting bug");
								deselectBug();
							}
							
							// Remove from list
							BugItem removed = importedData.remove(index);
							
							// Delete local images
							R.removeImages(removed.getImages());

							bugsEdited++;
						}
						else{
							System.out.println("Updating bug " + newBug);
							
							// Replace bug
							BugItem oldBug = importedData.set(index,newBug);
							
							// Get images that were deleted
							List<BugImage> deletedImages = new ArrayList<BugImage>(oldBug.getImages());
							deletedImages.removeAll(newBug.getImages());
							
							// Get images that were added
							List<BugImage> addedImages = new ArrayList<BugImage>(newBug.getImages());
							addedImages.removeAll(oldBug.getImages());
							
							R.removeImages(deletedImages);
							R.addImages(addedImages);
							bugsEdited++;
						}
					}
					setRefreshButtonText("REFRESHING...\nRefreshing GUI");
				}

				// Only setup if we edited something
				if( bugsEdited == 0 ){
					System.out.println("Bugs Edited: " + bugsEdited);
					return;
				}
				
				BugListGUI.setupColumns(importedData, bugs, Vektra.this);

				// Set it up for 
				ModifyReport.assignVersions(importedData);
				
				// Reselect
				BugItem old = deselectBug();
				if( old != null ){
					System.out.println("Reselecting bug");
					
					// Get new bug from new data
					int index = loadedData.indexOf(old);
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

				setRefreshButtonText("REFRESH");
			}
			
		});
		Platform.runLater(t);
		return t;
	}

	protected void assignUpdated(ObservableList<BugItem> oldData, ObservableList<BugItem> newData) {
		
		// TODO 
		// TODO Need to perform a deep contains function!
		// TODO 
		
//		for(BugItem i : newData){
//			if( Collections.)
//			
//		}
		
	}

	/**
	 * Deselects the current bug in the bug list
	 * @return bug that was originally selected
	 */
	protected BugItem deselectBug() {
		if( selectedBug == null ){
			return null;
		}
		
		selectedBug.dispose();
		
    	screenshotList.getChildren().clear();
    	displayScreenshot.setImage(null);
    	message.setText("");
    	reportID.setText("");
    	version.setText("");
    	tags.setText("");
    	priority.setText("");
    	priorityIndicator.setFill(Color.BLACK);
    	
    	BugItem oldBug = selectedBug;
    	selectedBug = null;
    	return oldBug;
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
			
			// Deselect old bug
			deselectBug();
			
			
            selectedBug = bug;
            openScreenshots.setText("(" + bug.imageMap.size() + ")");
            message.setText(bug.message);
            reportID.setText(String.valueOf(bug.ID));
            
            int i = 0;
            String tagString = "";
            for(Tag tag : bug.tags){ 
            	tagString += tag.message;
            	if( (++i) < bug.tags.size() ){
            		tagString += ", ";
            	}
            }
            
            tags.setText(tagString);
            priority.setText(bug.getPriority().toString());
            
            
            if( bug.getStatus() == Status.FIXED ){
            	priorityIndicator.setFill(Color.GREEN);
            }
            else{
            	priorityIndicator.setFill(bug.priority.getColor());
            }
            
            whoLogged.setText(bug.who == null ? "-" : bug.who);
            loggedDate.setText(bug.date == null ? "-" : bug.date);
            whoUpdated.setText(bug.whoUpdated == null ? "-" : bug.whoUpdated);
            updatedDate.setText(bug.lastUpdate == null ? "-" : bug.lastUpdate);
            version.setText(bug.version == null ? "-" : bug.version.stage + " " + bug.version.version + " " + bug.version.bit + "B");

            // Clear images and add new ones if there are some
        	screenshotList.getChildren().clear();
        	displayScreenshot.setImage(null);
            if( !bug.getImages().isEmpty() ){

            	i = 0;
            	ScreenShotListListener listener = new ScreenShotListListener();
            	for( BugImage bImage : bug.imageMap.values() ){
            		ImageView v = bImage.cloneView();
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
            
            // Apply comments
            BugMessageGUI.setupComments(bug.getComments(), comments, this);
		}
	}

	/**
	 * Sets up the menu bar on startup with the required file/edit/view... etc items.
	 * @param mainLayout Layout to add the items to.
	 * @param primaryStage What stage to add the menu to.
	 */
	private MenuBar setupMenu(Stage primaryStage) {
		
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
		
		//Menu edit = new Menu("Edit");
		//Menu view = new Menu("View");
		Menu report = new Menu("Report");
		MenuItem createReport = new MenuItem("Create Report");
		report.getItems().add(createReport);
		createReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				CreateReport.display(-1, Vektra.this);
			}
			
		});
		MenuItem editReport = new MenuItem("Edit Report");
		report.getItems().add(editReport);
		editReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				EditReport.display(selectedBug, Vektra.this);
			}
			
		});
		MenuItem deleteReport = new MenuItem("Delete Report");
		report.getItems().add(deleteReport);
		deleteReport.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				deleteCurrentBug();
			}
			
		});
		
		Menu help = new Menu("Help");
		MenuItem about = new MenuItem("About");
		help.getItems().add(about);
		about.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				AboutWindow.show();
			}
			
		});
		
		menuBar.getMenus().add(file);
		//menuBar.getMenus().add(edit);
		//menuBar.getMenus().add(view);
		menuBar.getMenus().add(report);
		menuBar.getMenus().add(help);

		return menuBar;		
	}
	
	/**
	 * Signs out of the database
	 * If we are not signed in, it will still reset all GUI appropriately.
	 */
	public void signOut() {
		reportLogin.setDisable(true);
		
		// Change GUI
		loginMenuItem.setVisible(true);
		signoutMenuItem.setVisible(false);
		loggedInName.setText("-");
		loggedInPing.setText("-");
		loggedInCurrentDate.setText("-");
		reportLogin.setText("LOGIN");
		bugs.getItems().clear();
		
		deselectBug();
		
		createReport.setDisable(true);
		editReport.setDisable(true);
		deleteReport.setDisable(true);
		refresh.setDisable(true);
		
		// Disconnect from server
		if( SQLData.isConnected() ){
			
			// Stop refreshing
			closeRequest.removeThread();
			SQLData.close();

			// Tell the user we logged in!
			PopupMessage.show("Sign Out","Logged Out Successfully!\nGood Bye " + SQLData.getUsername() + "!");
		}		
		reportLogin.setDisable(false);
	}

	/**
	 * Calls LoginDialog which grabs the information from the user and attempts to log in with it.
	 * If the log in is successful. A refresh thread will begin and a message will appear.
	 */
	public void login() {

		reportLogin.setDisable(true);
		
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
				// Do nothing till it dies
			}
		}
		
		//Platform.runLater(refreshThread);
		//Platform.setImplicitExit(false);
		
		loggedInName.setText(capitilize(SQLData.getUsername()));		
		loginMenuItem.setVisible(false);
		signoutMenuItem.setVisible(true);
		createReport.setDisable(false);
		editReport.setDisable(false);
		deleteReport.setDisable(false);
		refresh.setDisable(false);
		reportLogin.setDisable(false);
		reportLogin.setText("SIGN OUT");
		
		deselectBug();
		bugs.getItems().clear();
		

		
		// Start thread
		refreshThread = new RefreshThread();
		refreshThread.start();
		
		// Change UI to reflect being logged in
		closeRequest.setThread(refreshThread);
		
		// Tell the user we logged in!
		PopupMessage.show("Login","Login Successful!\nWelcome " + SQLData.getUsername() + "!");
		
	}

	/**
	 * Capitilizes the first letter of the string. Will return the given string if empty or null.
	 * @param username
	 * @return
	 */
	private String capitilize(String username) {
		if( username == null || username.isEmpty() ){
			return username;
		}
		else if( username.length() == 1 ){
			return username.substring(0,1).toUpperCase();
		}
		return username.substring(0,1).toUpperCase() + username.substring(1);
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
		if( !isDeleted ){
			PopupError.show("Delete Failed", "Could not delete the Bug with ID '" + item.ID + "'");
		}
		
		// Delete properly
		deselectBug();
		PopupMessage.show("Delete Completed", "Successfully deleted Bug with ID '" + item.ID + "'");
		refreshThread.partialUpdate();
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
	
	public void refreshBugs(){
		BugListGUI.filterBugs(importedData, bugs, this);
	}
	
	/**
	 * When the refresh button is pressed. It will reset the timer on the refresh thread which will then refresh the data.
	 * @author James
	 *
	 */
	public class Refresh implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			if( refreshThread == null || !refreshThread.isAlive() ){
				PopupError.show("Can not refresh!", "Not logged in?");
			}
			
			refreshThread.fullRefresh();
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
	 * Constantly running thread in the background checking if the database has any new updates
	 * @author James
	 *
	 */
	private class RefreshThread extends Thread {
		
		// Time in between refreshes
		private static final int REFRESHDELAY = 10000;
		
		// Amoutn of times we have refreshed
		private int refreshCount = 0;
		
		// Next possible time we can refresh
		private long time = 0;
		
		// Boolean to allow us to continue running or not.
		private boolean running = true;
		
		@Override
		public void run() {
			
			// When we first start refreshing
			// Load all the local resources!
			LocalResources.loadLocalImages();
			while(running){
				
				// Only check every so often
				if( time < System.currentTimeMillis() ){
					
					
					// Disable refresh button
					Thread disable = new Thread(()->{
							refresh.setDisable(true);
							submitComment.setDisable(true);
							refresh.setText("REFRESHING...\nGetting Data");
					});
					Platform.runLater(disable);
					
					
					// Record when we tried getting the new data
					long start = System.currentTimeMillis();
					

					// Increase refresh counter and store if this was a full update or not
					boolean fullUpdate = refreshCount++ == 0; 
					
					// Get the data from the database
					SQLData.DatabaseData loadedData;
					setRefreshButtonText("REFRESHING...\nGetting Data");
					if( fullUpdate ){
						
						// Get all the data off the server
						loadedData = SQLData.getData();
						
						setRefreshButtonText("REFRESHING...\nSynching Images");
						
						// Save local images
						LocalResources.synchronizeLocalImages(loadedData.images.values());
						
						setRefreshButtonText("REFRESHING...\n");
					}
					else{
						loadedData = SQLData.getUpdatedData();
						
						// Do not need to synchronizeLocalImages here as it's done in the refreshData method
					}
					
					
					// Get the end time
					long end = System.currentTimeMillis();
					
					// Get how long it took to pull the data
					long length = end-start;
					
					// Only do it if we loaded data
					if( loadedData != null ){
					
						// Refresh the GUI
						Thread refreshedData = refreshData(loadedData.data, SQLData.retrieveCurrentTime(), length, fullUpdate);
						
						// Enable refresh button
						while(disable != null && disable.isAlive() && refreshedData != null && refreshedData.isAlive() ){}
						Thread enable = new Thread(()->{
								refresh.setDisable(false);
								submitComment.setDisable(false);
								refresh.setText("REFRESH");							
						});
						Platform.runLater(enable);
					}
					
					// Reset the timer
					time = System.currentTimeMillis() + REFRESHDELAY;
				}
				
			}
			System.out.println("No longer refreshing!");
		}
		
		public void partialUpdate() {
			time = 0;
		}

		/**
		 * Performs a full refresh on the data collecting everything
		 */
		public void fullRefresh() {
			time = 0;
			refreshCount = 0;
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
			
			// Close all Display Images
			DisplayImageWindow.closeAllWindows();
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


	/**
	 * This method is called when the user clicks on the large display picture.
	 */
	public void displayImageClicked() {
		System.out.println("CLICKED");
		DisplayImageWindow.show(primaryStage, displayScreenshot);
	}
	
	public void filterBugs(){
		System.out.println("Filter");
		BugListGUI.setupColumns(FilterConfiguration.filter(importedData), bugs, this);
	}
	
	public void setRefreshButtonText(String string) {
		Platform.runLater(()->refresh.setText(string));
	}

	public static void main(String[] args){
		launch(args);
	}

	/**
	 * @param createReport the createReport to set
	 */
	public void setCreateReport(Button createReport) {
		this.createReport = createReport;
	}

	/**
	 * @param editReport the editReport to set
	 */
	public void setEditReport(Button editReport) {
		this.editReport = editReport;
	}

	/**
	 * @param deleteReport the deleteReport to set
	 */
	public void setDeleteReport(Button deleteReport) {
		this.deleteReport = deleteReport;
	}

	/**
	 * @param bugs the bugs to set
	 */
	public void setBugs(TableView<BugItem> bugs) {
		this.bugs = bugs;
	}

	/**
	 * @param submitComment the submitComment to set
	 */
	public void setSubmitComment(Button submitComment) {
		this.submitComment = submitComment;
	}

	public BugItem getSelectedBug() {
		return selectedBug;
	}

	public void setReportLogin(Button reportLogin2) {
		reportLogin = reportLogin2;
	}

	public void setRefresh(Button refresh2) {
		refresh = refresh2;
	}

	public void setLoggedInName(Label loggedInName2) {
		loggedInName = loggedInName2;
	}

	public void setLoggedInPing(Label loggedInPing2) {
		loggedInPing = loggedInPing2;
	}

	public void setLoggedInCurrentDate(Label loggedInCurrentDate2) {
		loggedInCurrentDate = loggedInCurrentDate2;
	}

	public void performFullRefresh() {
		if( refreshThread == null || !refreshThread.isAlive() ){
			PopupError.show("Can not refresh!", "Not logged in?");
		}
		
		BugItem selectedBug = deselectBug();
		refreshThread.fullRefresh();
	}

	public void performPartialRefresh() {
		if( refreshThread == null || !refreshThread.isAlive() ){
			PopupError.show("Can not refresh!", "Not logged in?");
		}
		
		refreshThread.partialUpdate();
	}
	
	public void setPriorityIndicator(Rectangle priorityIndicator2) {
		priorityIndicator = priorityIndicator2;
	}

	public void setScreenshotList(FlowPane screenshotList2) {
		screenshotList = screenshotList2;
	}

	public void setDisplayScreenshot(ImageView displayScreenshot2) {
		displayScreenshot = displayScreenshot2;
	}

	public void setVersion(Label version2) {
		version = version2;
	}

	public void setPriority(Label priority2) {
		priority = priority2;
	}

	public void setTags(Label tags2) {
		tags = tags2;
	}

	public void setReportID(Label reportID2) {
		reportID = reportID2;
	}

	public void setUpdatedDate(Label updatedDate2) {
		updatedDate = updatedDate2;
	}

	public void setWhoUpdated(Label whoUpdated2) {
		whoUpdated = whoUpdated2;
	}

	public void setLoggedDate(Label loggedDate2) {
		loggedDate = loggedDate2;
	}

	public void setWhoLogged(Label whoLogged2) {
		whoLogged = whoLogged2;
	}

	public void setEnterComment(TextField enterComment2) {
		enterComment = enterComment2;
	}

	public void setComments(TableView<Comment> comments2) {
		comments = comments2;
	}

	public void setMessage(TextArea message2) {
		message = message2;
	}

	public void setSelectedComment(Comment object) {
		selectedComment = object;
	}

	public Comment getSelectedComment() {
		return selectedComment;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setOpenScreenshots(Label openScreenshots2) {
		openScreenshots = openScreenshots2;
	}

	
}
