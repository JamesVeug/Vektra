package vektra;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Each Bug requires a priority to see how important this bug is in relation to being solved.
 * The priority class records each of the possible Objects and returns them when .get(..) is called.
 * @author James
 *
 */
public class Priority {

	// Null object
	public static final Priority NULL = new Priority("NULL", Color.BLACK);
	
	// Possible Priorities
	public static final Priority LOW = new Priority("LOW", Color.YELLOW);
	public static final Priority MEDIUM = new Priority("MEDIUM", Color.ORANGE);
	public static final Priority HIGH = new Priority("HIGH", Color.RED);
	
	// List of Priorities
	public static final List<Priority> priorityList = new ArrayList<Priority>(); 
	static{
		priorityList.add(LOW);
		priorityList.add(MEDIUM);
		priorityList.add(HIGH);
	}

	// Color of Priority
	public final Color color;
	
	// String representation of the Priority 
	public final String label; 
	
	/**
	 * Constructor for the set objects.
	 * Assigns string representation to be visdually displayed. And records a color to be used.
	 * @param string Visual text to represent the Priority
	 * @param color Color which shows how important it is compared to other priorities
	 */
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

	/**
	 * Matches the given string against the possible Priorities and returns a related set object.
	 * If no matches are met. The NULL Priority object will be returned.
	 * @param priority
	 * @return Priority object that meets the parameter. Otherwise the NULL Priority.
	 */
	public static Priority get(String priority) {
		for(Priority p : priorityList){
			if( p.label.equals(priority) ){
				return p;
			}
		}
		return NULL;
	}
}
