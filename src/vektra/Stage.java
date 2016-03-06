package vektra;

import java.util.ArrayList;
import java.util.List;

/**
 * Class related to what stage the reporter but is at.
 * The Stage class stores the possible Stage objects so there are no duplicates.
 * @author James
 *
 */
public class Stage {
	
	// Set Object for when there are no matches for the Stage
	public static final Stage NULL = new Stage("NULL");
	
	// Possible stages for the game
	public static final Stage DEMO = new Stage("DEMO");
	public static final Stage PROLOGUE = new Stage("PROLOGUE");
	public static final Stage ALPHA = new Stage("ALPHA");
	public static final Stage BETA = new Stage("BETA");
	public static final Stage GOLD = new Stage("GOLD");
	public static final List<Stage> stageList = new ArrayList<Stage>(); 
	static{
		stageList.add(DEMO);
		stageList.add(PROLOGUE);
		stageList.add(ALPHA);
		stageList.add(BETA);
		stageList.add(GOLD);
	}

	/**
	 * String representation of the Stage
	 */
	private final String stage;
	
	/**
	 * Constructor to be created from the set objects
	 * Stores the given string and is used for display.
	 * @param string Representation of the stage.
	 */
	private Stage(String string) {
		this.stage = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return stage;
	}
	
	/**
	 * Gets the appropriate Stage object from our list of Stages and returns it.
	 * If there are no strings stages that match the given parameter. Then NULL will be returned
	 * @param stage Stage to look for amongst the set Stages.
	 * @return final Stage object or the Null Stage
	 */
	public static Stage get(String stage) {
		for(Stage p : stageList){
			if( p.stage.equalsIgnoreCase(stage) ){
				return p;
			}
		}
		return NULL;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Stage))
			return false;
		Stage other = (Stage) obj;
		if (stage == null) {
			if (other.stage != null)
				return false;
		} else if (!stage.equals(other.stage))
			return false;
		return true;
	}
}