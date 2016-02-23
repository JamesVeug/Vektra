package vektra;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
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
	public static final String VERSION = "0.16";
	
	private Stage primaryStage;
	
	private ObservableList<BugItem> importedData;
	private BugItem selectedBug;
	
	private Button createReport;
	private Button editReport;
	private Button deleteReport;
	private Button openID;
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
	
	/**
	 * Starting method that creates the main window and loads the primary font.
	 */
	public void revampedStart(Stage primaryStage) throws Exception {
		
		APPLICATION = this;
		
		// Required in order to be able to use it in css's
		@SuppressWarnings("unused")
		final Font font = Font.loadFont(Vektra.class.getClass().getResourceAsStream("/fonts/Microstyle Bold Extended ATT.ttf"), 20);
		
		this.primaryStage = primaryStage;
		setupStage(primaryStage);
		
		GridPane reportOptions = ReportOptionsGUI.create(primaryStage,this);
		TableView<BugItem> bugs = BugListGUI.create(primaryStage,this);
		GridPane screenshotList = ScreenShotDisplayGUI.create(primaryStage,this);
		GridPane messages = BugMessageGUI.create(primaryStage,this);
		MenuBar menu = setupMenu(primaryStage);
		
		
		GridPane mainLayout = new GridPane();
//		mainLayout.addRow(0, menu);
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
	
	private void setupStage(Stage primaryStage) {
		primaryStage.setTitle("VEKTRA - Bug Reporter");
		primaryStage.setWidth(1024);
		primaryStage.setHeight(800);
		primaryStage.getIcons().add(new Image("v.jpg"));
	}

	/**
	 * Starting method that creates the main window and loads the primary font.
	 */
	public void start(Stage primaryStage) throws Exception {
		revampedStart(primaryStage);
		if( true ){
			return;
		}
//		APPLICATION = this;
//		
//		// Required in order to be able to use it in css's
//		final Font font = Font.loadFont(Vektra.class.getClass().getResourceAsStream("/fonts/Microstyle Bold Extended ATT.ttf"), 20);
//		
//		this.primaryStage = primaryStage;
//		setupStage(primaryStage);
//		
//		
//		
//		GridPane mainLayout = new GridPane();
//		//mainLayout.getStylesheets().add("css/custom.css");
//		mainLayout.setBackground(new Background(new BackgroundFill(Color.RED, new  CornerRadii(0), new Insets(0))));
////		mainLayout.topProperty().
//
//		
//		setupMenu(mainLayout, primaryStage);
//		
//		
//		// Top
//		GridPane options = ReportOptionsGUI.create(primaryStage, this);
//		
//
//		bugs = BugListGUI.create(primaryStage, this);
//		
//		
//		// Middle area
//		GridPane screenshotlayout = ScreenShotDisplayGUI.create(primaryStage, this);
//		
//		// Bottom right
//		
//		GridPane bugListAndInfolayout = BugMessageGUI.create(primaryStage, this);
//
//		mainLayout.addRow(1,options);
//		mainLayout.addRow(2,bugListAndInfolayout);
//		
//		GridPane testGrid = new GridPane();
//		testGrid.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(0), new Insets(0))));
//		
//		
//		StackPane outter = new StackPane();
//		outter.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(0), new Insets(0))));
//		outter.getChildren().add(mainLayout);
//	    
//		Scene scene = new Scene(outter);
//
//		closeRequest = new WindowCloseRequest();
//		primaryStage.setOnCloseRequest(closeRequest);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//		
//		
//		// Ask to log in!
//		login();
//	}
//	
//
//	@SuppressWarnings("unchecked")
//	private void setupComments(Collection<Comment> currentComments) {
//		
//		comments.getItems().clear();
//		if( !currentComments.isEmpty() ){
//			ObservableList<Comment> list = FXCollections.observableArrayList(currentComments);
//			comments.setItems(list);
//			
//			Collections.sort(list,new Comparator<Comment>(){
//
//				@Override
//				public int compare(Comment one, Comment two) {
//					return one.timePosted.compareTo(two.timePosted);
//				}
//				
//			});
//		}
//		
//		// Menu for when the user right clicks a comment
//		final ContextMenu contextMenu = getCommentPopupMenu();
//		
//		
//		TableColumn<Comment, String> commenterColumn = new TableColumn<Comment,String>("POSTER");
//		commenterColumn.setSortable(false);
//		commenterColumn.setMinWidth(80);
//		commenterColumn.setMaxWidth(80);
//		commenterColumn.setCellValueFactory(new PropertyValueFactory<Comment, String>("poster"));
//		commenterColumn.setCellFactory(new Callback<TableColumn<Comment, String>, TableCell<Comment, String>>() {
//	        public TableCell<Comment, String> call(TableColumn<Comment, String> param) {
//	            return new TableCell<Comment, String>() {
//
//	                @Override
//	                public void updateItem(String item, boolean empty) {
//	                    super.updateItem(item, empty);
//	                    if (!isEmpty()) {
//	                        this.getStylesheets().add("css/buglist.css");
//	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new CommentCellListener());
//	                		this.setContextMenu(contextMenu);
//	                        
//	                        setText(item);
//	                        
//	                    }
//	                }
//	            };
//	        }
//	    });
//		if( !comments.getColumns().isEmpty() ){
//			commenterColumn.setSortType(comments.getColumns().get(0).getSortType());
//		}
//		
//		TableColumn<Comment, String> messageColumn = new TableColumn<Comment, String>("MESSAGE");
//		messageColumn.setSortable(false);
//		messageColumn.setCellValueFactory(new PropertyValueFactory<Comment, String>("message"));
//		messageColumn.setCellFactory(new Callback<TableColumn<Comment, String>, TableCell<Comment, String>>() {
//
//		        @Override
//		        public TableCell<Comment, String> call(TableColumn<Comment, String> param) {
//		            TableCell<Comment, String> cell = new TableCell<Comment, String>(){
//		            	@Override
//		                public void updateItem(String item, boolean empty) {
//		                    super.updateItem(item, empty);
//		                    if (!isEmpty()) {
//		                        setText(item);
//		                    }
//		                }
//		            };
//		            Text text = new Text();
//		            text.getStyleClass().add("table-cell");
//		            cell.setGraphic(text);
//		            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//		            cell.getStylesheets().add("css/buglist.css");
//		            text.wrappingWidthProperty().bind(cell.widthProperty());
//		            text.textProperty().bind(cell.itemProperty());
//		            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new CommentCellListener());
//		            cell.setContextMenu(contextMenu);
//		            return cell ;
//		        }
//        });
//		if( !comments.getColumns().isEmpty() ){
//			messageColumn.setSortType(comments.getColumns().get(1).getSortType());
//		}
//		
//		@SuppressWarnings("rawtypes")
//		TableColumn sorting = comments.getSortOrder().isEmpty() ? null : comments.getSortOrder().get(0);
//		if( sorting == null ){
//			// Ignore
//		}
//		else if( sorting == comments.getColumns().get(2) ){
//			comments.getSortOrder().set(0, commenterColumn);
//		}
//		else if( sorting == comments.getColumns().get(2) ){
//			comments.getSortOrder().set(0, messageColumn);
//		}
//		
//		comments.getColumns().clear();
//		comments.getColumns().addAll(commenterColumn,messageColumn);	
//		comments.sort();
		
//		messageColumn.setPrefWidth(320);
		//messageColumn.minWidthProperty().bind(commentScroll.widthProperty());
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
			PopupError.show("Submit Commeny", "Could not submit comment");
		}
	}

	


	/**
	 * Sets up the table with all the bugs we currently have sorted
	 * HACK HACK HACK
	 * Recreates all the columns
	 */
	@SuppressWarnings("unchecked")
	private void setupTable() {
    	final Background GreenBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(0), new Insets(5)));
    	
    	
		// Bottom Left ( BUG LIST )
		TableColumn<BugItem, Priority> priorityColumn = new TableColumn<BugItem, Priority>("P");
		priorityColumn.setMinWidth(20);
		priorityColumn.setMaxWidth(20);
		priorityColumn.setCellValueFactory(new PropertyValueFactory<BugItem, Priority>("priority"));
		priorityColumn.setCellFactory(new Callback<TableColumn<BugItem, Priority>, TableCell<BugItem, Priority>>() {
	        public TableCell<BugItem, Priority> call(TableColumn<BugItem, Priority> param) {
	            return new TableCell<BugItem, Priority>() {
	            	

	                @Override
	                public void updateItem(Priority priority, boolean empty) {
	                    super.updateItem(priority, empty);
	                    
	                    if (!isEmpty()) {

		                    int index = getIndex();
		            		BugItem item = importedData.get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new BugListListener());
	                        
	                        if( item.getStatus() == Status.FIXED ){
		                        setBackground(GreenBackground);
	                        	
	                        }
	                        else{
		                        setBackground(new Background(new BackgroundFill(priority.getColor(), new CornerRadii(0), new Insets(5))));
	                        	
	                        }
	                    }
	                }
	            };
	        }
	    });
		if( !bugs.getColumns().isEmpty() ){
			priorityColumn.setSortType(bugs.getColumns().get(0).getSortType());
		}
		
		// Bottom Left ( BUG LIST )
		TableColumn<BugItem, Integer> idColumn = new TableColumn<BugItem, Integer>("ID");
		idColumn.setMinWidth(40);
		idColumn.setMaxWidth(40);
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
		if( !bugs.getColumns().isEmpty() ){
			idColumn.setSortType(bugs.getColumns().get(1).getSortType());
		}
		
		TableColumn<BugItem, Status> statusColumn = new TableColumn<BugItem, Status>("STATUS");
		statusColumn.setMinWidth(50);
		statusColumn.setMaxWidth(50);
		statusColumn.setCellValueFactory(new PropertyValueFactory<BugItem, Status>("status"));
		statusColumn.setCellFactory(new Callback<TableColumn<BugItem, Status>, TableCell<BugItem, Status>>() {
	        public TableCell<BugItem, Status> call(TableColumn<BugItem, Status> param) {
	            return new TableCell<BugItem, Status>() {

	                @Override
	                public void updateItem(Status item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new BugListListener());
	                        
	                        
	                        setText(item.toString());
	                        
	                    }
	                }
	            };
	        }
	    });
		if( !bugs.getColumns().isEmpty() ){
			statusColumn.setSortType(bugs.getColumns().get(2).getSortType());
		}
		
		TableColumn<BugItem, String> updateColumn = new TableColumn<BugItem, String>("UPDATED");
		updateColumn.setCellValueFactory(new PropertyValueFactory<BugItem, String>("lastUpdate"));
		updateColumn.setCellFactory(new Callback<TableColumn<BugItem, String>, TableCell<BugItem, String>>() {
	        public TableCell<BugItem, String> call(TableColumn<BugItem, String> param) {
	            return new TableCell<BugItem, String>() {

	                @Override
	                public void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new BugListListener());
	                        
	                        int[] date = splitDate(item);
	                        
//	                        System.out.println("SPLIT DATE: " + item);
//	                        for(String s : date){
//	                        	System.out.println("\t"+s);
//	                        }
	                        
	                        String displayDate = date[1] + "-" + date[2] + " " + date[3] + ":" + date[4] + ":" + date[5];
	                        
	                        setText(displayDate);
	                        
	                    }
	                }
	            };
	        }
	    });
		if( !bugs.getColumns().isEmpty() ){
			updateColumn.setSortType(bugs.getColumns().get(3).getSortType());
		}
		
		@SuppressWarnings("rawtypes")
		TableColumn sorting = bugs.getSortOrder().isEmpty() ? null : bugs.getSortOrder().get(0);
		if( sorting == null ){
			// Ignore
		}
		else if( sorting == bugs.getColumns().get(0) ){
			bugs.getSortOrder().set(0, priorityColumn);
		}
		else if( sorting == bugs.getColumns().get(1) ){
			bugs.getSortOrder().set(0, idColumn);
		}
		else if( sorting == bugs.getColumns().get(2) ){
			bugs.getSortOrder().set(0, statusColumn);
		}
		else if( sorting == bugs.getColumns().get(2) ){
			bugs.getSortOrder().set(0, updateColumn);
		}
		
		bugs.getColumns().clear();
		bugs.getColumns().addAll(priorityColumn, idColumn, statusColumn,updateColumn);	
		bugs.sort();
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
	 */
	public void refreshData(final ObservableList<BugItem> loadedData, final String currentTime, final long length, final boolean fullUpdate) {
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
					System.out.println("NO CHANGE");
					return;
				}
				
				// Assign new values in the table
				if( fullUpdate ){
					bugs.setItems(loadedData);
					
					// Save local data
					importedData = loadedData;
				}
				else{
					System.out.println("PARTIAL update");
					
					// Modify the data in the currently save
					for(BugItem newBug : loadedData){
						int index = importedData.indexOf(newBug);
						System.out.println("Index " + index);
						System.out.println("BUG INFO" + newBug.toString());
						if( index == -1 ){
							System.out.println("Adding bug " + newBug.ID);
							
							// Add new item to bug
							importedData.add(0,newBug);
							
							// Add local images
							R.addImages(newBug.getImages());
						}
						else if( newBug.message == null ){
							System.out.println("Deleting bug");
							if( selectedBug.equals(newBug) ){
								System.out.println("Deselecting bug");
								deselectBug();
								selectedBug = null;
							}
							
							// Remove from list
							BugItem removed = importedData.remove(index);
							
							// Delete local images
							R.removeImages(removed.getImages());
							
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
						}
					}
				}
				
				setupTable();
				
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
		
		
		
	}

	/**
	 * Deselects the current bug in the bug list
	 */
	protected void deselectBug() {
		if( selectedBug == null ){
			return;
		}

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
            version.setText(bug.version == null ? "-" : bug.version.stage + " " + bug.version.version);

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
				createReport();
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


	public void createReport() {
		int maxID = importedData == null ? -1 : importedData.get(importedData.size()-1).ID;
		CreateReport.display(maxID);
	}
	
	/**
	 * Signs out of the database
	 * If we are not signed in, it will still reset all GUI appropriately.
	 */
	protected void signOut() {
		
		// Change GUI
		loginMenuItem.setVisible(true);
		signoutMenuItem.setVisible(false);
		loggedInName.setText("-");
		loggedInPing.setText("-");
		loggedInCurrentDate.setText("-");
		bugs.getItems().clear();
		
		createReport.setDisable(true);
		editReport.setDisable(true);
		deleteReport.setDisable(true);
		openID.setDisable(true);
		refresh.setDisable(true);
		
		// Disconnect from server
		if( SQLData.isConnected() ){
			
			// Stop refreshing
			closeRequest.removeThread();
			SQLData.close();

			// Tell the user we logged in!
			PopupMessage.show("Sign Out","Logged Out Successfully!\nGood Bye " + SQLData.getUsername() + "!");
		}		
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
				// Do nothing till it dies
			}
		}
		
		//Platform.runLater(refreshThread);
		//Platform.setImplicitExit(false);
		
		loggedInName.setText(SQLData.getUsername());		
		loginMenuItem.setVisible(false);
		signoutMenuItem.setVisible(true);
		createReport.setDisable(false);
		editReport.setDisable(false);
		deleteReport.setDisable(false);
		openID.setDisable(false);
		refresh.setDisable(false);
		
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
		if( !isDeleted ){
			PopupError.show("Delete Failed", "Could not delete the Bug with ID '" + item.ID + "'");
		}
		
		// Delete properly
		deselectBug();
		PopupMessage.show("Delete Completed", "Successfully deleted Bug with ID '" + item.ID + "'");
		refreshThread.partialUpdate();
	}
	
	public void editCurrentBug() {
		EditReport.display(selectedBug);
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
			
			refreshThread.fullRefreshTime();
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
					Thread disable = new Thread(new Runnable(){

						@Override
						public void run() {
							refresh.setDisable(true);
							submitComment.setDisable(true);
							refresh.setText("REFRESHING...");
						}
						
					});
					Platform.runLater(disable);
					
					
					// Record when we tried getting the new data
					long start = System.currentTimeMillis();
					

					// Increase refresh counter and store if this was a full update or not
					boolean fullUpdate = refreshCount++ == 0; 
					
					// Get the data from the database
					ObservableList<BugItem> loadedData;
					if( fullUpdate ){
						loadedData = SQLData.getData();
						
						// Save local images
						LocalResources.synchronizeLocalImages(loadedData);
					}
					else{
						loadedData = SQLData.getUpdatedData();
						
						// Do not need to synchronizeLocalImages here as it's done in the refreshData method
					}
					
					
					// Get the end time
					long end = System.currentTimeMillis();
					
					// Get how long it took to pull the data
					long length = end-start;
					
					
					// Refresh the GUI
					refreshData(loadedData, SQLData.retrieveCurrentTime(), length, fullUpdate);
					
					// Disable refresh button
					while(disable != null && disable.isAlive() ){}
					Thread enable = new Thread(new Runnable(){

						@Override
						public void run() {
							refresh.setDisable(false);
							submitComment.setDisable(false);
							refresh.setText("REFRESH");
						}
						
					});
					Platform.runLater(enable);
					
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
		public void fullRefreshTime() {
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

	public void setOpenID(Button openID2) {
		openID = openID2;
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

	public void performfullRefresh() {
		if( refreshThread == null || !refreshThread.isAlive() ){
			PopupError.show("Can not refresh!", "Not logged in?");
		}
		
		refreshThread.fullRefreshTime();
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
