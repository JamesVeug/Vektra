package vektra;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vektra.dialogs.PopupError;
import vektra.resources.R;

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
			String dateQuery = "(SELECT * FROM `bugdates` WHERE `lastupdated` >= '" + previousDate + "')";
			System.out.println("Date Query: '" + dateQuery + "'");

			
			String selectionQuery = "SELECT dates.bugid, dates.lastupdated, dates.whoupdated, date, message, poster, priority, status, tag, tagid, link, screenshotid, version, stage " 
									+ "FROM "
									+ dateQuery + " AS dates "
									+ "LEFT JOIN `bugs`  "
									+ "ON dates.bugid = bugs.bugid " 
									+ "LEFT JOIN `bugdates` "
									+ "ON dates.bugid = bugdates.bugid " 
									+ "LEFT JOIN `priorities` "
									+ "ON dates.bugid = priorities.bugid " 
									+ "LEFT JOIN `versions` "
									+ "ON dates.bugid = versions.bugid " 
									+ "LEFT JOIN `statuses` "
									+ "ON dates.bugid = statuses.bugid " 
									+ "LEFT JOIN `tags` "
									+ "ON dates.bugid = tags.bugid " 
									+ "LEFT JOIN `screenshots` "
									+ "ON dates.bugid = screenshots.bugid " 
									+ "ORDER BY dates.bugid;";
									
			
			/*String selectionQuery = "SELECT b.bugid, d.whoupdated, d.lastupdated, date, message,poster, priority, status, tag, tagid, link FROM "
									+ "bugs b, priorities p, statuses s , tags t, screenshots h, " + dateQuery + " AS d "
									+ "WHERE "
									+ "d.bugid = b.bugid AND d.bugid = p.bugid AND d.bugid = s.bugid AND d.bugid = t.bugid AND d.bugid = h.bugid;";*/
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
				String version = result.getString("version");
				String stage = result.getString("stage");

				// Updated Info
				String whoUpdated = result.getString("whoupdated");
				String lastUpdated = result.getString("lastupdated");

				
				// Multiple entries
				String tag = result.getString("tag");
				int tagid = result.getInt("tagid");
				
				// Screensots
				String link = result.getString("link");
				int screenshotid = result.getInt("screenshotid");
	
				// Get screenshots
				BugImage screenshot = getScreenshot(screenshotid, link);
				Tag tagItem = new Tag(tagid, id, tag);
				
				if( bugMapping.containsKey(id) ){
					BugItem saved = bugMapping.get(id);
					
					// Add another tag
					if( tag != null ){
						saved.addTag(tagItem);
					}
					
					// Add another screenshot
					if( screenshot != null ){
						saved.addScreenshot(link, screenshot);
					}
					
					if( priority != null ){
						saved.priority = Priority.get(priority);
					}
					
					if( status != null ){
						saved.status = Status.get(status);
					}
				}
				else{
					BugItem bug = new BugItem(id, tagItem,Priority.get(priority), Status.get(status), poster,message,date, new Version(version,Stage.get(stage)), screenshot != null ? link : null, screenshot != null ? screenshot : null);
					bugs.add(bug);
					bugMapping.put(id, bug);
					bug.whoUpdated = whoUpdated;
					bug.lastUpdate = lastUpdated;
					//System.out.println("\tWhoUpdated: " + bug.whoUpdated);
					//System.out.println("\tLastUpdate: " + bug.lastUpdate);
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
			return null;
		}
		
		try {
			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
			
			// Get current time
			lastUpdate = retrieveCurrentTime();
			String query = "SELECT bugs.bugid, lastupdated, whoupdated, date, message, poster, priority, status, tag, tagid, link, screenshotid, version, stage "
							+"FROM `bugs` "
							+"LEFT JOIN `bugdates` "
							+"ON bugs.bugid = bugdates.bugid "
							+"LEFT JOIN `priorities` "
							+"ON bugs.bugid = priorities.bugid "
							+"LEFT JOIN `versions` "
							+"ON bugs.bugid = versions.bugid "
							+"LEFT JOIN `statuses` "
							+"ON bugs.bugid = statuses.bugid "
							+"LEFT JOIN `tags` "
							+"ON bugs.bugid = tags.bugid "
							+"LEFT JOIN `screenshots` "
							+"ON bugs.bugid = screenshots.bugid "
							
							+"ORDER BY bugs.bugid;";
			
			System.out.println(query);
			
			Statement st = con.createStatement();	
			ResultSet result = st.executeQuery(query);
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
	public static String retrieveCurrentTime() {
		
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



	private static BugImage getScreenshot(int screenshotid, String link) {
		//System.out.println("Link: " + link);
		if( link != null && !link.isEmpty() ){
			try{
				System.out.println("Screenshotid " + screenshotid);
				BugImage image = R.getImage(link, 400, 300, screenshotid);
				image.screenshotID = screenshotid;
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

	public static int insert(BugItem bug) {
		if( !isConnected() ){
			System.out.println("Not connected!");
			return -1;
		}
		
		String currentTime = retrieveCurrentTime();
			
		String bugcommand = "INSERT INTO bugs (`poster`, `message`) VALUES ('" + username + "', '" + fix(bug.message) + "')";
		boolean bugcommandConfirmation = submitQuery(bugcommand);
		if( !bugcommandConfirmation ){
			System.out.println("Did not submit bug!");
			return -2;
		}
		
		
		// Get the bug we just added		
		int bugid = getSubmittedBugID(username, bug.message );
		bug.ID = bugid;
		String prioritycommand = "INSERT INTO priorities (`priority`, `bugid`) VALUES ('" + bug.priority + "', '" + bugid + "')";
		boolean prioritycommandConfirmation = submitQuery(prioritycommand);
		if( !prioritycommandConfirmation ){
			System.out.println("Did not submit Priority!");
			return -3;
		}	
		
		String statuscommand = "INSERT INTO statuses (`status`, `bugid`) VALUES ('"+bug.status+"', '" + bugid + "')";
		boolean statusstatusConfirmation = submitQuery(statuscommand);
		if( !statusstatusConfirmation ){
			System.out.println("Did not submit Status!");
			return -4;
		}
		
		String versioncommand = "INSERT INTO versions (`version`, `bugid`) VALUES ('"+bug.version+"', '" + bugid + "')";
		boolean versionConfirmation = submitQuery(versioncommand);
		if( !versionConfirmation ){
			System.out.println("Did not submit Version!");
			return -5;
		}
		
		//String screenshotcommand;
		if( bug.imageMap != null && !bug.imageMap.isEmpty() ){
			String screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES " + listToMultipleValues(bug.imageMap.keySet(), String.valueOf(bugid));
			boolean screenshotcommandConfirmation = submitQuery(screenshotcommand);
			if( !screenshotcommandConfirmation ){
				System.out.println("Did not submit Screenshots!");
				return -6;
			}
		}
//		else{
//			screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES ('" + "NULL" + "', '" + bugid + "');";
//		}
		
		
		String tagcommand = "INSERT INTO tags (`tag`, `bugid`) VALUES " + listToMultipleValues(bug.getTagMessages(), String.valueOf(bugid));
		boolean tagcommandConfirmation = submitQuery(tagcommand);
		if( !tagcommandConfirmation ){
			System.out.println("Did not submit TAGS!!");
			return -7;
		}
		
		// Tell everyone this has been updated
		updateBugsLastReportedDate(bug,currentTime);
		System.out.println("Inserted bug with ID " + bugid);
		
		return bugid;
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

		// Get currentTime
		String currentTime = retrieveCurrentTime();
		
		System.out.println("Bug ID: " + item.ID);
		boolean deleted = submitQuery("DELETE FROM bugs where bugid = " + item.ID);
		if( !deleted ){
			System.out.println("Did not delete bug!");
			return false;
		}	
		
		// Report update
		updateBugsLastReportedDate(item, currentTime);
		
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
		
		// Version has changed
		System.out.println(newBug.version);
		if( newBug.version != null && oldBug.version == null ){
			// insert new version!
			queries.add("INSERT INTO versions (`version`,`bugid`) VALUES ('" + newBug.version + "', '"+ID+"');");
		}
		else if( oldBug != null && !oldBug.version.equals(newBug.version) ){
			queries.add("UPDATE versions SET version = '" + newBug.version + "' WHERE BugId = " + ID + "; ");
		}

		// Tags have changed
		List<String> tagQueries = getModifiedTags(oldBug,newBug);
		if( !tagQueries.isEmpty() ){
			for(String s : tagQueries ){
				queries.add(s);
			}
		}

		// The person editing is not the same person that created the bug
		List<String> screenshotQueries = getModifiedScreenshots(oldBug, newBug);
		if( !screenshotQueries.isEmpty() ){
			for(String s : screenshotQueries ){
				queries.add(s);
			}
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
					
					System.out.println("\tUpdating with: \n\t\t'" + query + "'");
					
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

	private static List<String> getModifiedTags(BugItem oldBug, BugItem newBug) {
		
		List<String> queries = new ArrayList<String>();
		System.out.println("OldTags: " + oldBug.tags);
		System.out.println("NewTags: " + newBug.tags);
		
		// Step through each tag in oldBug
		for( Tag tag : oldBug.tags ){
		
			// Is it in new Bug?
			if( !newBug.tags.contains(tag) ){
			
				// No?
					
				// Delete Tag from DB
				queries.add("DELETE FROM `tags` WHERE `tagid` = '" + tag.tagid + "';");
			}
		}
		
		// Step through each tag in newBug
		for( Tag tag : newBug.tags ){
		
			// Is it in old Bug?
			if( !oldBug.tags.contains(tag) ){
				
				// No?
					
				// Insert Tag into DB
				queries.add("INSERT INTO `tags` (`tag`, `bugid`) VALUES ('"+tag.message+"', '"+tag.bugid+"');");
			}
		}
		
		
		return queries;
	}
	
	private static List<String> getModifiedScreenshots(BugItem oldBug, BugItem newBug) {
		
		List<String> queries = new ArrayList<String>();
		System.out.println("Old " + oldBug.ID);
		System.out.println("New " + newBug.ID);
		System.out.println("OldLinks: " + oldBug.imageMap.hashCode() + " " + oldBug.imageMap.keySet());
		System.out.println("NewLinks: " + newBug.imageMap.hashCode() + " " + newBug.imageMap.keySet());
		
		// Step through each tag in oldBug
		for( String link : oldBug.imageMap.keySet() ){
		
			// Is it in new Bug?
			if( !newBug.imageMap.keySet().contains(link) ){
			
				// No?
					
				// Delete Tag Screenshot DB
				queries.add("DELETE FROM `screenshots` WHERE `bugid` = '" + newBug.ID + "' AND `link` = '"+ link +"';");
			}
		}
		
		// Step through each tag in newBug
		for( String link : newBug.imageMap.keySet() ){
		
			// Is it in old Bug?
			if( !oldBug.imageMap.keySet().contains(link) ){
				
				// No?
					
				// Insert Screenshot into DB
				queries.add("INSERT INTO screenshots (`link`, `bugid`) VALUES ('" + link + "', '" + oldBug.ID + "');");
			}
		}
		
		
		return queries;
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
		String updateBug = "UPDATE `bugdates` SET `lastupdated` = '"+currentTime+"', `whoupdated` = '" + username + "' WHERE `bugid` = '"+bug.ID+"';";

		try {
		
			Statement st = con.createStatement();
			
			int b = st.executeUpdate(updateBug);
			int r = st.getUpdateCount();

			System.out.println("UPDATED: " + r + " b " + b);
			if( r == 0 ){
				String insertUpdate = "INSERT INTO `bugdates`(`bugid`, `lastupdated`, `whoupdated`) VALUES ('"+bug.ID+"', '"+currentTime+"', '" + username + "');";     
				
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

	/**
	 * @return the lastUpdate
	 */
	public static String getLastUpdate() {
		return lastUpdate;
	}

	public static String getServer() {
		return table;
	}

}

























