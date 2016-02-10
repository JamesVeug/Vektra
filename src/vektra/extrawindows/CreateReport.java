package vektra.extrawindows;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vektra.BugItem;
import vektra.SQLData;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;

public class CreateReport extends ModifyReport{
	
	private static Button createReport;
	
	public static void display(int maxID) {
		ModifyReport.display(maxID+1);

		
		

		LOW.setSelected(true);
		GAMEPLAY.setSelected(true);

		createReport = new Button("Create Bug");
		createReport.setOnAction(new CreateReportButtonPress());
		setConfirmButton(createReport);
	}

	private static void ProcessCreatingReport(){
		if( !ModifyReport.checkForErrors() ){
			return;
		}
		
		createReport.setDisable(true);
		
		// Create
		BugItem bug = getBug();
		int inserted = SQLData.insert(bug);
		if( inserted > 0 ){
			primaryStage.close();
			PopupMessage.show("Success!", "Created a new Bug Report!\nBugID: " + inserted);
			return;
		}
		if( inserted == -1 ){
			PopupError.show("Failed to insert new Report!", "Not connected to database");
		}
		else if( inserted == -2 ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Bug.");
		}
		else if( inserted == -3 ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Priority.");
		}
		else if( inserted == -4 ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Status.");
		}
		else if( inserted == -5 ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Screenshot.");
		}
		else if( inserted == -6 ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Tag.");
		}

		// Only if failed!
		createReport.setDisable(false);
		
	}

	
	
	private static class CreateReportButtonPress implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			ProcessCreatingReport();			
		}

	}
	
}
