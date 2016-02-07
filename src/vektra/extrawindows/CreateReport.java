package vektra.extrawindows;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vektra.BugItem;
import vektra.SQLData;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;

public class CreateReport extends ModifyReport{
	
	public static void display(int maxID) {
		display(maxID+1);
		//MAXID = maxID;
		

		LOW.setSelected(true);
		GAMEPLAY.setSelected(true);

		Button createReport = new Button("Create Bug");
		createReport.setOnAction(new CreateReportButtonPress());
		setConfirmButton(createReport);
	}

	private static void ProcessCreatingReport(){		
		
		// Create
		BugItem bug = getBug();
		boolean inserted = SQLData.insert(bug);
		if( !inserted ){
			PopupError.show("Failed to insert new Report!", "Could not insert new Report.");
		}
		else{
			primaryStage.close();
			PopupMessage.show("Success!", "Created new Report!");
		}
		
	}

	
	
	private static class CreateReportButtonPress implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			ProcessCreatingReport();			
		}

	}
	
}
