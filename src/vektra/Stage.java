package vektra;

import java.util.ArrayList;
import java.util.List;

public class Stage {
	public static final Stage NULL = new Stage("NULL");
	
	public static final Stage DEMO = new Stage("DEMO");
	public static final Stage PROLOGUE = new Stage("PROLOGUE");
	public static final Stage ALPHA = new Stage("ALPHA");
	public static final Stage BETA = new Stage("BETA");
	public static final Stage GOLD = new Stage("GOLD");
	public static final List<String> stageListStrings = new ArrayList<String>();
	public static final List<Stage> stageList = new ArrayList<Stage>(); 
	static{
		stageList.add(DEMO);
		stageList.add(PROLOGUE);
		stageList.add(ALPHA);
		stageList.add(BETA);
		stageList.add(GOLD);
		
		for(Stage s : stageList){
			stageListStrings.add(s.stage);
		}
	}

	private final String stage;
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
	
	public static Stage get(String priority) {
		for(Stage p : stageList){
			if( p.stage.equals(priority) ){
				return p;
			}
		}
		return NULL;
	}
	
	public static List<String> getStrings(){
		return stageListStrings;
	}
}