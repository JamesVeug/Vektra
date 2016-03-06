package vektra.extrawindows;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vektra.Priority;
import vektra.Status;
import vektra.Vektra;
import vektra.resources.FilterConfiguration;

public class FilterWindow {

	private static TextField whoField;
	private static List<CheckBox> statuses;
	private static List<CheckBox> priorities;
	private static Stage stage;

	public static void show(Vektra vektra) {
		
		stage = new Stage();
		stage.setTitle("Filter Options");
		stage.getIcons().add(new Image("v.jpg"));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		
		// Bug Info
		HBox bugLayout = new HBox();
		bugLayout.setAlignment(Pos.CENTER_LEFT);
		bugLayout.getStylesheets().add("css/custom.css");
		bugLayout.getStyleClass().add("aboutinfo");
		
		whoField = new TextField();
		whoField.setPromptText("Vektra");

		bugLayout.getChildren().addAll(new Label("BUGS: "), new Label("Poster"), whoField);
		
		
		// Status
		HBox statusLayout = new HBox();
		statusLayout.setAlignment(Pos.CENTER_LEFT);
		statusLayout.setSpacing(10);
		statusLayout.getStylesheets().add("css/custom.css");
		statusLayout.getStyleClass().add("aboutinfo");
		statusLayout.getChildren().add(new Label("Status: "));
		
		statuses = createStatusCheckBoxes();//new ArrayList<CheckBox>();
		statusLayout.getChildren().addAll(statuses);
		
		// Status
		HBox priortyLayout = new HBox();
		priortyLayout.setAlignment(Pos.CENTER_LEFT);
		priortyLayout.setSpacing(10);
		priortyLayout.getStylesheets().add("css/custom.css");
		priortyLayout.getStyleClass().add("aboutinfo");
		priortyLayout.getChildren().add(new Label("Priority: "));
		
		priorities = createPriorityCheckBoxes();
		priortyLayout.getChildren().addAll(priorities);
		
		// Submit Buttom
		Button submit = new Button("SUBMIT");
		submit.setOnAction((a)->submitFilterSettings(vektra));
		
		Button clear = new Button("CLEAR");
		clear.setOnAction((a)->clearFilterSettings(vektra));
		
		VBox layout = new VBox();
		layout.setSpacing(10);
		layout.getChildren().addAll(bugLayout,statusLayout,priortyLayout,clear,submit);
		layout.setAlignment(Pos.TOP_LEFT);
		layout.setPadding(new Insets(10));
		
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

	private static void clearFilterSettings(Vektra vektra) {
		for(CheckBox s : statuses){
			s.setSelected(false);
		}
		for(CheckBox s : priorities){
			s.setSelected(false);
		}
		whoField.setText("");
	}

	private static List<CheckBox> createStatusCheckBoxes() {
		List<CheckBox> statusCheckBoxes = new ArrayList<CheckBox>();
		
		Map<Status, Boolean> map = FilterConfiguration.getStatusSettings();
		for(Status s : map.keySet()){
			CheckBox box = new CheckBox();
			box.setText(s.label);
			box.setSelected(map.get(s));
			statusCheckBoxes.add(box);
		}
		return statusCheckBoxes;
	}
	
	private static List<CheckBox> createPriorityCheckBoxes() {
		List<CheckBox> priorityCheckBoxes = new ArrayList<CheckBox>();
		
		Map<Priority, Boolean> map = FilterConfiguration.getPrioritySettings();
		for(Priority s : map.keySet()){
			CheckBox box = new CheckBox();
			box.setText(s.label);
			box.setSelected(map.get(s));
			priorityCheckBoxes.add(box);
		}
		return priorityCheckBoxes;
	}

	private static void submitFilterSettings(Vektra vektra) {
		
		Map<Status,Boolean> statusMap = new HashMap<Status,Boolean>();
		for(CheckBox s : statuses){
			statusMap.put(Status.get(s.getText()), s.isSelected());
		}
		
		Map<Priority,Boolean> priorityMap = new HashMap<Priority,Boolean>();
		for(CheckBox s : priorities){
			priorityMap.put(Priority.get(s.getText()), s.isSelected());
		}
		
		
		FilterConfiguration.setStatusSettings(statusMap);
		FilterConfiguration.setPrioritySettings(priorityMap);
		FilterConfiguration.saveFilterOptions();
		
		stage.close();
		vektra.refreshBugs();
	}

}
