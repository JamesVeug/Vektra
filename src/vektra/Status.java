package vektra;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Every bug needs to be in a certain status which represents if it is being fixed or not.
 * The Status class records each of the possible Statuses as objects and never recreates new ones.
 * @author James
 *
 */
public class Status {

	// Null object
	public static final Status NULL = new Status("NULL");
	
	// Possible statuses for the Bug to be in
	public static final Status WIP = new Status("WIP");
	public static final Status FIXED = new Status("Fixed");
	public static final Status PENDING = new Status("Pending");
	public static final List<Status> statusList = new ArrayList<Status>(); 
	static{
		statusList.add(PENDING);
		statusList.add(WIP);
		statusList.add(FIXED);
	}

	// Visual representation of the Status
	public final String label; 
	
	/**
	 * Constructor for the set Objects that assign the visual resentation of the status
	 * @param string What to be displayed as the Statys
	 */
	private Status(String string) {
		this.label = string;
	}

	
	@Override
	public String toString(){
		return label;
	}

	/**
	 * Gets the appropriate Status for the given string.
	 * If no matches are met. Then NULL will be returned.
	 * @param status What to search for to get the correct Status
	 * @return final Status object or the NULL Status
	 */
	public static Status get(String status) {
		for(Status p : statusList){
			if( p.label.equalsIgnoreCase(status) ){
				return p;
			}
		}
		return NULL;
	}
}
