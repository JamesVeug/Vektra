package vektra.extrawindows;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vektra.BugItem;
import vektra.SQLData;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupMessage;

public class EditReport extends ModifyReport{
	
	
	private static BugItem bugToEdit;
	
	public static void display(BugItem bug) {
		if( bug == null ){
			PopupError.show("Can not edit Bug", "Null bug supplied. Can not modify!");
			return;
		}
		
		bugToEdit = bug;
		
		display(bug.ID);
		
		text.setText(bug.message);

		LOW.setSelected(bugToEdit.priority.equals("LOW"));
		MEDIUM.setSelected(bugToEdit.priority.equals("MEDIUM"));
		HIGH.setSelected(bugToEdit.priority.equals("HIGH"));

		
		GAMEPLAY.setSelected(bugToEdit.tags.contains("GAMEPLAY"));
		VISUAL.setSelected(bugToEdit.tags.contains("VISUAL"));
		AUDIO.setSelected(bugToEdit.tags.contains("AUDIO"));
		BREAKING.setSelected(bugToEdit.tags.contains("BREAKING"));

		addImages(bug.imageMap);
		
		Button createReport = new Button("Update Bug");
		createReport.setOnAction(new UpdateBugButtonPress());
		setConfirmButton(createReport);
	}

	private static void ProcessEditBug(){
		
		
		// UPDATE
		BugItem bug = getBug();
		boolean updated = SQLData.update(bug);
		if( !updated ){
			PopupError.show("Failed to update Bug!", "Could not Update bug on server!.");
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
