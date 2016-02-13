package vektra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;

/**
 * Contains all information about a single bug that has been reported, or will be reported.
 * All fields are public due to never requiring being changed.
 * @author James
 *
 */
public class BugItem {

	// Final
	public int ID;
	public String priority;
	public String status;
	public String who;
	public String message;
	public String date;
	public String version;
	
	// Will be modified
	public Set<Tag> tags;
	public Map<String, BugImage> imageMap;
	public String whoUpdated;
	public String lastUpdate;
	
	/**
	 * Sets up the bug with the given starter values
	 * @param iD bugid that is stored on the database to distinguish between the many kinds of bugs.
	 * @param tags Determines what kind of bug this is related towards.
	 * @param priority Is this a low,medium or high prioritized bug?
	 * @param status Which state this bug is in in related to being solved.
	 * @param who Who posted this bug
	 * @param message Description of what the bug is all about.
	 * @param date When was this bug created?
	 * @param images Images mapped from their link to the image which visually representing the bug
	 */
	public BugItem(int iD, Set<Tag> tags, String priority, String status,
			String who, String message, String date, String version, Map<String,BugImage> images){

		ID = iD;
		this.tags = tags;
		this.priority = priority;
		this.status = status;
		this.who = who;
		this.message = message;
		this.date = date;
		this.version = version;
		
		this.imageMap = new HashMap<String, BugImage>();
		this.imageMap.putAll(images);
	}
	
	/**
	 * Constructor that takes a group of images and tags
	 * @param iD bugid that is stored on the database to distinguish between the many kinds of bugs.
	 * @param tags Determines what kind of bug this is related towards.
	 * @param priority Is this a low,medium or high prioritized bug?
	 * @param status Which state this bug is in in related to being solved.
	 * @param who Who posted this bug
	 * @param message Description of what the bug is all about.
	 * @param date When was this bug created?
	 * @param link Link for the image to be downloaded from later. 
	 * @param image Image that visually represents the bug
	 */
	public BugItem(int iD, Tag tag, String priority, String status,
			String who, String message, String date, String version, String link, BugImage image) {
		this(iD, toTagSet(tag), priority, status, who, message, date, version, toMap(link,image));
	}

	/**
	 * Converts the given string and image to a single map 
	 * @param link Key
	 * @param image Value
	 * @return new HashMap containing a single entry
	 */
	private static Map<String, BugImage> toMap(String link, BugImage image) {
		Map<String, BugImage> images = new HashMap<String, BugImage>();
		if( image != null ){
			images.put(link, image);
		}
		return images;
	}

	/**
	 * Create a new set and adds the given string to it
	 * @param tag What to add to the set
	 * @return new Set with the tag as its only element.
	 */
	private static Set<Tag> toTagSet(Tag tag) {
		Set<Tag> list = new HashSet<Tag>();
		list.add(tag);
		return list;
	}

	/**
	 * Gets the images related to this bug.
	 * @return Collection of images
	 */
	public Collection<BugImage> getImages() {
		return imageMap.values();
	}

	/**
	 * Adds the given string as a tag to this bug.
	 * @param tag
	 */
	public void addTag(Tag tag) {
		tags.add(tag);		
	}
	
	public List<String> getTagMessages(){
		List<String> messages = new ArrayList<String>();
		for(Tag t : tags){
			messages.add(t.message);
		}
		return messages;
	}

	/**
	 * Adds a given screenshot to the bug along with it's link.
	 * @param link Link to the screenshot to be downloaded
	 * @param screenshot Image of the screenshot which was downloaded from the link.
	 */
	public void addScreenshot(String link, BugImage screenshot) {
		imageMap.put(link, screenshot);
	}
	
	@Override
	public String toString(){
		
		String links = "-";
		if( imageMap != null && !imageMap.isEmpty() ){
			links = "";
			for(BugImage i : imageMap.values()){
				links += "\n\t\t" + i;
			}
		}
		
		String combinedTags = "-";
		if( tags != null && !tags.isEmpty() ){
			combinedTags = "";
			for(Tag i : tags){
				combinedTags += i.message + "  ";
			}
		}
		
		return String.valueOf(ID) + "\n"
				+ "\tPriority: " + priority + "\n"
				+ "\tStatus: " + status + "\n"
				+ "\tVersion: " + version + "\n"
				+ "\tWho: " + who + "\n"
				+ "\tDate: " + date + "\n"
				+ "\tTags: " + combinedTags + "\n"
				+ "\tLinks: " + links + "\n"
				+ "\tMessage: " + message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
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
		if (!(obj instanceof BugItem))
			return false;
		BugItem other = (BugItem) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	/**
	 * Gets ID of the BugItem
	 * Required Method for the table in the Vektra class to be used
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Gets Status of the BugItem
	 * Required Method for the table in the Vektra class to be used
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

}
