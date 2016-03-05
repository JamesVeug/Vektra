package vektra.GUI;

import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import vektra.BugItem;
import vektra.Priority;
import vektra.Status;
import vektra.Vektra;
import vektra.extrawindows.FilterWindow;
import vektra.resources.FilterConfiguration;

public class BugListGUI {
	final static Background SetFixedStatusBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(0), new Insets(5)));
	final static Background UpdatedColor = new Background(new BackgroundFill(Color.valueOf("rgb(57, 71, 61)"), new CornerRadii(0), new Insets(5)));
	
	private static boolean autoFilterBugs = true;

	/**
	 * Creates a new TableView to contain our list of bugs
	 * @param primaryStage
	 * @param vektra
	 * @return
	 */
	public static Pane create(Stage primaryStage, Vektra vektra) {
		
		BorderPane pane = new BorderPane();
		
			TableView<BugItem> bugs = new TableView<BugItem>();
			bugs.setRowFactory(new Callback<TableView<BugItem>, TableRow<BugItem>>() {
		        @Override
		        public TableRow<BugItem> call(TableView<BugItem> tableView) {
		            final TableRow<BugItem> row = new TableRow<BugItem>() {
		                @Override
		                protected void updateItem(BugItem person, boolean empty){
		                    super.updateItem(person, empty);
		                    if( !empty ){
			                    if (person.hasBeenUpdated) {
			                        if (! getStyleClass().contains("updatedRow")) {
			                            getStyleClass().add("updatedRow");
			                        }
			                    } else {
			                        getStyleClass().removeAll(Collections.singleton("updatedRow"));
			                    }
		                    }
		                }
		            };
					return row;
		        }
		    });
			
			// Listen for the selected item to change
			bugs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			    if (newSelection != null) {
			    	BugItem item = bugs.getSelectionModel().getSelectedItem();
			    	if(item.hasBeenUpdated ){
			    		item.hasBeenUpdated = false;
			    	}
			    	
			    	// Change the GUI
			    	vektra.selectBug(item);
			    }
			});
			
			bugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			bugs.setMaxWidth(300);
			bugs.getStylesheets().add("css/buglist.css");
			vektra.setBugs(bugs);
			
			setupColumns(null, bugs, vektra);
		
		pane.setCenter(bugs);
		
		//
		// FILTER OPTIONS
		//
		
		GridPane filterOptionsPane = new GridPane();
		filterOptionsPane.setBackground(UpdatedColor);
		filterOptionsPane.setPadding(new Insets(5));
		
			Label filterLabel = new Label("Filter");
			filterLabel.getStylesheets().add("css/buglist.css");
			filterLabel.getStyleClass().add("filter");
		filterOptionsPane.addRow(0, filterLabel);
		
			HBox filterOptions = new HBox();
			filterOptions.setAlignment(Pos.CENTER);
			filterOptions.setSpacing(5);
			filterOptions.setPadding(new Insets(5));

			ImageView autoFilterIcon = new ImageView();
				autoFilterIcon.setImage(new Image("auto.png",30,30,false,false));
			
			CheckBox autoFilter = new CheckBox();
				autoFilter.setTooltip(new Tooltip("Auto filter Bug list"));
				autoFilter.setSelected(autoFilterBugs);
				autoFilter.selectedProperty().addListener((a,o,n)->{autoFilterBugs = n;});
				autoFilter.setGraphic(autoFilterIcon);
				
			Button filterButton = new Button("FILTER");
				filterButton.setOnAction((a)->setupColumns(FilterConfiguration.filter(bugs.getItems()), bugs, vektra));
				filterButton.setTooltip(new Tooltip("Filter the currently listed Bugs"));
		
			Button editFilterButton = new Button();
				editFilterButton.setGraphic(new ImageView(new Image("edit.png",15,15,false,false)));
				editFilterButton.setOnAction((a)->FilterWindow.show(vektra));
				editFilterButton.setTooltip(new Tooltip("Edit the Filter Settings"));
	
			filterOptions.getChildren().addAll(autoFilterIcon, autoFilter,filterButton,editFilterButton);
		filterOptionsPane.addRow(1, filterOptions);
		
		pane.setBottom(filterOptionsPane);
		GridPane.setValignment(filterOptions, VPos.BOTTOM);
		return pane;
	}

	/**
	 * Resets up the columns when the information is updated
	 * @param importedData
	 * @param bugs
	 * @param vektra
	 */
	public static void setupColumns(ObservableList<BugItem> importedData, TableView<BugItem> bugs, Vektra vektra) {

		// Create a new list if we don't have one
		ObservableList<BugItem> filtered = importedData == null ? FXCollections.observableArrayList() : importedData;
		filtered = autoFilterBugs ? FilterConfiguration.filter(filtered) : filtered;
		
    	bugs.setItems(filtered);
		
		// Bottom Left ( BUG LIST )
//		if( bugs.getColumns().isEmpty() ){
			createColumns(bugs, vektra);
			
//		}
		bugs.sort();
	}
	
	private static void createColumns(TableView<BugItem> bugs, Vektra vektra){
		TableColumn<BugItem, Priority> priorityColumn = new TableColumn<BugItem, Priority>("P");
		priorityColumn.setMinWidth(20);
		priorityColumn.setMaxWidth(20);
		priorityColumn.setComparator(new PriorityComparator());
		priorityColumn.setCellValueFactory(new PropertyValueFactory<BugItem, Priority>("priority"));
		priorityColumn.setCellFactory(new Callback<TableColumn<BugItem, Priority>, TableCell<BugItem, Priority>>() {
	        public TableCell<BugItem, Priority> call(TableColumn<BugItem, Priority> param) {
	            return new TableCell<BugItem, Priority>() {
	            	

	                @Override
	                public void updateItem(Priority priority, boolean empty) {
	                    super.updateItem(priority, empty);
	                    
	                    if (!isEmpty()) {

		                    int index = getIndex();
		            		BugItem bug = bugs.getItems().get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
//	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->{ refreshRow(bugs, index, bug); vektra.selectBug(bug); });
	                        
	                        if( bug.getStatus() == Status.FIXED ){
		                        setBackground(SetFixedStatusBackground);
	                        	
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
	                    	int index = getIndex();
		            		BugItem bug = bugs.getItems().get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
//	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->{ refreshRow(bugs, index, bug); vektra.selectBug(bug); });
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
		statusColumn.setComparator(new StatusComparator());
		statusColumn.setCellValueFactory(new PropertyValueFactory<BugItem, Status>("status"));
		statusColumn.setCellFactory(new Callback<TableColumn<BugItem, Status>, TableCell<BugItem, Status>>() {
	        public TableCell<BugItem, Status> call(TableColumn<BugItem, Status> param) {
	            return new TableCell<BugItem, Status>() {

	                @Override
	                public void updateItem(Status item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                    	int index = getIndex();
		            		BugItem bug = bugs.getItems().get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
//	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->{ refreshRow(bugs, index, bug); vektra.selectBug(bug); });
	                        
	                        
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
	                    	int index = getIndex();
		            		BugItem bug = bugs.getItems().get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
//	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->{ refreshRow(bugs, index, bug); vektra.selectBug(bug); });
	                        
	                        int[] date = splitDate(item);
	                        
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
	private static int[] splitDate(String item) {
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
	
	
	private static class PriorityComparator implements Comparator<Priority>{

		@Override
		public int compare(Priority one, Priority two) {
			return getValue(one)-getValue(two);
		}
		
		private int getValue(Priority p){
			if( p == Priority.LOW ) return 0;
			if( p == Priority.MEDIUM ) return 1;
			if( p == Priority.HIGH ) return 2;
			return -1;
		}
		
	}
	
	private static class StatusComparator implements Comparator<Status>{

		@Override
		public int compare(Status one, Status two) {
			return getValue(one)-getValue(two);
		}
		
		private int getValue(Status s){
			if( s == Status.FIXED ) return 0;
			if( s == Status.PENDING ) return 1;
			if( s == Status.WIP ) return 2;
			return -1;
		}
		
	}
}












