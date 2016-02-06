package vektra;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoadingDialog {	
	private final ProgressBar pb;
	private final ProgressIndicator pi;
	private final Label stageLabel;
	
	private Stage stage;
	private String[] messages;
	
	private final int maxStages;
	private int currentStage;
	
	private LoadingDialog(int maxStages, String[] messages){		
		this.maxStages = maxStages;
		currentStage = 0;
		this.messages = messages;
		
		Group root = new Group();
	    Scene scene = new Scene(root);
	    stage = new Stage();
	    stage.setScene(scene);
	    stage.setTitle("Progress Controls");
	
	    pb = new ProgressBar(0);
	    pi = new ProgressIndicator(0);
	    stageLabel = new Label(messages[0]);

	
	    final HBox hb = new HBox();
	    hb.setSpacing(5);
	    hb.setAlignment(Pos.CENTER);
	    hb.getChildren().addAll(pb, pi,stageLabel);
	    
	    scene.setRoot(hb);
	    stage.show();
	    isVisible = true;
	}
	
	private void stopDialog(){
		stage.hide();
	}
	
	private void addStage(){
		currentStage++;
		pb.setProgress(((double)currentStage)/maxStages);
		pi.setProgress(((double)currentStage)/maxStages);
		stageLabel.setText(messages[currentStage]);
	}
	
	private int getCurrentStage(){
		return currentStage;
	}
	
	private int getMaxStages(){
		return maxStages;
	}
	
	
	//
	//
	// Static references
	//
	//
	
	
	private static boolean isVisible = false;
	private static LoadingDialog log;
	
	public static void start(final int stages, final String... messages){
		if( isVisible ){
			PopupError.show("Can not show loading dialog.", "Already visible");
		}
		else if( stages <= 0 ){
			PopupError.show("Can not show loading dialog.", "Stages must be greater than 0 '" + stages + "'");
		}
		else if( stages != messages.length ){
			PopupError.show("Can not show loading dialog.", "Max Stages must match the amount of messages given. Stages: " + stages + ". Messages: " + messages.length);
		}
		else if( log != null ){
			PopupError.show("Can not show loading dialog.", "Dialog already assigned ot variable");
		}

	    isVisible = false;

    	log = new LoadingDialog(stages, messages);
	}
	
	public static boolean isVisible(){
		return isVisible;
	}
	
	public static void stop(){
		if( !isVisible ){
			PopupError.show("Can not hide loading dialog.", "Dialog is not already visible");
		}
		
		log.stopDialog();
		log = null;
		isVisible = false;
	}
	
	/**
	 * Step to the next bar of the progress bar
	 */
	public static void next(){
		if( log.getCurrentStage()+1 > log.getMaxStages() ){
			PopupError.show("Error adding stage.", "Current stages exceeded Max Stages. Current: " + (log.currentStage+1) + ". Max: " + log.maxStages);
		}		
		
		log.addStage();
	}
}















