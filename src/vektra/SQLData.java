package vektra;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class SQLData {
	private static Connection con;
		
	private static String username = "-";
	private static String password = "-";
	private static String server = "mathparser.com";
	private static String table = "-";

	
//	public static ObservableList<BugItem> getDataUpdated(){
//		connect();
//		
//		if( con == null ){
//			return null;
//		}
//		
//		
//		try {
//
//			Statement st = con.createStatement();			
//			ResultSet result = st.executeQuery("SELECT b.bugid, date, message,poster, priority, status, tag, link FROM "
//					+ "bugs b, priorities p, statuses s , tags t, screenshots h WHERE "
//					+ "b.bugid = p.bugid AND b.bugid = s.bugid AND b.bugid = t.bugid AND b.bugid = h.bugid;");
//			
//			ObservableList<BugItem> bugs = FXCollections.observableArrayList();
//			processResults(result, bugs);
//			return bugs;
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		
//		return null;
//	}
	
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
			// TODO Auto-generated catch block
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
			
			Statement st = con.createStatement();
			/*ResultSet result = st.executeQuery("SELECT * FROM (bugs LEFT JOIN priorities ON bugs.bugid=priorities.bugid a "
												+ "LEFT JOIN statuses ON a.bugid=statuses.bugid b "
												+ "LEFT JOIN tags ON b.bugid=tags.bugid c "
												+ "LEFT JOIN screenshots ON c.bugid=screenshots.bugid d);");*/
			
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
				//System.err.println(e.getMessage());
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
			
		String bugcommand = "INSERT INTO bugs (`poster`, `message`) VALUES ('" + username + "', '" + bug.getMessage() + "')";
		boolean bugcommandConfirmation = submitQuery(bugcommand);
		if( !bugcommandConfirmation ){
			System.out.println("Did not submit bug!");
			return false;
		}
		
		
		// Get the bug we just added		
		int bugid = getSubmittedBugID(username, bug.getMessage() );
		String prioritycommand = "INSERT INTO priorities (`priority`, `bugid`) VALUES ('" + bug.priority + "', '" + bugid + "')";
		boolean prioritycommandConfirmation = submitQuery(prioritycommand);
		if( !prioritycommandConfirmation ){
			System.out.println("Did not submit Priority!");
			return false;
		}	
		
		String statuscommand = "INSERT INTO statuses (`status`, `bugid`) VALUES ('"+bug.getStatus()+"', '" + bugid + "')";
		boolean statusstatusConfirmation = submitQuery(statuscommand);
		if( !statusstatusConfirmation ){
			System.out.println("Did not submit Status!");
			return false;
		}
		
		String screenshotcommand;
		if( bug.imageMap != null && !bug.imageMap.isEmpty() ){
			screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES " + listToMultipleValues(bug.imageMap.keySet(), bugid);
		}
		else{
			screenshotcommand = "INSERT INTO screenshots (`link`, `bugid`) VALUES ('" + "NULL" + "', '" + bugid + "');";
		}
		boolean screenshotcommandConfirmation = submitQuery(screenshotcommand);
		if( !screenshotcommandConfirmation ){
			System.out.println("Did not submit Screenshots!");
			return false;
		}
		
		String tagcommand = "INSERT INTO tags (`tag`, `bugid`) VALUES " + listToMultipleValues(bug.getTags(), bugid);
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

	private static String listToMultipleValues(Set<String> set, int bugID){
		if( set == null || set.isEmpty() ){
			return null;
		}
		
		String string = "";
		
		
		int i = 0;
		for(String s : set){
			string += "( '" + s + "', '" + bugID + "')";
			if( (++i) < set.size() ){
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


	public static boolean update(BugItem bug) {
		// TODO Auto-generated method stub
		return false;
	}

}












