package vektra.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vektra.BugItem;
import vektra.Priority;
import vektra.Status;
import vektra.dialogs.PopupError;
import vektra.dialogs.PopupWarning;

public class FilterConfiguration {

	// Statuses
	private static final String FILE_FILTER_STATUS_FIXED = "FILTER_STATUS_FIXED";
	private static final String FILE_FILTER_STATUS_WIP = "FILTER_STATUS_WIP";
	private static final String FILE_FILTER_STATUS_PENDING = "FILTER_STATUS_PENDING";
	private static final String FILE_FILTER_STATUS_NULL = "FILTER_STATUS_NULL";
	
	// Priorty
	private static final String FILE_FILTER_PRIORITY_NULL = "FILTER_PRIORITY_NULL";
	private static final String FILE_FILTER_PRIORITY_LOW = "FILTER_PRIORITY_LOW";
	private static final String FILE_FILTER_PRIORITY_MEDIUM = "FILTER_PRIORITY_MEDIUM";
	private static final String FILE_FILTER_PRIORITY_HIGH = "FILTER_PRIORITY_HIGH";
	
	// Bugs
	private static final String FILE_FILTER_BUG_WHO = "FILTER_BUGS_WHO";
	
	public static final Map<String, String> FILE_KEY_LIST = new HashMap<String, String>(){
		private static final long serialVersionUID = -1637211318756745886L;
	{
		// STATUS
		put(FILE_FILTER_STATUS_FIXED, "FALSE");
		put(FILE_FILTER_STATUS_WIP, "FALSE");
		put(FILE_FILTER_STATUS_PENDING, "FALSE");
		put(FILE_FILTER_STATUS_NULL, "FALSE");
		
		// PRIORITY
		put(FILE_FILTER_PRIORITY_NULL, "FALSE");
		put(FILE_FILTER_PRIORITY_LOW, "FALSE");
		put(FILE_FILTER_PRIORITY_MEDIUM, "FALSE");
		put(FILE_FILTER_PRIORITY_HIGH, "FALSE");
		
		// BUG
		put(FILE_FILTER_BUG_WHO, "-");
	}};
	
	private static Map<String,String> filters = new HashMap<String,String>();
	
	public static ObservableList<BugItem> filter(ObservableList<BugItem> bugs){
		ObservableList<BugItem> filteredList = FXCollections.observableArrayList();
		if( bugs == null || bugs.isEmpty() ){
			return filteredList;
		}
		
		loadFilterOptions();
		
		// Sort them accordingly.
		for(BugItem bug : bugs ){
			if( !shouldFilter(bug) ){
				filteredList.add(bug);
			}
		}
		
		saveFilterOptions();
		
		// Return filtered List
		return filteredList;
	}

	/**
	 * Check the given bug to see if it should be filtered or not.
	 * If the bug should not be added to the next array. This will return falses
	 * @param bug What should be checked for filtering
	 * @return True if should be filtered. Otherwise False
	 */
	private static boolean shouldFilter(BugItem bug) {
		
		try{
			// STATUSES
			if( (bug.status == Status.FIXED) && Boolean.parseBoolean(filters.get(FILE_FILTER_STATUS_FIXED)) ){
				return true;
			}
			else if( (bug.status == Status.NULL) && Boolean.parseBoolean(filters.get(FILE_FILTER_STATUS_NULL)) ){
				return true;
			}
			else if( (bug.status == Status.PENDING) && Boolean.parseBoolean(filters.get(FILE_FILTER_STATUS_PENDING)) ){
				return true;
			}
			else if( (bug.status == Status.WIP) && Boolean.parseBoolean(filters.get(FILE_FILTER_STATUS_WIP)) ){
				return true;
			}
			
			// PRIORITIES
			else if( (bug.priority == Priority.NULL) && Boolean.parseBoolean(filters.get(FILE_FILTER_PRIORITY_NULL)) ){
				return true;
			}
			else if( (bug.priority == Priority.LOW) && Boolean.parseBoolean(filters.get(FILE_FILTER_PRIORITY_LOW)) ){
				return true;
			}
			else if( (bug.priority == Priority.MEDIUM) && Boolean.parseBoolean(filters.get(FILE_FILTER_PRIORITY_MEDIUM)) ){
				return true;
			}
			else if( (bug.priority == Priority.HIGH) && Boolean.parseBoolean(filters.get(FILE_FILTER_PRIORITY_HIGH)) ){
				return true;
			}
			
			// BUGS
			else if( getFilteredNames().contains(bug.who) ){
				return true;
			}
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
		
		// Do not filter
		return false;
	}
	
	private static Set<String> getFilteredNames() {
		Set<String> names = new HashSet<String>();
		
		System.out.println("Fitlering Names: ");
		String nameString = filters.get(FILE_FILTER_BUG_WHO);
		if( !nameString.isEmpty() ){
			String[] splitNames = nameString.split(" ");
			for(String n : splitNames){
				System.out.print(n.trim() + ", ");
				names.add(n.trim());
			}
		}
		System.out.println();
		return names;
	}

	/**
	 * Loads the current filter options saved on the computer
	 */
	private static void loadFilterOptions(){
		System.out.println("Filtering...");
		File file = new File(getDirectory());
		
		// Check that it creates directory
		if( !file.exists() ){
			saveFilterOptions();
		}
		
		// Keep a list of all the keys we have stored as default.
		// And remove each key as we load the file, so we know what we do not have in the loaded file, but we do in the program.
		Set<String> loadedKeys = new HashSet<String>(FILE_KEY_LIST.keySet());
		
		try {
			Scanner scan = new Scanner(file);
			while(scan.hasNext()){
				String key = scan.next();
				System.out.println("Key " + key);
				
				if( !scan.hasNext() ){
					PopupError.show("Loading Filter Configurations", "Could not find value for key " + key);
					continue;
				}
				
				// Check value is valid
				String value = scan.nextLine().trim();
				
//				System.out.println("Found: " + key + ", " + value);
				if(!loadedKeys.contains(key)){
					PopupWarning.show("Loading Filter Configurations", "Unknown key:", key);
					filters.remove(key);
				}
				else{
					loadedKeys.remove(key);
					filters.put(key, value); 
//					System.out.println("Putting " + key + " , " + value);
				}
			}
			scan.close();
			
			// If we don't have all the keywords in the file.
			// Resave the file with our newly updated filter
			if( !loadedKeys.isEmpty() ){
				for(String key : loadedKeys){
					System.out.println("Missing: " + key);
					filters.put(key, FILE_KEY_LIST.get(key));
				}
				saveFilterOptions();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFilterOptions() {
		System.out.println("Saving file");
		File file = new File(getDirectory());
		
		try {
			PrintStream print = new PrintStream(file);
			
			// Create new Config file
			for(String k : filters.keySet()){
				print.println(k.trim() + " " + filters.get(k).trim());
			}
			
			print.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Finished saving...");
	}

	private static String getDirectory() {
		return "filter.cfg";
	}
	
	public static Map<Status,Boolean> getStatusSettings(){
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		Map<Status,Boolean> statuses = new HashMap<Status,Boolean>();
		for(Status e : Status.statusList){
			
			// Status = WIP / Pending... etc
			// Convert to FILTER_WIP
			String name = "FILTER_STATUS_"+e.label.toUpperCase();
			if( filters.containsKey(name)){
				statuses.put(e, Boolean.parseBoolean(filters.get(name)));
			}
		}
		
		
		return statuses;
	}

	public static Map<Priority, Boolean> getPrioritySettings() {
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		Map<Priority,Boolean> statuses = new HashMap<Priority,Boolean>();
		for(Priority e : Priority.priorityList){
			
			// Status = WIP / Pending... etc
			// Convert to FILTER_WIP
			String name = "FILTER_PRIORITY_"+e.label.toUpperCase();
			if( filters.containsKey(name)){
				statuses.put(e, Boolean.parseBoolean(filters.get(name)));
			}
		}
		
		
		return statuses;
	}

	public static void setStatusSettings(Map<Status, Boolean> statusMap) {
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		for(Entry<Status,Boolean> e : statusMap.entrySet()){
			
			// Status = WIP / Pending... etc
			// Convert to FILTER_WIP
			String name = "FILTER_STATUS_"+e.getKey().label.toUpperCase();
			if( filters.containsKey(name)){
				filters.put(name, String.valueOf(e.getValue()));
			}
		}
	}
	
	public static void setPrioritySettings(Map<Priority, Boolean> statusMap) {
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		for(Entry<Priority,Boolean> e : statusMap.entrySet()){
			
			// Status = WIP / Pending... etc
			// Convert to FILTER_WIP
			String name = "FILTER_PRIORITY_"+e.getKey().label.toUpperCase();
			if( filters.containsKey(name)){
				filters.put(name, String.valueOf(e.getValue()));
			}
		}
	}
	
	public static String getWhoPostedSettings(){
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		String who = filters.get(FILE_FILTER_BUG_WHO);
		if( who.equals(FILE_KEY_LIST.get(FILE_FILTER_BUG_WHO)) ){
			return "";
		}
		
		return who;
	}

	public static void setBugSettings(String text) {
		if( filters.isEmpty() ){
			loadFilterOptions();
		}
		
		// Save with default value
		if( text.isEmpty() ){
			text = FILE_KEY_LIST.get(FILE_FILTER_BUG_WHO);
		}
		
		// Add
		filters.put(FILE_FILTER_BUG_WHO, text);
	}
}











