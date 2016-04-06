package vektra.extrawindows;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vektra.Vektra;
import vektra.resources.Configurations;

public class ConfigurationWindow {

	private static TextField imageDirectory;
	private static NumberField autoRefreshInterval;
	private static ComboBox<String> autoRefreshIntervalIncrement;
	private static CheckBox autoRefresh; // Should we automatically refresh AT ALL?
	private static CheckBox autoFullRefresh;
	private static Label autoRefreshLabel;
	private static HashMap<String, Integer> increments = new HashMap<String,Integer>(){
		private static final long serialVersionUID = 3274017685828665089L;
	{
		put("Minutes",60); // Time to seconds
		put("Hours",3600);
	}};
	
	private static Stage stage;

	public static void show(Vektra vektra) {
		
		stage = new Stage();
		stage.setTitle("Preferences");
		stage.getIcons().add(new Image("v.jpg"));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		
		// Bug Info
		VBox bugLayout = new VBox();
		bugLayout.setAlignment(Pos.CENTER_LEFT);
		bugLayout.getStylesheets().add("css/custom.css");
		bugLayout.getStyleClass().add("aboutinfo");
		
		imageDirectory = new TextField();
		imageDirectory.setPromptText("C:/Users/username/AppData/Roaming/VektraBugReporter");
		imageDirectory.setText(getImageDirectory());
		imageDirectory.setPrefWidth(500);

		bugLayout.getChildren().addAll(new Label("Image Directory:"), imageDirectory);
		
		// Auto Refresh
		HBox intervalLayout = new HBox();
		autoRefreshInterval = new NumberField(3);
		autoRefreshIntervalIncrement = new ComboBox<String>();
		autoRefreshIntervalIncrement.getItems().addAll(increments.keySet());
		autoRefreshIntervalIncrement.setValue("Minutes");
		
		autoRefreshLabel = new Label("Refresh Delay");
		autoRefreshLabel.setPadding(new Insets(0,10,0,0));
		intervalLayout.getChildren().addAll(autoRefreshLabel, autoRefreshInterval,autoRefreshIntervalIncrement);
		
		autoRefresh = new CheckBox("Automatically Refresh"); // Should we automatically refresh AT ALL?
		autoRefresh.setTooltip(new Tooltip("Should Vektra automatically check for updates?"));
		autoRefresh.setSelected(true);
		autoRefresh.setOnAction((a)->{toggleAutoRefreshOptions();});
		
		autoFullRefresh = new CheckBox("Full Refresh ( SLOW )");
		autoFullRefresh.setTooltip(new Tooltip("When automatically checking for an update. Should Vektra request all data? ( SLOW )"));
		autoFullRefresh.setSelected(false);
		
		VBox autoRefreshLayout = new VBox();
			VBox innerautoRefreshLayout = new VBox();
			innerautoRefreshLayout.setPadding(new Insets(0,0,0,20));
			innerautoRefreshLayout.getChildren().addAll(autoFullRefresh,intervalLayout);
		autoRefreshLayout.getChildren().addAll(autoRefresh,innerautoRefreshLayout);
		
		// Submit Buttom
		HBox buttonPane = new HBox();
		buttonPane.setSpacing(20);
			Button submit = new Button("Submit");
			submit.setOnAction((a)->submitSettings(vektra));
			
			Button cancel = new Button("Cancel");
			cancel.setOnAction((a)->{stage.close();});
		buttonPane.getChildren().addAll(submit, cancel);
		
		VBox layout = new VBox();
		layout.setSpacing(10);
		layout.getChildren().addAll(bugLayout,autoRefreshLayout,buttonPane);
		layout.setAlignment(Pos.TOP_LEFT);
		layout.setPadding(new Insets(10));
		
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}

	private static void toggleAutoRefreshOptions() {
		boolean ticked = autoRefresh.isSelected();
		autoFullRefresh.setDisable(!ticked);
		autoRefreshInterval.setDisable(!ticked);
		autoRefreshIntervalIncrement.setDisable(!ticked);
		autoRefreshLabel.setDisable(!ticked);
	}
	
	private static String getImageDirectory() {
		String d = Configurations.getImageDirectory();
		return d;
	}

	private static void submitSettings(Vektra vektra) {

		Configurations.setImageDirectory(imageDirectory.getText());
		Configurations.setAutoRefresh(autoRefresh.isSelected());
		

		int interval = autoRefreshInterval.getNumber() * increments.get(autoRefreshIntervalIncrement.getValue());
		Configurations.setAutoRefreshInterval(interval);
		Configurations.setFullAutoRefresh(autoFullRefresh.isSelected());
		Configurations.saveFilterOptions();
		
		stage.close();
		vektra.refreshBugs();
	}
	
	public static class NumberField extends TextField
	{
		public NumberField(int value){
			super(String.valueOf(value));
		}

	    @Override
	    public void replaceText(int start, int end, String text)
	    {
	        if (validate(text))
	        {
	            super.replaceText(start, end, text);
	        }
	    }

	    @Override
	    public void replaceSelection(String text)
	    {
	        if (validate(text))
	        {
	            super.replaceSelection(text);
	        }
	    }

	    private boolean validate(String text)
	    {
	        return text.matches("[0-9]*");
	    }
	    
	    public int getNumber(){
	    	return Integer.parseInt(getText());
	    }
	}
}
