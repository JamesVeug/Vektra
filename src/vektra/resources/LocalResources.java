package vektra.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import vektra.BugImage;
import vektra.BugItem;
import vektra.SQLData;

public class LocalResources {
	

	public static Map<Integer, String> screenshotIDToDirectory = new HashMap<Integer,String>();
	public static Map<Integer, Image> screenshotIDToImage = new HashMap<Integer,Image>();
	
	
	public static BugImage getImage(String link, int screenshotid){
		
		// Try and get it from our local storage
		BugImage localImage = getLocalImage(link, -1, -1, screenshotid);
		if( localImage != null ){
			return localImage;
		}
		
		// Don't have it stored locally
		return null;
	}

	public static BugImage getImage(String link, double w, double h, int screenshotid) {
		
		// Try and get it from our local storage
		BugImage localImage = getLocalImage(link, w, h, screenshotid);
		if( localImage != null ){
			return localImage;
		}
		
		// Don't have it stored locally
		return null;
	}

	/**
	 * Try and load the file locally
	 * @param link
	 * @param w
	 * @param h
	 * @param screenshotid 
	 * @return
	 */
	private static BugImage getLocalImage(String link, double w, double h, int screenshotid) {		

		Image imageViaLink = screenshotIDToImage.get(screenshotid);
		if( imageViaLink != null ){
			System.out.println("Via link");
			
			if( w == -1 ){
				w = imageViaLink.getWidth();
			}
			
			if( h == -1 ){
				h = imageViaLink.getHeight();
			}
			
			System.out.println("USING LOCAL IMAGE!");
			return new BugImage(imageViaLink, w,h, link);
		}
		
		// Could not find it!
		System.out.println("Could not find local image '" + screenshotid + "'");
		return null;
	}
	
	public static void synchronizeLocalImages(ObservableList<BugItem> databaseData){
		System.out.println("Syncing " + databaseData.size());
		
		
		List<BugImage> databaseImages = new ArrayList<BugImage>();
		
		// Sort data by what we have and do not have
		for(BugItem bug : databaseData){
			
			for( BugImage image : bug.imageMap.values() ){
				databaseImages.add(image);
				int screenshotId = image.screenshotID;
				
				if( !screenshotIDToImage.containsKey(screenshotId)){
					addImage(image);
				}				
			}
		}
		
		System.out.println("Database Images: " + databaseImages.size());
		for(BugImage i : databaseImages){
			System.out.println("\t"+i.screenshotID);
		}
		
		// Check if the images off the computer match the images on the database
		List<BugImage> computerImages = getImagesOffcomputer();
		for(BugImage computerImage : computerImages){
			if( !databaseImages.contains(computerImage) ){
				
				// Database does not have this image
				// Delete it off computer
				boolean deleted = removeImageViaID(computerImage.screenshotID); 
				if( deleted ){
					System.err.println("Deleted image " + computerImage.link);
					
				}
				else{
					System.err.println("Could not deleted image " + computerImage.link);					
				}
			}
		}

		System.out.println("Finished Syncing");

	}
	
	private static void addImage(BugImage image) {
		String directory = downloadToComputer(image);
		if( directory != null ){
			screenshotIDToImage.put(image.screenshotID, image.getImage());
			screenshotIDToDirectory.put(image.screenshotID, directory);
		}
		else{
			System.out.println("Could not download image to directory! " + image.screenshotID);
		}
	}

	private static List<BugImage> getImagesOffcomputer() {
		File imagesDirectory = new File(getImageDirectory());
		
		// Check that it creates directory
		if( !imagesDirectory.exists() ){
			imagesDirectory.mkdirs();
		}
		
		List<BugImage> images = new ArrayList<BugImage>();
		for( File file : imagesDirectory.listFiles() ){
			
			String ID = file.getName().substring(0,file.getName().indexOf("."));
			
			try{
				String path = file.getPath();
				BufferedImage buffimage = ImageIO.read(file);
				WritableImage image = null;
				image = SwingFXUtils.toFXImage(buffimage, image);
				
				if( image != null ){
					BugImage bugImage = new BugImage(new ImageView(image), path);
					bugImage.screenshotID = Integer.parseInt(ID);
					images.add(bugImage);
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
				
		return images;
	}

	/**
	 * 
	 * @param image to download onto computer
	 * @return filename with directory
	 */
	private static String downloadToComputer(BugImage image){
		String link = image.link;
		System.out.println("UPLOADING TO COMPUTER: " + link);
		
		String name = image.screenshotID + link.substring(link.lastIndexOf("."));
		if( name.contains("?") ){
			name = name.substring(0,name.indexOf("?"));
		}
		
		String ext = name.substring(name.lastIndexOf(".")+1);			
		String filename = image.screenshotID + "." + ext;
		
		System.out.println("ext: " + ext);
		System.out.println("File: " + filename);
		
		File directory = new File(getImageDirectory());
		if( !directory.exists() ){
			System.out.println("Directory does not exist. Creating directory!");
			directory.mkdirs();
		}
		
		String filepath = getImageDirectory() + filename;
		File file = new File(filepath);
		if( file.exists() ){
			System.err.println("File already exists. Not downloading to computer (" + getImageDirectory() + filename + ")");
			return filepath;
		}
		
		BufferedImage buffimage = toBufferedImage(image.getImage());
		try {
		   ImageIO.write(buffimage, ext, file);
		   return filepath;
		} catch(IOException e) {
			System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
		}
		return null;
	}
	
	public static void removeImagesViaID(List<Integer> ids){
		System.out.println("Removing local images (" + ids.size() + ")");
		
		for(Integer id : ids){
			removeImageViaID(id);
		}
		
	}
	
	public static boolean removeImageViaID(Integer id){
		System.out.println("Deleting Image ID:" + id);
		
		// delete from directory
		String filename = screenshotIDToDirectory.get(id);
		boolean deleted = deleteFromComputer(filename);
		if( deleted ){
			System.out.println("\tDeleted '" + filename + "'");
			
			// Remove from storage
			screenshotIDToDirectory.remove(id);
			screenshotIDToImage.remove(id);		
			return true;
		}
		else{
			System.out.println("\tCould not delete file '" + filename + "'");
			return false;
		}
	}
	
	/**
	 * Deletes a file off the computjer with the given path 
	 * @param path directory and filename of file to delete.
	 * @return true if it was deleted
	 */
	private static boolean deleteFromComputer(String path){
		
		File file = new File(path);
		if( file != null && file.exists() ){
			file.delete();
			return true;
		}
		else{
			return false;
		}
	}

	private static String getImageDirectory() {
		return "resources/" + SQLData.getDatabase() + "/images/";
	}
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img){
		
		BufferedImage image = SwingFXUtils.fromFXImage(img, null);

	    // Return the buffered image
	    return image;
	}

	public static void addImages(Collection<BugImage> images) {
		for(BugImage i : images){
			addImage(i);
		}
	}

	public static void loadLocalImages() {
		System.out.println("Getting local images");
		
		List<BugImage> images = getImagesOffcomputer();
		if( images == null ){
			System.out.println("images uploaded from computer were null!");
			return;
		}
		
		System.out.println("Local Image Count " + images.size());
		for(BugImage i : images ){
			System.out.println("\t" + i.link);
		}
		addImages(images);
		
	}
}














