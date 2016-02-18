package vektra;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class Status {

	// Null object
	public static final Status NULL = new Status("NULL");
	
	public static final Status WIP = new Status("WIP");
	public static final Status FIXED = new Status("Fixed");
	public static final Status PENDING = new Status("Pending");
	public static final List<String> statusListStrings = new ArrayList<String>();
	public static final List<Status> statusList = new ArrayList<Status>(); 
	static{
		statusList.add(PENDING);
		statusList.add(WIP);
		statusList.add(FIXED);
		
		for(Status s : statusList){
			statusListStrings.add(s.label);
		}
	}


	public final String label; 
	
	private Status(String string) {
		this.label = string;
	}

	
	@Override
	public String toString(){
		return label;
	}

	public static Status get(String priority) {
		for(Status p : statusList){
			if( p.label.equalsIgnoreCase(priority) ){
				return p;
			}
		}
		return NULL;
	}
	
	public static List<String> getStrings(){
		return statusListStrings;
	}
}
