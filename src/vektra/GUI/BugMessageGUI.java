package vektra.GUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import vektra.Comment;
import vektra.SQLData;
import vektra.Vektra;
import vektra.dialogs.PopupConfirmation;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupTextField;

public class BugMessageGUI {

	public static GridPane create(Stage primaryStage, Vektra vektra) {
		GridPane messagePane = new GridPane();
		messagePane.setPadding(new Insets(5,5,0,5));
		messagePane.setMaxWidth(400);
//		messagePane.setBackground(new Background(new BackgroundFill(Color.PINK, new CornerRadii(0), new Insets(0))));
		
		Label label = new Label("REPORT DESCRIPTION:");
		label.getStyleClass().add("reportDescription");
		messagePane.addRow(0, label);
		GridPane.setColumnSpan(label, 2);
		
		TextArea message = new TextArea("No Text Here");
		message.setPrefHeight(400);
		message.setEditable(false);
		message.setWrapText(true);
		messagePane.addRow(1, message);
		messagePane.setPrefHeight(200);
		vektra.setMessage(message);
		GridPane.setColumnSpan(message, 2);
		
//		VBox commentPane = new VBox();
			TableView<Comment> comments = new TableView<Comment>();
			comments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			comments.getStylesheets().add("css/buglist.css");
			
			//commentScroll = new ScrollPane(comments);
			setupComments(new ArrayList<Comment>(), comments, vektra);
			vektra.setComments(comments);
			
			messagePane.addRow(2, comments);
			GridPane.setColumnSpan(comments, 2);
			
//			GridPane commentOptions = new GridPane();
				
				TextField enterComment = new TextField();
				enterComment.setPromptText("Enter a response here");
				enterComment.setOnAction((a)->vektra.SubmitCommentButtonPressed(enterComment.getText()));
				enterComment.getStyleClass().add("createReport_Options_Text");
//				enterComment.setPrefWidth(357);
				enterComment.setPrefHeight(100);
				vektra.setEnterComment(enterComment);
				
				Button submitComment = new Button("Send");
				submitComment.setOnAction((a)->vektra.SubmitCommentButtonPressed(enterComment.getText()));
				vektra.setSubmitComment(submitComment);
				
			messagePane.addRow(3, submitComment);
			messagePane.addColumn(1, enterComment);
			GridPane.isFillWidth(enterComment);

		GridPane extraInfoPane = new GridPane();
			extraInfoPane.setPadding(new Insets(0,0,0,10));
			extraInfoPane.setStyle("-fx-background-color: #ECECEC;");
			Label loggedByLabel = new Label("LOGGED BY:");
			loggedByLabel.setPrefSize(120,25);
			loggedByLabel.getStyleClass().add("extraMessageInfoHeaders");
			
			Label whoLogged = new Label("-");
			whoLogged.setPrefSize(100,25);
			whoLogged.getStyleClass().add("extraMessageInfo");
			vektra.setWhoLogged(whoLogged);
			
			Label dateLabel = new Label("DATE:");
			dateLabel.setPrefSize(70,25);
			dateLabel.getStyleClass().add("extraMessageInfoHeaders");
			
			Label loggedDate = new Label("-");
			loggedDate.setPrefSize(200,25);
			loggedDate.getStyleClass().add("extraMessageInfo");
			vektra.setLoggedDate(loggedDate);
			
			Label updatedByLabel = new Label("UPDATED BY:");
			updatedByLabel.setPrefSize(160,25);
			updatedByLabel.getStyleClass().add("extraMessageInfoHeaders");
			
			Label whoUpdated = new Label("-");
			whoUpdated.setPrefSize(100,25);
			whoUpdated.getStyleClass().add("extraMessageInfo");
			vektra.setWhoUpdated(whoUpdated);
			
			Label updatedDateLabel = new Label("DATE:");
			updatedDateLabel.setPrefSize(100,25);
			updatedDateLabel.getStyleClass().add("extraMessageInfoHeaders");
			
			Label updatedDate = new Label("-");
			updatedDate.setPrefSize(200,50);
			updatedDate.getStyleClass().add("extraMessageInfo");
			vektra.setUpdatedDate(updatedDate);
			
			
		extraInfoPane.addColumn(0, loggedByLabel);
		extraInfoPane.addRow(1, updatedByLabel);
		extraInfoPane.addColumn(1, whoLogged);
		extraInfoPane.addRow(1, whoUpdated);
		extraInfoPane.addColumn(2, dateLabel);
		extraInfoPane.addRow(1, updatedDateLabel);
		extraInfoPane.addColumn(3, loggedDate);
		extraInfoPane.addRow(1, updatedDate);

		messagePane.addRow(4, extraInfoPane);
		GridPane.setColumnSpan(extraInfoPane, 2);
		
		return messagePane;
	}
	
	public static void setupComments(Collection<Comment> currentComments, TableView<Comment> comments, Vektra vektra) {
		
		comments.getItems().clear();
		if( !currentComments.isEmpty() ){
			ObservableList<Comment> list = FXCollections.observableArrayList(currentComments);
			comments.setItems(list);
			
			Collections.sort(list,new Comparator<Comment>(){

				@Override
				public int compare(Comment one, Comment two) {
					return one.timePosted.compareTo(two.timePosted);
				}
				
			});
		}
		
		// Menu for when the user right clicks a comment
		final ContextMenu contextMenu = getCommentPopupMenu(vektra);
		
		
		TableColumn<Comment, String> commenterColumn = new TableColumn<Comment,String>("POSTER");
		commenterColumn.setSortable(false);
		commenterColumn.setMinWidth(80);
		commenterColumn.setMaxWidth(80);
		commenterColumn.setCellValueFactory(new PropertyValueFactory<Comment, String>("poster"));
		commenterColumn.setCellFactory(new Callback<TableColumn<Comment, String>, TableCell<Comment, String>>() {
	        public TableCell<Comment, String> call(TableColumn<Comment, String> param) {
	            return new TableCell<Comment, String>() {

	                @Override
	                public void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!isEmpty()) {
	                        this.getStylesheets().add("css/buglist.css");
	                        this.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->commentSelected(a,comments,vektra));
	                		this.setContextMenu(contextMenu);
	                        
	                        setText(item);
	                        
	                    }
	                }
	            };
	        }
	    });
		if( !comments.getColumns().isEmpty() ){
			commenterColumn.setSortType(comments.getColumns().get(0).getSortType());
		}
		
		TableColumn<Comment, String> messageColumn = new TableColumn<Comment, String>("MESSAGE");
		messageColumn.setSortable(false);
		messageColumn.setCellValueFactory(new PropertyValueFactory<Comment, String>("message"));
		messageColumn.setCellFactory(new Callback<TableColumn<Comment, String>, TableCell<Comment, String>>() {

		        @Override
		        public TableCell<Comment, String> call(TableColumn<Comment, String> param) {
		            TableCell<Comment, String> cell = new TableCell<Comment, String>(){
		            	@Override
		                public void updateItem(String item, boolean empty) {
		                    super.updateItem(item, empty);
		                    if (!isEmpty()) {
		                        setText(item);
		                    }
		                }
		            };
		            Text text = new Text();
		            text.getStyleClass().add("table-cell");
		            cell.setGraphic(text);
		            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		            cell.getStylesheets().add("css/buglist.css");
		            text.wrappingWidthProperty().bind(cell.widthProperty());
		            text.textProperty().bind(cell.itemProperty());
		            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, (a)->commentSelected(a,comments,vektra));
		            cell.setContextMenu(contextMenu);
		            return cell ;
		        }
        });
		if( !comments.getColumns().isEmpty() ){
			messageColumn.setSortType(comments.getColumns().get(1).getSortType());
		}
		
		@SuppressWarnings("rawtypes")
		TableColumn sorting = comments.getSortOrder().isEmpty() ? null : comments.getSortOrder().get(0);
		if( sorting == null ){
			// Ignore
		}
		else if( sorting == comments.getColumns().get(2) ){
			comments.getSortOrder().set(0, commenterColumn);
		}
		else if( sorting == comments.getColumns().get(2) ){
			comments.getSortOrder().set(0, messageColumn);
		}
		
		comments.getColumns().clear();
		comments.getColumns().addAll(commenterColumn,messageColumn);	
		comments.sort();
		
//		messageColumn.setPrefWidth(320);
		//messageColumn.minWidthProperty().bind(commentScroll.widthProperty());
	}

	private static void commentSelected(MouseEvent t, TableView<Comment> comments, Vektra vektra) {
		TableCell c = (TableCell) t.getSource();
        int index = c.getIndex();
        if( index > comments.getItems().size() ){
        	vektra.setSelectedComment(null);
        	return;
        }
        
        // Select the comment
        vektra.setSelectedComment(comments.getItems().get(index));
		System.out.println("selected Comment: " + vektra.getSelectedComment());
		
		System.out.println("Clicked");
		if( t.isSecondaryButtonDown() ){
			c.getContextMenu().show(vektra.getPrimaryStage());
		}
	}
	
	private static ContextMenu getCommentPopupMenu(Vektra vektra) {
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem copy = new MenuItem("Copy Text");
		MenuItem edit = new MenuItem("Edit");
		MenuItem delete = new MenuItem("Delete");
		copy.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	final Clipboard clipboard = Clipboard.getSystemClipboard();
		        final ClipboardContent content = new ClipboardContent();
		        
		        Comment selectedComment = vektra.getSelectedComment();
		        String commentCopy = selectedComment.getPoster() + ": " + selectedComment.message;
		        content.putString(commentCopy);
		        clipboard.setContent(content);
		    }
		});
		
		edit.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	
		    	Comment selectedComment = vektra.getSelectedComment();
		    	String poster = selectedComment.poster;
		        if( !poster.equalsIgnoreCase(SQLData.getUsername()) ){
		        	PopupError.show("Edit Comment", "Can not edit comments not posted by you.");
		        	return;
		        }
		    	
		        String editedText = PopupTextField.show("Edit Comment","Please enter the text you wish to change the comment to.",selectedComment.getMessage());
		        if( editedText != null ){
		        	Comment newComment = new Comment(selectedComment.poster, selectedComment.timePosted, editedText, selectedComment.bugid);
		        	if( !SQLData.update(selectedComment, newComment) ){
		        		PopupError.show("Edit Comment", "Could not update comment!");;
		        	}
		        }
		    }
		});
		
		delete.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	Comment selectedComment = vektra.getSelectedComment();
		        String poster = selectedComment.poster;
		        if( !poster.equalsIgnoreCase(SQLData.getUsername()) ){
		        	PopupError.show("Delete Comment", "Can not delete comments not posted by you.");
		        	return;
		        }
		        
		        // Double check we want to delete this comment
		        boolean confirm = PopupConfirmation.show("Delete Comment", "Are you sure you want to delete this comment?");
		        if( !confirm ){
		        	return;
		        }
		        
		        boolean deleted = SQLData.delete(selectedComment);
		        if( deleted ){
		        	vektra.performPartialRefresh();
		        }
		        
		    }
		});
		

		contextMenu.getItems().addAll(copy, edit, delete);
		return contextMenu;
	}

}
