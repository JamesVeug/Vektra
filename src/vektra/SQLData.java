package vektra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import vektra.dialogs.PopupError;

public class SQLData {
	private static Connection con;
		
	private static String username = "-";
	private static String password = "-";
	private static String server = "mathparser.com";
	private static String table = "-";
	
	private static String lastUpdate = "";

	
	public static ObservableList<BugItem> getUpdatedData(){
		connect();
		
		if( !isConnected() ){
			return null;
		}
		
		System.out.println("Getting updated data");

		try {

			// Get what the current date off the server is.
			String currentDate = retrieveCurrentTime();
			
			// Get all the updates from the last time we performed a select
			String previousDate = lastUpdate;
			
			System.out.println("Current:  " + currentDate);
			System.out.println("Previous: " + previousDate);
			
			
			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
			
			Statement st = con.createStatement();// createStatement();
			
			// Get all the bugid's that have been updated since the last time we updated
			String dateQuery = "(SELECT `bugid` FROM `bugdates` WHERE `lastupdated` >= '" + previousDate + "')";
			System.out.println("Date Query: '" + dateQuery + "'");
			

//			ObservableList<BugItem> bugst = FXCollections.observableArrayList();
//			ResultSet results = st.executeQuery(dateQuery);
//			// Get all the bugs that we queried
//			processResults(results, bugst);
//			System.out.println("DATE QUERY: " + bugst.size());
			
			String selectionQuery = "SELECT b.bugid, date, message,poster, priority, status, tag, link FROM "
									+ "bugs b, priorities p, statuses s , tags t, screenshots h, " + dateQuery + " AS d "
									+ "WHERE "
									+ "d.bugid = b.bugid AND d.bugid = p.bugid AND d.bugid = s.bugid AND d.bugid = t.bugid AND d.bugid = h.bugid;";
			//System.out.println("Selection Query: \n'" + selectionQuery + "'"); 
							
			// Get all the information
			ResultSet result = st.executeQuery(selectionQuery);
		
			// Get all the bugs that we queried
			processResults(result, bugs);
			
			
			// Save lastUpdate as currentDate so we can get the next load of date!
			if( !bugs.isEmpty() ){
				lastUpdate = currentDate;
				System.out.println("Size: " + bugs);
			}
			
			return bugs;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Converts the given objects from the result object into BugItem's and adds them to the bugs list.
	 * @param result Where the bugs are stored and retreived off the database.
	 * @param bugs Where to store the bugs
	 */
	private static void processResults(ResultSet result, ObservableList<BugItem> bugs){
		//long startTime = System.currentTimeMillis();
		
		try {

			Map<Integer,BugItem> bugMapping = new HashMap<Integer,BugItem>();
			while( result.next() ){
				Integer id = result.getInt("bugid");
				String date = result.getString("date");
				String message = result.getString("message");
				String poster = result.getString("poster");
				String priority = result.getString("priority");
				String status = result.getString("status");
				
				// Multiple entries
				String tag = result.getString("tag");
				String link = result.getString("link");
	
				// Get screenshots
				Image screenshot = getScreenshot(link);
				
				if( bugMapping.containsKey(id) ){
					BugItem saved = bugMapping.get(id);
					
					// Add another tag
					if( tag != null ){
						saved.addTag(tag);
					}
					
					// Add another screenshot
					if( screenshot != null ){
						saved.addScreenshot(link, screenshot);
					}
				}
				else{
					BugItem bug = new BugItem(id, tag,priority, status, poster,message,date, screenshot != null ? link : null, screenshot != null ? screenshot : null);
					bugs.add(bug);
					bugMapping.put(id, bug);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//long endTime = System.currentTimeMillis();
		//System.out.println("Time Taken to search: " + (endTime - startTime) + " " + bugs.size() + " bugs!");
	}
	
	public static ObservableList<BugItem> getData(){
		connect();
		if( !isConnected() ){
			System.out.println("Disconnected");
			return null;
		}
		
		try {
			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
			
			// Get current time
			lastUpdate = retrieveCurrentTime();
			
			
			Statement st = con.createStatement();			
			ResultSet result = st.executeQuery("SELECT b.bugid, date, message,poster, priority, status, tag, link FROM "
												+ "bugs b, priorities p, statuses s , tags t, screenshots h WHERE "
												+ "b.bugid = p.bugid AND b.bugid = s.bugid AND b.bugid = t.bugid AND b.bugid = h.bugid;");
						
					
			
			processResults(result, bugs);
			
			return bugs;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	/**
	 * Gets the current time off the database
	 * @return
	 */
	private static String retrieveCurrentTime() {
		
		Statement st;
		try {
			st = con.createStatement();
			ResultSet result = st.executeQuery("SELECT NOW() currentDate");
			while( result.next() ){
				String date = result.getString("currentDate");
				date = date.substring(0, date.lastIndexOf("."));
				return date;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}



	private static Image getScreenshot(String link) {
		//System.out.println("Link: " + link);
		if( link != null && !link.isEmpty() ){
			try{
				Image image = new Image(link, 400, 300, true, true);
				//System.out.println("Loaded image: '" + link + "'");
				return image;
			}catch( IllegalArgumentException e ){
				//popupException("Can not load image: '" + link + "'", e);
				//System.err.println("Can not load image: '" + link + "'");
				//System.err.println(e.message);
				//e.printStackTrace();
			}
		}
		
		return null;
	}

	private static void connect() {
		try {
			if( con != null && !con.isClosed() ){
				return;
			}
			
			
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setServerName(server);
			dataSource.setDatabaseName(table);
			dataSource.setPort(3306);			
			con = dataSource.getConnection();
			

			System.out.println("SQL Connect Try Finished");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("SQL Connect Finished");
	}
	
	public static void close(){
		System.out.println("SQLDATA WARNING: CLOSING");
		try {
			if( con != null && !con.isClosed() ){
				con.close();
			}
			System.out.println("SQLDATA WARNING: CLOSED");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean connect(String server, String name, String pass){
		table = server;
		username = name;
		password = pass;
		
		connect();
		
		return isConnected();
	}

	public static boolean isConnected() {
		try {
			return con != null && !con.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getUsername() {
		return username;
	}

	public static boolean insert(BugItem bug) {
		if( !isConnected() ){
			System.out.println("Not connected!");
			return false;
		}
			
		String bugcommand = "INSERT INTO bugs (`poster`, `message`) VALUES ('" + username + "', '" + fix(bug.message) + "')";
		boolean bugcommandConfirmation = submitQuery(bugcommand);
		if( !bugcommandConfirmation ){
			System.out.println("Did not submit bug!");
			return false;
		}
		
		
		// Get the bug we just added		
		int bugid = getSubmittedBugID(username, bug.message );
		String prioritycommand = "INSERT INTO priorities (`priority`, `bugid`) VALUES ('" + bug.priority + "', '" + bugid + "')";
		boolean prioritycommandConfirmation = submitQuery(prioritycommand);
		if( !prioritycommandConfirmation ){
			System.out.println("Did not submit Priority!");
			return false;
		}	
		
		String statuscommand = "INSERT INTO statuses (`status`, `bugid`) VALUES ('"+bug.status+"', '" + bugid + "')";
		boolean statusstatusConfirmation = submitQuery(statuscommand);
		if( !statusstatusConfirmation ){
			System.out.println("Did not submit Status!");
			return false;
		}
		
		String screenshotcommand;
		if( bug.imageMap != null && !bug.imageMap.isEmpty() ){
			screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES " + listToMultipleValues(bug.imageMap.keySet(), String.valueOf(bugid));
		}
		else{
			screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES ('" + "NULL" + "', '" + bugid + "');";
		}
		boolean screenshotcommandConfirmation = submitQuery(screenshotcommand);
		if( !screenshotcommandConfirmation ){
			System.out.println("Did not submit Screenshots!");
			return false;
		}
		
		String tagcommand = "INSERT INTO tags (`tag`, `bugid`) VALUES " + listToMultipleValues(bug.tags, String.valueOf(bugid));
		boolean tagcommandConfirmation = submitQuery(tagcommand);
		if( !tagcommandConfirmation ){
			System.out.println("Did not submit TAGS!!");
			return false;
		}
		
		return true;
	}

	private static int getSubmittedBugID(String username, String message) {
		System.out.println("Looking for Submitted bugs's ID!");
		Statement st;
		try {
			st = con.createStatement();
			String selection = "SELECT bugid FROM (select bugid,date FROM bugs WHERE poster = '" + username + "' order by convert(date, datetime) DESC LIMIT 1) as TEMP";
			ResultSet result = st.executeQuery(selection);
			while( result.next() ){
				int id = result.getInt("bugid");
				System.out.println("\tRecieved: " + id);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Couldn't find ID!");
		return -1;
	}

	private static boolean submitQuery(String bugcommand) {
		System.out.println("Submitting Query: '" + bugcommand + "'");
		Statement st;
		try {
			st = con.createStatement();
			boolean result = st.execute(bugcommand);
			
			System.out.println("Finished Submitting Query: '" + result + "'");
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	private static String listToMultipleValues(Collection<String> list, String value){
		if( list == null || list.isEmpty() ){
			return null;
		}
		
		String string = "";
		
		
		int i = 0;
		for(String s : list){
			string += "( '" + s + "', '" + value + "')";
			if( (++i) < list.size() ){
				string += ",";
			}
		}
		
		return string;
	}

	public static boolean delete(BugItem item) {
		System.out.println("Deleting Bug " + item);
		if( !isConnected() ){
			System.out.println("Not connected");
			return false;
		}
		else if( item == null ){
			System.out.println("Given null bug");
			return false;
		}

		System.out.println("Bug ID: " + item.ID);
		boolean deleted = submitQuery("DELETE FROM bugs where bugid = " + item.ID);
		if( !deleted ){
			System.out.println("Did not delete bug!");
			return false;
		}	
		
		
		System.out.println("Finished Deleting");
		return true;
	}


	public static boolean update(BugItem oldBug, BugItem newBug) {
		System.out.println("Updating bug " + oldBug.ID);
		
		// Make sure we are updating the right bug
		if( oldBug.ID != newBug.ID ){
			PopupError.show("Can not update Bug", "BugID's do not match! Old: " + oldBug.ID + " New: " + newBug.ID);
			return false;
		}

		// Get ID
		String ID = String.valueOf(newBug.ID);
		
		// Add everything to a single query to make sure it works
		List<String> queries = new ArrayList<String>();

		// Images have changed
		if( oldBug.imageMap.values().size() != newBug.imageMap.values().size() ){
			System.out.println("Different sizes");
			
			List<String> newImages = new ArrayList<String>();
			List<String> deleted = new ArrayList<String>();
			
			// Check for what is new in the new bugs images
			for(String newLink : newBug.imageMap.keySet()){
				if( !oldBug.imageMap.keySet().contains(newLink) ){
					newImages.add(newLink);
				}
			}
			
			// Check for what has been deleted
			for(String oldLink : oldBug.imageMap.keySet()){
				if( !newBug.imageMap.keySet().contains(oldLink) ){
					deleted.add(oldLink);
				}
			}
			
			// Make sure we have at least 1 image!
			if( newImages.isEmpty() ){
				System.out.println("No Images in new bug");
				
				// TODO should only delete what is in the delete list to avoid conflict!
				// Delete everything from screenshots and add a NULL
				queries.add("Delete from screenshots where bugid = " + ID);
				queries.add("INSERT INTO screenshots (`link`, `bugid`) VALUES ('NULL', '" + ID + "')");
			}
			else{
				System.out.println("More new bugs");
				
				
				// Delete removed images
				if( !deleted.isEmpty() ){
					System.out.println("Deleteing bugs");
					String seperated = "";
					
					int i = 0;
					for(String s : deleted ){
						seperated += "'" + s + "'";
						if( ++i < deleted.size() ){
							 seperated += ", ";
						}
					}
					
					queries.add("Delete * from screenshots where bugid = " + ID + " AND link IN (" + seperated + ")");					
					
				}
				
				// Add the new images
				if( !newImages.isEmpty() ){
					System.out.println("Adding new bugs");

					queries.add("INSERT INTO screenshots (`link`, `bugid`) VALUES " + listToMultipleValues(newImages, ID));
				}
			}
		}
		
		// Get the date off the server
		String currentTime = retrieveCurrentTime();

		// Message has changed
		if( !oldBug.message.equals(newBug.message) ){
			queries.add("UPDATE bugs SET message = '" + fix(newBug.message) + "' WHERE BugId = " + ID + "; ");
		}

		// Priority has changed
		if( !oldBug.priority.equals(newBug.priority) ){
			queries.add("UPDATE priorities SET priority = '" + newBug.priority + "' WHERE BugId = " + ID + "; ");
		}

		// Status has changed
		if( !oldBug.status.equals(newBug.status) ){
			queries.add("UPDATE statuses SET status = '" + newBug.status + "' WHERE BugId = " + ID + "; ");
		}
		

		// Tags have changed
		if( oldBug.tags.size() != newBug.tags.size() ){
			
		}

		// The person editing is not the same person that created the bug
		if( !oldBug.who.equals(username)  ){
			
		}
		
		
		if( queries.isEmpty() ){
			PopupError.show("Can not update Bug", "No changes were made to the bug.");
			return false;
		}
		
		// Save current time so we know it has been edited!
		
		// Attempt updating
		System.out.println("Updating with time: " + currentTime);
		updateBugsLastReportedDate(newBug, currentTime);

		
		
		connect();
		
		if( isConnected() ){
			
			try{
				System.out.println("Beginning Update with " + queries.size() + " queries.");

				long start = System.currentTimeMillis();
				for(int i = 0; i < queries.size(); i++){
					String query = queries.get(i);
					System.out.println("\tUpdating with: \n\t" + query);
					
					long updateStart = System.currentTimeMillis();
					Statement st = con.createStatement();
					boolean result = st.execute(query);
					long updateEnd = System.currentTimeMillis();
					long updateTime = updateEnd-updateStart;
					
					
					System.out.println("\t\tFinished Submitting Update Query: '" + result + "' " + updateTime + "ms.");
				}
				
				long end = System.currentTimeMillis();
				long time = end-start;
				System.out.println("\tFinished Update: " + time + "ms.");
				
				return true;
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return false;
	}

	/**
	 * Adds or removes required characters from the message so there aren't any confusions with the SQL query
	 * @param message What to fix
	 * @return fixed string version of the message
	 */
	private static String fix(String message) {
		
		String quotes = message.replaceAll("'", "''");
		
		return quotes;
	}

	/**
	 * 
	 * @param newBug
	 */
	private static void updateBugsLastReportedDate(BugItem bug, String currentTime) {

		System.out.println("UPDATING BUG");
		
		// TODO Implement and TEST adding new rows to the bugdates Table!
		// TODO Test Vektra method where the the updated items are selected and modify the current table
		
		// Attempt to update
		String updateBug = "UPDATE `bugdates` SET `lastupdated` = '"+currentTime+"' WHERE `bugid` = '"+bug.ID+"';";

		try {
		
			Statement st = con.createStatement();
			
			int b = st.executeUpdate(updateBug);
			int r = st.getUpdateCount();

			System.out.println("UPDATED: " + r + " b " + b);
			if( r == 0 ){
				String insertUpdate = "INSERT INTO `bugdates`(`bugid`, `lastupdated`) VALUES ('"+bug.ID+"', '"+currentTime+"');";
				
				Statement st2 = con.createStatement();
				int inserted = st2.executeUpdate(insertUpdate);
				System.out.println("INSERTED: " + inserted);
			}
			else if( r < 0 ){
				throw new RuntimeException("Updating returned " + r);
			}
		
		
		
		// Else
		
		
		
		
		// Insert
		

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

























