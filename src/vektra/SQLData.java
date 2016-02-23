package vektra;

import java.sql.Connection;
import java.sql.Date;
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

/**
 * SQLData class contains all SQL related queries to and from the Database 
 * @author James
 *
 */
public class SQLData {
	
	// Connection to the database if there is one 
	private static Connection con;
		
	// Information that connects to the server
	private static String username = "-";
	private static String password = "-";
	private static String server = "-";
	private static String database = "-";

	// Last update time performed to check for new data
	private static String lastUpdate = "";

	/**
	 * Performs a check against the database to see if any new updates to the database have been performed.
	 * If we do not have a connection to the database. This will attempt to reconnect. If it it's still not connected, it will then return null.
	 * @return new ObservableList containing the complete BugItems that have been updated in the database.
	 */
	public static ObservableList<BugItem> getUpdatedData(){
		
		// Connect if we aren't connected yet.
		connect();
		
		// If we still aren't connected. return null
		if( !isConnected() ){
			return null;
		}
		

		try {

			// Get what the current date off the server is.
			String currentDate = retrieveCurrentTime();
			
			// Get all the updates from the last time we performed a select
			String previousDate = lastUpdate;
			
			
			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
			
			Statement st = con.createStatement();
			
			// Get all the bugid's that have been updated since the last time we updated
			String dateQuery = "(SELECT * FROM `bugdates` WHERE `lastupdated` >= '" + previousDate + "')";
			System.out.println("Date Query: '" + dateQuery + "'");

			// Select all the bugs that have been updated since we last checked the database
			String selectionQuery = "SELECT dates.bugid, dates.lastupdated, dates.whoupdated, date, message, poster, priority, status, tag, tagid, link, screenshotid, version, stage, comment, commentid, whocommented, datecommented " 
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
									+ "LEFT JOIN `comments` "
									+ "ON dates.bugid = comments.bugid " 
									
									+ "ORDER BY dates.bugid;";
									
							
			// Get all the information
			ResultSet result = st.executeQuery(selectionQuery);
		
			// Get all the bugs that we queried
			processResults(result, bugs);
			
			
			// If we received an update. Record the time
			if( !bugs.isEmpty() ){
				lastUpdate = currentDate;
			}
			
			// Finished getting update
			return bugs;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// An error occured. Just return null
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

				
				//
				// Multiple entries
				//
				
				// Comments
				String comment = result.getString("comment");
				Integer commentid = result.getInt("commentid");
				String whocommented = result.getString("whocommented");
				Date datecommented = result.getDate("datecommented");
				
				// Tags
				String tag = result.getString("tag");
				int tagid = result.getInt("tagid");
				Tag tagItem = new Tag(tagid, tag);
				
				// Screensots
				String link = result.getString("link");
				int screenshotid = result.getInt("screenshotid");
				BugImage screenshot = getScreenshot(screenshotid, link);
				
				
				// If we haven't recorded this bug yet. Save it as a new entry 
				if( !bugMapping.containsKey(id) ){
					BugItem bug = new BugItem(id, tagItem,Priority.get(priority), Status.get(status), poster,message,date, new Version(version,Stage.get(stage)), screenshot != null ? link : null, screenshot != null ? screenshot : null);
					bugs.add(bug);
					bugMapping.put(id, bug);
					bug.whoUpdated = whoUpdated;
					bug.lastUpdate = lastUpdated;
					
					if( comment != null ){
						bug.addComment(new Comment(comment, commentid, whocommented, datecommented, id));
					}
				}
				else{
					// Already saved this bug. So we must have additional entries for the bug
					// that will be listed under the 'Multiple entries' comment, above.
					
					// Get bug that we have already saved
					BugItem saved = bugMapping.get(id);
					
					// Add another tag
					if( tag != null ){
						saved.addTag(tagItem);
					}
					
					// Add another screenshot
					if( screenshot != null ){
						saved.addScreenshot(link, screenshot);
					}
					
					// Add another comment
					if( comment != null ){
						saved.addComment(new Comment(comment, commentid, whocommented, datecommented, saved.ID));
					}
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all data from the database and returns a new ObservableList that contains BugItems converted from the data that was pulled.
	 * @return List of all BugItems from the database, otherwise null if we could not connect or an exception was called.
	 */
	public static ObservableList<BugItem> getData(){
		
		// Connect if required
		connect();
		
		// Still not connected
		if( !isConnected() ){
			return null;
		}
		
		try {
			
			// Get current time
			lastUpdate = retrieveCurrentTime();
			
			// Select all bugs, tags, screenshots and order them by BugID.
			String query = "SELECT bugs.bugid, lastupdated, whoupdated, date, message, poster, priority, status, tag, tagid, link, screenshotid, version, stage, comment, commentid, whocommented, datecommented "
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
							+"LEFT JOIN `comments` "
							+"ON bugs.bugid = comments.bugid "
							
							+"ORDER BY bugs.bugid;";
			
			// Perform the query
			Statement st = con.createStatement();	
			ResultSet result = st.executeQuery(query);

			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
			
			// Convert data into BugItems, and store them in list above.
			processResults(result, bugs);
			
			// Return entire list of bugs
			return bugs;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// An error occured
		return null;
	}

	/**
	 * Gets the current time off the database
	 * @return String representation of the current date and time that is on the database
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

	/**
	 * Gets the Image from the given link.
	 * @param screenshotid ID to apply to a valid BugImage.
	 * @param link Where to get the image from ( online link )
	 * @return BugImage that stores the link and screenshotid if it's valid. Otherwise null
	 */
	private static BugImage getScreenshot(int screenshotid, String link) {
		
		// Check to make sure we have a valid link from the database
		if( link != null && !link.isEmpty() && !link.equals("NULL") ){
				
			// Get it from our database
			// Either from our computer, or download off the internet
			BugImage image = R.getImage(link, 400, 300, screenshotid);
			if( image != null ){
				image.screenshotID = screenshotid;
			}
			
			// Return BugImage
			return image;
		}
		
		// Could not get the image
		return null;
	}

	/**
	 * Attempts to reconnect to the database using the currently assigned database values
	 */
	private static void connect() {
		
		try {
			
			// Check to make sure we don't already have a valid connection
			if( con != null && !con.isClosed() ){
				return;
			}
			
			//System.out.println("SQL Connecting to Server (" + server + ", " + database + ", " + username + ", " + password + ")");
			
			// Set up a new DataSource on where we are connecting to
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setServerName(server);
			dataSource.setDatabaseName(database);
			dataSource.setPort(3306);			
			
			// Attempt to connect
			con = dataSource.getConnection();
			

		} catch (SQLException e) {
			e.printStackTrace();
			
			PopupError.show("Could not Connect", e.getMessage());
		}
	}
	
	/**
	 * Close the connection to the database 
	 */
	public static void close(){
		try {
			
			// Make sure we are connected
			if( con != null && !con.isClosed() ){
				// Stop the connection
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Assigns new connection info for our database to connect to
	 * Starts a new connection  
	 * @param server Public domain on where we are connecting to
	 * @param database Table of which we will pull information from
	 * @param name Username of person connecting
	 * @param pass Password of account
	 * @return If we are connected or not
	 */
	public static boolean connect(String server, String database, String name, String pass){
		
		// Assign new server information
		SQLData.server = server;
		SQLData.database = database;
		SQLData.username = name;
		SQLData.password = pass;
		
		// Connect to database
		connect();
		
		// Return if we are connected or not.
		return isConnected();
	}

	/**
	 * Check if the Database is currently connected
	 * @return True if we are connected. False if not
	 */
	public static boolean isConnected() {
		
		try {
			// Check for valid connection
			return con != null && !con.isClosed();
		} catch (SQLException e) {}
		
		// Not connected
		return false;
	}

	/**
	 * Gets current Username of person that is connected to the database
	 * @return String of username
	 */
	public static String getUsername() {
		return username;
	}

	/**
	 * Insert a new bug into the database
	 * @param bug What to insert into the database
	 * @return Integer value is the bug was inserted or not. Negative if failed. Otherwise returns the BugID that was inserted
	 */
	public static List<Integer> insert(BugItem bug) {
		List<Integer> errors = new ArrayList<Integer>();
		
		// Attempt to insert the bug!
		if( !isConnected() ){
			errors.add(-1);
			return errors;
		}
		
		// Get the current time
		String currentTime = retrieveCurrentTime();
			
		// Attempt inserting the bug
		String bugcommand = "INSERT INTO bugs (`poster`, `message`) VALUES ('" + username + "', '" + fix(bug.message) + "')";
		boolean bugcommandConfirmation = submitQuery(bugcommand);
		if( !bugcommandConfirmation ){
			errors.add(-2);
			
			// Could not report the bug. Do not process anything else
			return errors;
		}
		
		// Get the bug we just added		
		int bugid = getSubmittedBugID(bug.message );
		bug.ID = bugid;
		
		// Save the bugid in the errors
		errors.add(bug.ID);
		
		// Submit the priority
		String prioritycommand = "INSERT INTO priorities (`priority`, `bugid`) VALUES ('" + bug.priority + "', '" + bugid + "')";
		boolean prioritycommandConfirmation = submitQuery(prioritycommand);
		if( !prioritycommandConfirmation ){
			errors.add(-3);
			return errors;
		}	
		
		// Submit the Status
		String statuscommand = "INSERT INTO statuses (`status`, `bugid`) VALUES ('"+bug.status+"', '" + bugid + "')";
		boolean statusstatusConfirmation = submitQuery(statuscommand);
		if( !statusstatusConfirmation ){
			errors.add(-4);
		}
		
		// Submit the version
		String versioncommand = "INSERT INTO versions (`version`, `stage`, `bugid`) VALUES ('" + bug.version.version + "', '" + bug.version.stage + "', '" + bugid + "')";
		boolean versionConfirmation = submitQuery(versioncommand);
		if( !versionConfirmation ){
			errors.add(-5);
		}
		
		// Submit the Screenshots
		if( bug.imageMap != null && !bug.imageMap.isEmpty() ){
			String screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES " + listToMultipleValues(bug.imageMap.keySet(), String.valueOf(bugid));
			boolean screenshotcommandConfirmation = submitQuery(screenshotcommand);
			if( !screenshotcommandConfirmation ){
				errors.add(-6);
			}
		}
		
		// Submit the Tags
		String tagcommand = "INSERT INTO tags (`tag`, `bugid`) VALUES " + listToMultipleValues(bug.getTagMessages(), String.valueOf(bugid));
		boolean tagcommandConfirmation = submitQuery(tagcommand);
		if( !tagcommandConfirmation ){
			errors.add(-7);
		}
		
		// Create a record that this bug has been inserted
		updateBugsLastReportedDate(bug.ID,currentTime);
				
		// Return the results
		return errors;
	}

	/**
	 * Gets the BugId of the recently submitted bug
	 * This method uses the username in the database to get the person that just submitted the bug
	 * @param message Message of the bug reported
	 * @return int value of the bugid that was submitted. Otherwise -1 if failed.
	 */
	private static int getSubmittedBugID(String message) {
		
		try {
			
			// Create selection to get the BugID by getting the last bug submitted by the person with the SQLData's username.
			Statement st = con.createStatement();
			String selection = "SELECT bugid FROM (select bugid,date FROM bugs WHERE poster = '" + username + "' order by convert(date, datetime) DESC LIMIT 1) as TEMP";
			ResultSet result = st.executeQuery(selection);
			while( result.next() ){
				
				// Get the ID. Convert to an int and return it
				int id = result.getInt("bugid");
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		// Could not get BugID
		return -1;
	}

	/**
	 * Submits the given command to the database and returns true if the submission was successfuly
	 * @param query Query to send to the database
	 * @return True if submission was successfuly
	 */
	private static boolean submitQuery(String query) {
		
		try {
			// Submit query
			Statement st = con.createStatement();
			
			@SuppressWarnings("unused")
			boolean result = st.execute(query);
			
			// No errors occured.
			// Submission was successuly
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		// An error occured.
		// Did not submit successfully
		return false;
	}

	/**
	 * Takes a list of keys and a single value to match them for a multiple value query.
	 * 
	 * Example:
	 * Link  keys = (www.something.com/ef.jpg, www.where.au/picture.png)
	 * BugID value = 42346
	 * 
	 * returns: ('www.something.com/ef.jpg', '42346'), ('www.where.au/picture.png', '42346')
	 * 
	 * 
	 * @param keys Keys to match with the value
	 * @param value To be matches to each of the keys
	 * @return String combining each of the keys to the value as a entry.
	 */
	private static String listToMultipleValues(Collection<String> keys, String value){
		if( keys == null || keys.isEmpty() ){
			return null;
		}
		
		
		// Finished multiple string value
		String string = "";
		
		
		// Step through each of the strings
		int i = 0;
		for(String s : keys){
			

			// Wrap the key with the value
			string += "( '" + s + "', '" + value + "')";
			
			// If we have another key in the list
			// add a comma so we can combine them
			if( (++i) < keys.size() ){
				string += ",";
			}
		}
		
		return string;
	}

	/**
	 * Delete the given bug bug from the database
	 * @param bugToDelete Bug that is in the database and needs to be deleted
	 * @return True if deleting was successfuly
	 */
	public static boolean delete(BugItem bugToDelete) {
		
		// Make sure we are connected first
		if( !isConnected() ){
			return false;
		}
		else if( bugToDelete == null ){
			
			// Can not delete a null bug
			return false;
		}

		// Get currentTime
		String currentTime = retrieveCurrentTime();
		
		System.out.println("Bug ID: " + bugToDelete.ID);
		boolean deleted = submitQuery("DELETE FROM bugs where bugid = " + bugToDelete.ID);
		if( !deleted ){
			System.out.println("Did not delete bug!");
			return false;
		}	
		
		// Report update
		updateBugsLastReportedDate(bugToDelete.ID, currentTime);
		
		// Successfully deleted
		return true;
	}


	/**
	 * Updates the bug that is already in the database with an improved version of the bug
	 * @param oldBug Bug that has been pulled from the database
	 * @param newBug Newly created from from the EditReport Dialog
	 * @return True if updated correctly. Otherwise False
	 */
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
			queries.add("INSERT INTO versions (`version`, `stage`, `bugid`) VALUES ('" + newBug.version.version + "', '" + newBug.version.stage + "', '" + ID +"');");
		}
		else if( oldBug != null && !oldBug.version.equals(newBug.version) ){
			queries.add("UPDATE versions SET version = '" + newBug.version.version + "', stage = '"+newBug.version.stage+"' WHERE BugId = " + ID + "; ");
		}

		// Tags have changed
		List<String> tagQueries = getModifiedTagQueries(oldBug,newBug);
		if( !tagQueries.isEmpty() ){
			for(String s : tagQueries ){
				queries.add(s);
			}
		}

		// The person editing is not the same person that created the bug
		List<String> screenshotQueries = getModifiedScreenshotQueries(oldBug, newBug);
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
		updateBugsLastReportedDate(newBug.ID, currentTime);

		
		// Before we update. Make sure we are connected
		connect();
		
		if( !isConnected() ){
			return false;
		}
			
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
			
			// Updated without error
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		// An error occured
		return false;
	}

	public static List<Comment> getComments(BugItem bug){
		return null;
	}
	
	/**
	 * Creates queries that require editing the database related to the Tags of the oldBug and the newBug
	 * @param oldBug Oldbug that was pulled from the database
	 * @param newBug Newly created bug that was created in the EditReport dialog
	 * @return List of queries required to update the Tags for the new bug
	 */
	private static List<String> getModifiedTagQueries(BugItem oldBug, BugItem newBug) {
		
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
				queries.add("INSERT INTO `tags` (`tag`, `bugid`) VALUES ('"+tag.message+"', '"+newBug.ID+"');");
			}
		}
		
		
		return queries;
	}
	
	/**
	 * Gets all the queries in relation to the screenshots that need to be added or deleted by comparing the two bugs.
	 * @param oldBug Oldbug that was pulled from the database
	 * @param newBug Newly created bug that was created in the EditReport dialog
	 * @return Queries that require deleting and updating the database to sync up to the newBugs screenshots.
	 */
	private static List<String> getModifiedScreenshotQueries(BugItem oldBug, BugItem newBug) {
		
		List<String> queries = new ArrayList<String>();
		
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
		
		// If we have a single ' in the message. Add another so it doesn't break the query. 
		String quotes = message.replaceAll("'", "''");
		
		return quotes;
	}

	/**
	 * Records an update for the given bug at the the current time so people can pull the change
	 * @param newBug What bug we updated
	 * @param currentTime When was this update performed
	 */
	private static void updateBugsLastReportedDate(int bugid, String currentTime) {
		
		// Attempt to update
		String updateBug = "UPDATE `bugdates` SET `lastupdated` = '"+currentTime+"', `whoupdated` = '" + username + "' WHERE `bugid` = '"+bugid+"';";

		try {
		
			// Attempt to update the database with the new bug
			Statement st = con.createStatement();
			st.executeUpdate(updateBug);
			int r = st.getUpdateCount();

			// Check we updated at least 1 row
			if( r == 0 ){
				
				// Did not update anything. So insert a new update
				String insertUpdate = "INSERT INTO `bugdates`(`bugid`, `lastupdated`, `whoupdated`) VALUES ('"+bugid+"', '"+currentTime+"', '" + username + "');";     
				
				Statement st2 = con.createStatement();
				st2.executeUpdate(insertUpdate);
			}
			else if( r < 0 ){
				PopupError.show("Error Recording Last update", "Could not record update for bug " + bugid + " with error " + r);
			}
		

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the date of the last performed update that changed the database
	 * @return The lastUpdate date and time
	 */
	public static String getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Get the server that we are connected/connecting to
	 * @return server that the SQLData is using for connections 
	 */
	public static String getServer() {
		return server;
	}
	
	/**
	 * Get the database that we are pulling bugs from
	 * @return Database that we are connected to 
	 */
	public static String getDatabase() {
		return database;
	}

	public static boolean submitComment(String text, BugItem selectedBug) {
		
		String currentTime = retrieveCurrentTime();
		
		String query = "INSERT INTO `comments`(`comment`, `whocommented`, `bugid`) VALUES ('"+fix(text)+"','"+username+"','"+selectedBug.ID+"')";
		boolean commentedSubmitted = submitQuery(query);
		if( commentedSubmitted ){
			updateBugsLastReportedDate(selectedBug.ID, currentTime);
		}
		
		return commentedSubmitted;		
	}

	/**
	 * Deletes a the given comment from the database 
	 * @param commentToDelete What to delete
	 * @return True if successfully deleted 
	 */
	public static boolean delete(Comment commentToDelete) {
		// Make sure we are connected first
		if( !isConnected() ){
			return false;
		}
		else if( commentToDelete == null ){
			
			// Can not delete a null bug
			return false;
		}

		// Get currentTime
		String currentTime = retrieveCurrentTime();
		
		System.out.println("Comment ID: " + commentToDelete.id);
		boolean deleted = submitQuery("DELETE FROM comments where commentid = " + commentToDelete.id);
		if( !deleted ){
			System.out.println("Did not delete bug!");
			return false;
		}	
		
		// Report update
		updateBugsLastReportedDate(commentToDelete.bugid, currentTime);
		
		// Successfully deleted
		return true;
	}

	/**
	 * Change the comment in the database and alert evberyone of it's change
	 * @param selectedComment What we originally had in the database
	 * @param newComment Comment to change the old Comment to
	 * @return True if updated correctly
	 */
	public static boolean update(Comment selectedComment, Comment newComment) {
		
		String currentTime = retrieveCurrentTime();
		
		String query = "UPDATE `comments` set `comment`= '" + newComment.message + "' WHERE `commentid`='" + selectedComment.id + "'; ";
		boolean submitted = submitQuery(query);
		if( submitted ){
			updateBugsLastReportedDate(selectedComment.bugid, currentTime);
		}
		
		return submitted;
	}
}

























