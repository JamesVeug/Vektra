package vektra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains all information about a single bug that has been reported, or will be reported.
 * All fields are public due to never requiring being changed.
 * @author James
 *
 */
public class BugItem {

	// Final
	public int ID;
	public Priority priority;
	public Status status;
	public String who;
	public String message;
	public String date;
	public Version version;
	
	// Will be modified
	public Set<Tag> tags;
	public Map<String, BugImage> imageMap;
	public Set<Comment> comments;
	public String whoUpdated;
	public String lastUpdate;
	public boolean hasBeenUpdated = false;
	
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
	public BugItem(int iD, Set<Tag> tags, Priority priority, Status status,
			String who, String message, String date, Version version, Map<String,BugImage> images){

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
		
		comments = new HashSet<Comment>();
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
	public BugItem(int iD, Tag tag, Priority priority, Status status,
			String who, String message, String date, Version version, String link, BugImage image) {
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
	public Status getStatus() {
		return status;
	}

	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the whoUpdated
	 */
	public String getWhoUpdated() {
		return whoUpdated;
	}

	/**
	 * @return the lastUpdate
	 */
	public String getLastUpdate() {
		return lastUpdate;
	}

	public void addComment(Comment comment) {
		if( comment != null ){
			comments.add(comment);
		}
	}

	/**
	 * @return the comments
	 */
	public Set<Comment> getComments() {
		return comments;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int deepHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (hasBeenUpdated ? 1231 : 1237);
		result = prime * result + ((imageMap == null) ? 0 : imageMap.hashCode());
		result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((who == null) ? 0 : who.hashCode());
		result = prime * result + ((whoUpdated == null) ? 0 : whoUpdated.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean deepEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BugItem))
			return false;
		BugItem other = (BugItem) obj;
		if (ID != other.ID)
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (hasBeenUpdated != other.hasBeenUpdated)
			return false;
		if (imageMap == null) {
			if (other.imageMap != null)
				return false;
		} else if (!imageMap.equals(other.imageMap))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (who == null) {
			if (other.who != null)
				return false;
		} else if (!who.equals(other.who))
			return false;
		if (whoUpdated == null) {
			if (other.whoUpdated != null)
				return false;
		} else if (!whoUpdated.equals(other.whoUpdated))
			return false;
		return true;
	}

}
