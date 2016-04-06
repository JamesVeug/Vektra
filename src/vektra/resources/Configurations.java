package vektra.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import vektra.dialogs.PopupError;
import vektra.dialogs.PopupWarning;

public class Configurations {

	// DEFAULT NEVER CHANGE
	private static final String CONFIG_DIRECTORY = System.getenv("APPDATA") + "/VektraBugReporter/";
	private static final String CONFIG_FILENAME = "config.cfg";
	
	// CHANGE
	private static final String CONFIG_RESOURCE_DIRECTORY = "RESOURCE_DIRECTORY";
	private static final String CONFIG_AUTOREFRESH_ENABLED = "AUTOREFRESH_ENABLED";
	private static final String CONFIG_AUTOREFRESH_INTERVAL = "AUTOREFRESH_INTERVAL";
	private static final String CONFIG_AUTOREFRESH_FULL = "AUTOREFRESH_FULL";
	
	
	public static final Map<String, String> FILE_KEY_LIST = new HashMap<String, String>(){
		private static final long serialVersionUID = -1637211318756745886L;
	{
		put(CONFIG_RESOURCE_DIRECTORY, CONFIG_DIRECTORY);
		put(CONFIG_AUTOREFRESH_ENABLED, "TRUE");
		put(CONFIG_AUTOREFRESH_INTERVAL, "10000");
		put(CONFIG_AUTOREFRESH_FULL, "FALSE");
	}};
	
	private static Map<String,String> values = new HashMap<String,String>();


	/**
	 * Loads the current filter options saved on the computer
	 */
	private static void loadFilterOptions(){
		System.out.println("Loading Options...");
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
					values.remove(key);
				}
				else{
					loadedKeys.remove(key);
					values.put(key, value); 
//					System.out.println("Putting " + key + " , " + value);
				}
			}
			scan.close();
			
			// If we don't have all the keywords in the file.
			// Resave the file with our newly updated filter
			if( !loadedKeys.isEmpty() ){
				for(String key : loadedKeys){
					System.out.println("Missing: " + key);
					values.put(key, FILE_KEY_LIST.get(key));
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
			for(String k : values.keySet()){
				print.println(k.trim() + " " + values.get(k).trim());
			}
			
			print.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Finished saving...");
	}

	private static String getDirectory() {
		return CONFIG_DIRECTORY + CONFIG_FILENAME;
	}
	
	public static String getImageDirectory(){
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		String directory = values.get(CONFIG_RESOURCE_DIRECTORY);
		return directory;
	}

	public static void setImageDirectory(String text) {
		values.put(CONFIG_RESOURCE_DIRECTORY, text);
	}

	public static void setAutoRefresh(boolean selected) {
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		values.put(CONFIG_AUTOREFRESH_ENABLED, String.valueOf(selected));
	}

	public static void setAutoRefreshInterval(int interval) {
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		values.put(CONFIG_AUTOREFRESH_INTERVAL, String.valueOf(interval));
	}

	public static void setFullAutoRefresh(boolean selected) {
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		values.put(CONFIG_AUTOREFRESH_FULL, String.valueOf(selected));
	}
	
	public static String getFullAutoRefresh(){
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		String directory = values.get(CONFIG_AUTOREFRESH_FULL);
		return directory;
	}
	
	public static String getAutoRefresh(){
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		String directory = values.get(CONFIG_AUTOREFRESH_ENABLED);
		return directory;
	}
	
	public static String getAutoRefreshInterval(){
		if( values.isEmpty() ){
			loadFilterOptions();
		}
		
		String directory = values.get(CONFIG_AUTOREFRESH_INTERVAL);
		return directory;
	}
}











