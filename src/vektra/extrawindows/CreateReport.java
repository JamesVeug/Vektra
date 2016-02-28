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
import vektra.Vektra;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;
import vektra.dialogs.PopupWarning;
import vektra.dialogs.PopupWarningConfirmation;

public class CreateReport extends ModifyReport{
	
	private static Button createReport;
	
	public static void display(int maxID, Vektra vektra) {
		ModifyReport.display("Create Report", maxID+1);

		
		

		LOW.setSelected(true);
		GAMEPLAY.setSelected(true);
		statusSelection.setValue(Status.PENDING);
		stageVersion.setValue(Stage.ALPHA);

		createReport = new Button("Create Bug");
		createReport.setOnAction((a)->ProcessCreatingReport(vektra));
		setConfirmButton(createReport);
	}

	private static void ProcessCreatingReport(Vektra vektra){
		if( !ModifyReport.checkForErrors() ){
			return;
		}
		
		createReport.setDisable(true);
		
		System.out.println("Inserting");
		
		// Create
		BugItem bug = getBug();
		List<Integer> insertedErrors = SQLData.insert(bug);
		System.out.println("Inserted");
		
		boolean addedBug = false;
		List<String> errorMessages = new ArrayList<String>();
		for(Integer inserted : insertedErrors){
			System.out.println("Each " + inserted);
		
			if( inserted > 0 ){
				addedBug = true;
				errorMessages.add("New BugID: " + inserted);
				break;
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
		System.out.println("Finished inserting each");
		
		// If we added the bug. Close the dialog
		if( addedBug ){
			primaryStage.close();
		}
		else{

			// Only if failed!
			createReport.setDisable(false);			
		}
		
		System.out.println("Added " + addedBug);
		
		//
		// Display message
		//
		
		// If we did not insert the bug. Display error
		if( !addedBug ){
			PopupError.show("Failed to insert new Report!", errorMessages);
		}
		else if( insertedErrors.size() == 1 ){
			
			// Refresh
			vektra.performPartialRefresh();
			
			// No errors
			PopupMessage.show("Inserted Bug Successfully!", "New BugID: " + insertedErrors.get(0));
			
		}
		else{
			
			// Refresh
			vektra.performPartialRefresh();
			
			// Some errors
			PopupWarning.show("Warning adding Bug", "Successfully added bug but with some warnings", errorMessages);
		}
	}
	
}
