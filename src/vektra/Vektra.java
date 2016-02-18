package vektra;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.TableColumn.SortType;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import vektra.dialogs.LoginDialog;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;
import vektra.extrawindows.AboutWindow;
import vektra.extrawindows.CreateReport;
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
	public static final String VERSION = "0.1";
	
	@SuppressWarnings("unused")
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
	private StackPane screenshotPane;
	private ImageView displayScreenshot;
	private Image logo;
	private FlowPane screenshotList;
	private ImageView selectedListImage;
	private Label openScreenshots;
	private TextArea message;
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
	public void start(Stage primaryStage) throws Exception {
		APPLICATION = this;
		
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

		
		GridPane layout = new GridPane();
		
		
		// Top
		GridPane options = new GridPane();
		options.setStyle("-fx-border-width: 1, 1; -fx-border-color: #FFFFFF");
		options.setPrefHeight(105);
		options.setMaxHeight(130);
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.setPadding(new Insets(5,5,5,5));
		buttonGrid.setVgap(5);
		
		createReport = new Button("CREATE\nREPORT");
		createReport.setTextAlignment(TextAlignment.CENTER);
		createReport.setOnAction(new MenuItemCreateReport());
		createReport.getStyleClass().add("button_create");
		createReport.setPrefWidth(85);
		createReport.setPrefHeight(65);
		createReport.setDisable(true);
		
		editReport = new Button("EDIT");
		editReport.setTextAlignment(TextAlignment.CENTER);
		editReport.setOnAction(new EditReportButtonPressed());
		editReport.getStyleClass().add("button_edit");
		editReport.setPrefWidth(85);
		editReport.setPrefHeight(50);
		editReport.setDisable(true);
		
		deleteReport = new Button("DELETE");
		deleteReport.setTextAlignment(TextAlignment.CENTER);
		deleteReport.setOnAction(new DeleteReport());
		deleteReport.getStyleClass().add("button_delete");
		deleteReport.setPrefWidth(85);
		deleteReport.setPrefHeight(50);
		deleteReport.setDisable(true);
		
		buttonGrid.addRow(0, createReport);
		buttonGrid.addRow(1, editReport);
		buttonGrid.addRow(2, deleteReport);
		
		
		GridPane extraButtonGrid = new GridPane();
		extraButtonGrid.setPadding(new Insets(5,5,5,0));
		extraButtonGrid.setVgap(5);
		
		openID = new Button("OPEN ID");
		openID.setOnAction(new OpenID());
		openID.getStyleClass().add("button_extra");
		openID.setPrefWidth(150);
		openID.setPrefHeight(50);
		openID.setDisable(true);
		refresh = new Button("REFRESH");
		refresh.setOnAction(new Refresh());
		refresh.getStyleClass().add("button_extra");
		refresh.setPrefWidth(150);
		refresh.setPrefHeight(50);
		refresh.setDisable(true);
		
		extraButtonGrid.addRow(0, openID);
		extraButtonGrid.addRow(1, refresh);
		
		
		GridPane loggedGrid = new GridPane();
		loggedGrid.alignmentProperty().set(Pos.TOP_RIGHT);
			Label loggedInLabel = new Label("User: ");
			loggedInLabel.getStyleClass().add("loggedLabel");
			loggedInName = new Label("-");
			loggedInName.getStyleClass().add("loggedLabel");
			
			Label loggedInPingLabel = new Label("Ping: ");
			loggedInPingLabel.getStyleClass().add("serverLabel");
			loggedInPing = new Label("-");
			loggedInPing.getStyleClass().add("serverLabel");
			
			Label loggedInCurrentDateLabel = new Label("Current Date: ");
			loggedInCurrentDateLabel.getStyleClass().add("serverLabel");
			loggedInCurrentDate = new Label("-");
			loggedInCurrentDate.getStyleClass().add("serverLabel");
		loggedGrid.addRow(0, loggedInLabel);
		loggedGrid.addColumn(1, loggedInName);
		loggedGrid.addRow(1, loggedInCurrentDateLabel);
		loggedGrid.addColumn(1, loggedInCurrentDate);
		loggedGrid.addRow(2, loggedInPingLabel);
		loggedGrid.addColumn(1, loggedInPing);
		
		
		options.addColumn(0, buttonGrid);
		options.addColumn(1, extraButtonGrid);
		options.addColumn(2, loggedGrid);
		

		bugs = new TableView<BugItem>();
		bugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);	
		bugs.setPrefHeight(400);
		bugs.setPrefWidth(410);
		bugs.getStylesheets().add("css/buglist.css");
		setupTable();
		
		
		// Middle area
		GridPane screenshotinfo = new GridPane();
		screenshotinfo.setPadding(new Insets(10,0,0,10));
		screenshotinfo.getStylesheets().add("css/custom.css");
		screenshotinfo.setHgap(20);
		//screenshotinfo.setPrefHeight(150);
		
		priorityIndicator = new Rectangle();
		priorityIndicator.setFill(Color.BLACK);
		priorityIndicator.setStroke(Color.BLACK);
		priorityIndicator.setWidth(10);
		priorityIndicator.setHeight(10);
		//screenshotinfo.setPadding(new Insets(1));

		screenshotinfo.addColumn(0, priorityIndicator);
		screenshotinfo.addRow(1, new Label());
		screenshotinfo.addRow(2, new Label());
		screenshotinfo.addRow(3, new Label());
		
		Label reportIDLabel = new Label("REPORT ID:");
		reportIDLabel.getStyleClass().add("reportidheader");
		screenshotinfo.addColumn(1, reportIDLabel);
		
		reportID = new Label("-");
		reportID.getStyleClass().add("reportid");
		screenshotinfo.addColumn(2, reportID);
		
		Label tagLabel = new Label("TAGS:");
		tagLabel.setPrefHeight(15);
		tagLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(1, tagLabel);
		
		tags = new Label("-");
		tags.setPrefHeight(15);
		tags.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, tags);
		
		Label priorityLabel = new Label("PRIORITY:");
		priorityLabel.setPrefHeight(30);
		priorityLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(2, priorityLabel);
		
		priority = new Label("-");
		priority.setPrefHeight(15);
		priority.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, priority);
		
		Label versionLabel = new Label("VERSION:");
		versionLabel.setPrefHeight(15);
		versionLabel.getStyleClass().add("tagstyle");
		screenshotinfo.addRow(3, versionLabel);
		
		version = new Label("-");
		version.setPrefHeight(15);
		version.getStyleClass().add("tagstyle");
		screenshotinfo.addColumn(2, version);
		
		
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
			message.setWrapText(true);
			messagePane.addRow(1, message);
			messagePane.setPrefHeight(200);
			
			GridPane extraInfoPane = new GridPane();
				extraInfoPane.setPadding(new Insets(0,0,0,10));
				extraInfoPane.setStyle("-fx-background-color: #ECECEC;");
				Label loggedByLabel = new Label("LOGGED BY:");
				loggedByLabel.setPrefSize(120,25);
				loggedByLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				whoLogged = new Label("-");
				whoLogged.setPrefSize(100,25);
				whoLogged.getStyleClass().add("extraMessageInfo");
				
				Label dateLabel = new Label("DATE:");
				dateLabel.setPrefSize(70,25);
				dateLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				loggedDate = new Label("-");
				loggedDate.setPrefSize(200,25);
				loggedDate.getStyleClass().add("extraMessageInfo");
				
				Label updatedByLabel = new Label("UPDATED BY:");
				updatedByLabel.setPrefSize(150,25);
				updatedByLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				whoUpdated = new Label("-");
				whoUpdated.setPrefSize(100,25);
				whoUpdated.getStyleClass().add("extraMessageInfo");
				
				Label updatedDateLabel = new Label("DATE:");
				updatedDateLabel.setPrefSize(100,25);
				updatedDateLabel.getStyleClass().add("extraMessageInfoHeaders");
				
				updatedDate = new Label("-");
				updatedDate.setPrefSize(200,50);
				updatedDate.getStyleClass().add("extraMessageInfo");
			extraInfoPane.addColumn(0, loggedByLabel);
			extraInfoPane.addRow(1, updatedByLabel);
			extraInfoPane.addColumn(1, whoLogged);
			extraInfoPane.addRow(1, whoUpdated);
			extraInfoPane.addColumn(2, dateLabel);
			extraInfoPane.addRow(1, updatedDateLabel);
			extraInfoPane.addColumn(3, loggedDate);
			extraInfoPane.addRow(1, updatedDate);
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
	                        
	                        if( item.getStatus().equalsIgnoreCase("FIXED") ){
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
		
		TableColumn<BugItem, String> statusColumn = new TableColumn<BugItem, String>("STATUS");
		statusColumn.setMinWidth(50);
		statusColumn.setMaxWidth(50);
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
		if( !bugs.getColumns().isEmpty() ){
			statusColumn.setSortType(bugs.getColumns().get(2).getSortType());
		}
		
		TableColumn<BugItem, String> updateColumn = new TableColumn<BugItem, String>("UPDATED");
		updateColumn.setPrefWidth(110);
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
	                        
	                        String[] date = item.split("\\s|[-/:]");
	                        String year = date[0];
	                        String month = date[1];
	                        String day = date[2];
	                        String hour = date[3];
	                        String minute = date[4];
	                        String second = date[5];
	                        if( second.contains(".") ){
	                        	second = second.substring(0, second.indexOf("."));
	                        }
	                        
//	                        System.out.println("SPLIT DATE: " + item);
//	                        for(String s : date){
//	                        	System.out.println("\t"+s);
//	                        }
	                        
	                        String displayDate = month + "-" + day + " " + hour + ":" + minute + ":" + second;
	                        
	                        setText(displayDate);
	                        
	                    }
	                }
	            };
	        }
	    });
		if( !bugs.getColumns().isEmpty() ){
			updateColumn.setSortType(bugs.getColumns().get(3).getSortType());
		}
		
		// TODO Need to copy over SortType FROM the columns 
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
            openScreenshots.setText("Open Screenshots (" + bug.imageMap.size() + ")");
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
            
            
            if( bug.getStatus().equalsIgnoreCase("FIXED") ){
            	priorityIndicator.setFill(Color.GREEN);
            }
            else{
            	priorityIndicator.setFill(bug.priority.getColor());
            }
            
            whoLogged.setText(bug.who == null ? "-" : bug.who);
            loggedDate.setText(bug.date == null ? "-" : bug.date);
            whoUpdated.setText(bug.whoUpdated == null ? "-" : bug.whoUpdated);
            updatedDate.setText(bug.lastUpdate == null ? "-" : bug.lastUpdate);
            version.setText(bug.version == null ? "-" : bug.version);

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
		
		//Menu edit = new Menu("Edit");
		//Menu view = new Menu("View");
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
			PopupMessage.show("Sign Out","Logged Out Successfully!\nGood Bye " + SQLData.getUsername() + "!");
		}

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
