package vektra.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import vektra.Vektra;
import vektra.extrawindows.CreateReport;
import vektra.extrawindows.EditReport;

public class ReportOptionsGUI {

	public static GridPane create(Stage primaryStage, Vektra vektra) {
		GridPane options = new GridPane();

		options.setStyle("-fx-border-width: 1, 1; -fx-border-color: #FFFFFF");
		options.setPrefHeight(105);
//		options.setMaxHeight(130);
//		options.setBackground(new Background(new BackgroundFill(Color.YELLOW, new  CornerRadii(0), new Insets(0))));
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.setPadding(new Insets(5,5,5,5));
		buttonGrid.setVgap(5);
		
		Button create = new Button("CREATE");
		vektra.setCreateReport(create);
		create.setTextAlignment(TextAlignment.CENTER);
		create.setOnAction((a)->{CreateReport.display(-1, vektra);});
		create.getStyleClass().add("button_create");
		create.setPrefWidth(85);
		create.setPrefHeight(65);
		create.setDisable(true);
		
		Button edit = new Button("EDIT");
		vektra.setEditReport(edit);
		edit.setTextAlignment(TextAlignment.CENTER);
		edit.setOnAction((a)->{EditReport.display(vektra.getSelectedBug(), vektra);});
		edit.getStyleClass().add("button_edit");
		edit.setPrefWidth(85);
		edit.setPrefHeight(50);
		edit.setDisable(true);
		
		Button delete = new Button("DELETE");
		delete.setTextAlignment(TextAlignment.CENTER);
		delete.setOnAction((a)->vektra.deleteCurrentBug());
		delete.getStyleClass().add("button_delete");
		delete.setPrefWidth(85);
		delete.setPrefHeight(50);
		delete.setDisable(true);
		vektra.setDeleteReport(delete);
		
		buttonGrid.addRow(0, create);
		buttonGrid.addRow(1, edit);
		buttonGrid.addRow(2, delete);
		
		
		GridPane extraButtonGrid = new GridPane();
		extraButtonGrid.setPadding(new Insets(5,5,5,0));
		extraButtonGrid.setVgap(5);
		
		Button loginButton = new Button("LOGIN");
		loginButton.setOnAction((a)->{if( loginButton.getText().equals("LOGIN")) vektra.login(); else vektra.signOut();});
		loginButton.getStyleClass().add("button_extra");
		loginButton.setPrefWidth(150);
		loginButton.setPrefHeight(50);
		vektra.setReportLogin(loginButton);
		
		Button refresh = new Button("REFRESH");
		refresh.setOnAction((a)->{vektra.performFullRefresh();});
		refresh.getStyleClass().add("button_extra");
		refresh.setPrefWidth(150);
		refresh.setPrefHeight(50);
		refresh.setDisable(true);
		vektra.setRefresh(refresh);
		
		extraButtonGrid.addRow(0, loginButton);
		extraButtonGrid.addRow(1, refresh);
		
		
		GridPane loggedGrid = new GridPane();
		loggedGrid.alignmentProperty().set(Pos.TOP_RIGHT);
			Label loggedInLabel = new Label("User: ");
			loggedInLabel.getStyleClass().add("loggedLabel");
			Label loggedInName = new Label("-");
			loggedInName.getStyleClass().add("loggedLabel");
			vektra.setLoggedInName(loggedInName);
			
			Label loggedInPingLabel = new Label("Ping: ");
			loggedInPingLabel.getStyleClass().add("serverLabel");
			Label loggedInPing = new Label("-");
			loggedInPing.getStyleClass().add("serverLabel");
			vektra.setLoggedInPing(loggedInPing);
			
			Label loggedInCurrentDateLabel = new Label("Current Date: ");
			loggedInCurrentDateLabel.getStyleClass().add("serverLabel");
			Label loggedInCurrentDate = new Label("-");
			loggedInCurrentDate.getStyleClass().add("serverLabel");
			vektra.setLoggedInCurrentDate(loggedInCurrentDate);
		loggedGrid.addRow(0, loggedInLabel);
		loggedGrid.addColumn(1, loggedInName);
		loggedGrid.addRow(1, loggedInCurrentDateLabel);
		loggedGrid.addColumn(1, loggedInCurrentDate);
		loggedGrid.addRow(2, loggedInPingLabel);
		loggedGrid.addColumn(1, loggedInPing);
		
		
		options.addColumn(0, buttonGrid);
		options.addColumn(1, extraButtonGrid);
		options.addColumn(2, loggedGrid);
		return options;
	}

}
