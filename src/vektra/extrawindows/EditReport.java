package vektra.extrawindows;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vektra.BugItem;
import vektra.SQLData;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;

public class EditReport extends ModifyReport{
	
	private static Button editReport;
	private static BugItem bugToEdit;
	
	public static void display(BugItem bug) {
		if( bug == null ){
			PopupError.show("Can not edit Bug", "Null bug supplied. Can not modify!");
			return;
		}
		
		bugToEdit = bug;
		
		ModifyReport.display(bug.ID);
		
		text.setText(bug.message);

		LOW.setSelected(bugToEdit.priority.equals("LOW"));
		MEDIUM.setSelected(bugToEdit.priority.equals("MEDIUM"));
		HIGH.setSelected(bugToEdit.priority.equals("HIGH"));

		
		GAMEPLAY.setSelected(bugToEdit.getTagMessages().contains("GAMEPLAY"));
		VISUAL.setSelected(bugToEdit.getTagMessages().contains("VISUAL"));
		AUDIO.setSelected(bugToEdit.getTagMessages().contains("AUDIO"));
		BREAKING.setSelected(bugToEdit.getTagMessages().contains("BREAKING"));

		addImages(bug.imageMap);
		
		editReport = new Button("Update Bug");
		editReport.setOnAction(new UpdateBugButtonPress());
		setConfirmButton(editReport);
	}

	private static void ProcessEditBug(){
		if( !ModifyReport.checkForErrors() ){
			return;
		}
		
		editReport.setDisable(true);
		
		
		// UPDATE
		BugItem bug = getBug();
		boolean updated = SQLData.update(bugToEdit, bug);
		if( !updated ){
			PopupError.show("Failed to update Bug!", "Could not Update bug on server!.");
			editReport.setDisable(false);
		}
		else{
			primaryStage.close();
			PopupMessage.show("Success!", "Modified Bug correctly!");
		}
		
	}
	
	private static class UpdateBugButtonPress implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			ProcessEditBug();			
		}

	}
	
}
