package vektra.GUI;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import vektra.BugItem;
import vektra.Priority;
import vektra.Status;
import vektra.Vektra;

public class BugListGUI {
	final static Background GreenBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(0), new Insets(5)));

	public static TableView<BugItem> create(Stage primaryStage, Vektra vektra) {
		
		TableView<BugItem> bugs = new TableView<BugItem>();
		bugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		bugs.setMaxWidth(300);
		bugs.getStylesheets().add("css/buglist.css");
		GridPane.isFillHeight(bugs);
		vektra.setBugs(bugs);
		
		setupColumns(null, bugs, vektra);
		
		return bugs;
	}

	public static void setupColumns(ObservableList<BugItem> importedData, TableView<BugItem> bugs, Vektra vektra) {
    	
    	
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
		            		BugItem bug = importedData.get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->vektra.selectBug(bug));
	                        
	                        if( bug.getStatus() == Status.FIXED ){
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
	                    	int index = getIndex();
		            		BugItem bug = importedData.get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->vektra.selectBug(bug));
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
	                    	int index = getIndex();
		            		BugItem bug = importedData.get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->vektra.selectBug(bug));
	                        
	                        
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
		            		BugItem bug = importedData.get(index);
		            		
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->vektra.selectBug(bug));
	                        
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
}
