package vektra;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class Priority {

	// Null object
	public static final Priority NULL = new Priority("NULL", Color.BLACK);
	
	public static final Priority LOW = new Priority("LOW", Color.YELLOW);
	public static final Priority MEDIUM = new Priority("MEDIUM", Color.ORANGE);
	public static final Priority HIGH = new Priority("HIGH", Color.RED);
	public static final List<String> priorityListStrings = new ArrayList<String>();
	public static final List<Priority> priorityList = new ArrayList<Priority>(); 
	static{
		priorityList.add(LOW);
		priorityList.add(MEDIUM);
		priorityList.add(HIGH);
		
		for(Priority p : priorityList){
			priorityListStrings.add(p.label);
		}
	}


	public final Color color; 
	public final String label; 
	
	private Priority(String string, Color color) {
		this.label = string;
		this.color = color;
	}
	
	/**
	 * Get the color which should represent this priority 
	 * @return Color object related to this priority
	 */
	public Color getColor(){
		return color;
	}

	
	@Override
	public String toString(){
		return label;
	}

	public static Priority get(String priority) {
		for(Priority p : priorityList){
			if( p.label.equals(priority) ){
				return p;
			}
		}
		return NULL;
	}
	
	public static List<String> getStrings(){
		return priorityListStrings;
	}
}
