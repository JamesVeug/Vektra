package vektra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;

public class BugItem {

	public int ID;
	public Set<String> tags;
	public String priority;
	public String status;
	public String who;
	public String message;
	public String date;
	public Set<Image> images;
	public Map<String, Image> imageMap;
	
	/**
	 * Constructor that takes a group of images and tags
	 * @param iD
	 * @param tags
	 * @param priority
	 * @param status
	 * @param who
	 * @param message
	 * @param date
	 * @param images
	 */
	public BugItem(int iD, Set<String> tags, String priority, String status,
			String who, String message, String date, Map<String,Image> images) {
		super();
		initialize(iD, tags, priority, status, who, message, date, images);
	}
	
	/**
	 * Constructor that can take a single image
	 * @param iD
	 * @param tag
	 * @param priority
	 * @param status
	 * @param who
	 * @param message
	 * @param date
	 * @param link
	 * @param image
	 */
	public BugItem(int iD, String tag, String priority, String status,
			String who, String message, String date, String link, Image image) {
		super();
		
		// Create new Tag Collections
		Set<String> tags = new HashSet<String>();
		if( tag != null && !tag.isEmpty() ){
			tags.add(tag);
		}
		
		// Create new Image collection
		Map<String, Image> images = new HashMap<String, Image>();
		if( image != null ){
			images.put(link, image);
		}
		
		initialize(iD, tags, priority, status, who, message, date, images);
	}
	
	private void initialize(int iD, Set<String> tags, String priority, String status,
			String who, String message, String date, Map<String,Image> images){

		ID = iD;
		this.tags = tags;
		this.priority = priority;
		this.status = status;
		this.who = who;
		this.message = message;
		this.date = date;
		
		this.images = new HashSet<Image>();
		this.images.addAll(images.values());
		
		this.imageMap = new HashMap<String, Image>();
		this.imageMap.putAll(images);
	}
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}
	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the images
	 */
	public Set<Image> getImages() {
		return images;
	}
	public void addTag(String tag) {
		tags.add(tag);		
	}

	public void addScreenshot(String link, Image screenshot) {
		images.add(screenshot);
		imageMap.put(link, screenshot);
	}
	
	@Override
	public String toString(){
		
		String links = "-";
		if( images != null && !images.isEmpty() ){
			links = "";
			for(Image i : images){
				links += "\n\t\t" + i;
			}
		}
		
		String combinedTags = "-";
		if( tags != null && !tags.isEmpty() ){
			combinedTags = "";
			for(String i : tags){
				combinedTags += i + "  ";
			}
		}
		
		return String.valueOf(ID) + "\n"
				+ "\tPriority: " + priority + "\n"
				+ "\tStatus: " + status + "\n"
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
}
