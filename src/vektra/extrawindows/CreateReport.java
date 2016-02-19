package vektra.extrawindows;


import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vektra.BugItem;
import vektra.SQLData;
import vektra.Stage;
import vektra.Status;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;
import vektra.dialogs.PopupWarningConfirmation;

public class CreateReport extends ModifyReport{
	
	private static Button createReport;
	
	public static void display(int maxID) {
		ModifyReport.display("Create Report", maxID+1);

		
		

		LOW.setSelected(true);
		GAMEPLAY.setSelected(true);
		statusSelection.setValue(Status.PENDING);
		stageVersion.setValue(Stage.ALPHA);

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
		List<Integer> insertedErrors = SQLData.insert(bug);
		
		boolean addedBug = false;
		List<String> errorMessages = new ArrayList<String>();
		for(Integer inserted : insertedErrors){
		
			if( inserted > 0 ){
				addedBug = true;
				errorMessages.add("Success!\nCreated a new Bug Report!\nBugID: " + inserted);
				return;
			}
			if( inserted == -1 ){
				errorMessages.add("Not connected to database");
			}
			else if( inserted == -2 ){
				errorMessages.add("Could not insert new Bug.");
			}
			else if( inserted == -3 ){
				errorMessages.add("Could not insert Priority.");
			}
			else if( inserted == -4 ){
				errorMessages.add("Could not insert Status.");
			}
			else if( inserted == -5 ){
				errorMessages.add("Could not insert Screenshots.");
			}
			else if( inserted == -6 ){
				errorMessages.add("Could not insert Tags.");
			}
		}
		
		// If we added the bug. Close the dialog
		if( addedBug ){
			primaryStage.close();
		}
		else{

			// Only if failed!
			createReport.setDisable(false);			
		}
		
		//
		// Display message
		//
		
		// If we did not insert the bug. Display error
		if( insertedErrors.contains(new Integer(-2)) ){
			PopupError.show("Failed to insert new Report!", errorMessages);
		}
		else if( insertedErrors.size() == 1 ){
			
			// No errors
			PopupMessage.show("Inserted Bug Successfully", "BugID: " + insertedErrors.get(0));
			
		}
		else{
			
			// Some errors
			PopupWarningConfirmation.show("Warning adding Bug", "Successfully added bug but with some warnings", errorMessages);
		}
	}

	
	
	private static class CreateReportButtonPress implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			ProcessCreatingReport();			
		}

	}
	
}
