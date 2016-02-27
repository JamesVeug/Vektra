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
import vektra.dialogs.PopupWarning;

public class LocalResources {
	

	private static Map<Integer, String> screenshotIDToDirectory = new HashMap<Integer,String>();
	
	
	public static BugImage getImage(int screenshotid){
		
		// Try and get it from our local storage
		BugImage localImage = getLocalImage(-1, -1, screenshotid);
		if( localImage != null ){
			return localImage;
		}
		
		// Don't have it stored locally
		return null;
	}

	public static BugImage getImage(double w, double h, int screenshotid) {
		
		// Try and get it from our local storage
		BugImage localImage = getLocalImage(w, h, screenshotid);
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
	private static BugImage getLocalImage(double w, double h, int screenshotid) {		
			
		if( screenshotIDToDirectory.get(screenshotid) != null ){
			System.out.println("USING LOCAL IMAGE!");
			return new BugImage(w,h, screenshotIDToDirectory.get(screenshotid));
		}

		// Could not find it!
		System.out.println("Could not find local image '" + screenshotid + "'");
		return null;
	}
	
	public static void synchronizeLocalImages(Collection<BugImage> databaseData){
		System.out.println("Syncing " + databaseData.size());
	
		// TODO
		// TODO Not loading 105.jpg or the others. WHY!?
		// TODO
		
		Map<Integer, BugImage> databaseImages = new HashMap<Integer, BugImage>();
		
		// Sort data by what we have and do not have
		for( BugImage image : databaseData ){			
			databaseImages.put(image.screenshotID, image);
			int screenshotId = image.screenshotID;
			System.out.println("Database " + screenshotId);
			
			if( !screenshotIDToDirectory.containsKey(screenshotId)){
				addImage(image);
			}
			else{
				System.out.println("Already saved: " + screenshotIDToDirectory.get(screenshotId));
			}
		}
		
		System.out.println("Database Images: " + databaseImages.size());
		for(BugImage i : databaseImages.values()){
			System.out.println("\t"+i.screenshotID);
		}
		
		// Check if the images off the computer match the images on the database
//		List<BugImage> computerImages = getImagesOffcomputer();
		List<String> computerImageIDs = getImagesOffcomputer();
		for(String path : computerImageIDs){
			
			int startIndex = path.lastIndexOf("\\")+1;
			
			String ID = path.substring(startIndex,path.indexOf("."));
			if( ID.contains("?") ){
				ID = ID.substring(0, ID.indexOf("?"));
			}
			
			try{
				Integer id = Integer.parseInt(ID);
				if( !databaseImages.containsKey(id) ){
					System.out.println("Do not have image '" + ID + "'");
					
					// Database does not have this image
					// Delete it off computer
					boolean deleted = removeImageViaID(id); 
					if( deleted ){
						System.err.println("Deleted image " + id);
						
					}
					else{
						System.err.println("Could not deleted image " + id);					
					}
				}
			}catch(NumberFormatException e){
				System.out.println("Could not convert ID to INT: " + ID );
			}
		}

		System.out.println("Finished Syncing");

	}
	
	private static List<String> getImagesOffcomputer() {
		File imagesDirectory = new File(getImageDirectory());
		
		// Check that it creates directory
		if( !imagesDirectory.exists() ){
			imagesDirectory.mkdirs();
		}
		
		List<String> images = new ArrayList<String>();
		for( File file : imagesDirectory.listFiles() ){
			
//			String ID = file.getName().substring(0,file.getName().indexOf("."));
			
			try{
//				Integer id = Integer.parseInt(ID);
				images.add(file.getPath());
				System.out.println("Added ID " + file.getName());
			}catch(NumberFormatException e){
				System.out.println("Could not convert ID to INT: " + file.getName() );
			}
		}
				
		return images;
	}

	private static void addImage(BugImage image) {
		
		// Don't store NULL
		if( image == R.getNullImage() ){
			return;
		}
		
		String directory = downloadToComputer(image);
		if( directory != null ){
			screenshotIDToDirectory.put(image.screenshotID, directory);
			System.out.println("Saved To: " + screenshotIDToDirectory.get(image.screenshotID));
		}
		else{
			System.out.println("Could not download image to directory! " + image.screenshotID);
		}
	}


	
	private static Image getImageOffComputer(Integer id) {
		
		if( !screenshotIDToDirectory.containsKey(id) ){
			return null;
		}
		
		File file = new File(screenshotIDToDirectory.get(id));
		
		System.out.println("Getting Image: " + file.getName());
		
//		String ID = file.getName().substring(0,file.getName().indexOf("."));
		
		try{
//			String path = file.getPath();
			BufferedImage buffimage = ImageIO.read(file);
			WritableImage image = null;
			image = SwingFXUtils.toFXImage(buffimage, image);
			
			if( image != null ){
				return image;
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
				
		// Couldn't get image
		return null;
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
		
//		System.out.println("ext: " + ext);
//		System.out.println("File: " + filename);
		
		File directory = new File(getImageDirectory());
		if( !directory.exists() ){
			System.out.println("Directory does not exist. Creating directory!");
			directory.mkdirs();
		}
		
		String filepath = getImageDirectory() + filename;
		File file = new File(filepath);
		if( file.exists() ){
			System.err.println("File already exists. Not downloading to computer (" + getImageDirectory() + filename + ")");
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				return "/"+filepath;
			}
		}
		
//		System.out.println("Iamge: " + image);
//		System.out.println("Image image " + image.getImage());
//		System.out.println("Image image " + image.link);
		BufferedImage buffimage = toBufferedImage(image.getImage());
		try {
		   ImageIO.write(buffimage, ext, file);
		   try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				return "/"+filepath;
			}
		} catch(IOException e) {
			System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
		} catch(IllegalArgumentException e ){
			//PopupWarning.show("Save Local Image", "Could not locally save image", file.getPath() + "\n" + e.getMessage());
		}
		
		// Could not save to computer
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
		if( filename == null ){
			
			// This ID does not exist in our database
			// Look for it in our idrectory
			File directory = new File(getImageDirectory());
			for(String file : directory.list()){
				if( file.startsWith(String.valueOf(id)) ){
					filename = getImageDirectory()+file;
					break;
				}
			}
			
			// Could not delete as we don't have the file anywhere!
			if( filename == null ){
				return false;
			}
		}
		
		boolean deleted = deleteFromComputer(filename);
		if( deleted ){
			System.out.println("\tDeleted '" + filename + "'");
			
			// Remove from storage
			screenshotIDToDirectory.remove(id);
			return true;
		}
		else{
			System.out.println("\tCould not delete file '" + filename + "'");
			return false;
		}
	}
	
	/**
	 * Deletes a file off the computer with the given path 
	 * @param path directory and filename of file to delete.
	 * @return true if it was deleted
	 */
	private static boolean deleteFromComputer(String path){
		
		// Don't have a path. So did not delete
		if( path == null ){
			System.out.println("Null path!");
			return false;
		}
		
		File file = new File(path);
		if( file != null && file.exists() ){
			file.delete();
			return true;
		}
		else{
			System.out.println("Path does not exist!");
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
		
		String path = getImageDirectory();
		File file = new File(path);
		if( file == null || !file.exists() ){
			file.mkdirs();
			return;
		}
		
		// Step through all images
		for(String name : file.list() ){
			String ID = name.substring(0,name.indexOf("."));
			
			try{
				Integer id = Integer.parseInt(ID);
				screenshotIDToDirectory.put(id, file.getAbsolutePath()+"\\"+name);
				System.out.println("Saved local image: " + ID + ", " + path+name);
			}catch(NumberFormatException e ){
				System.out.println("Couldn't convert " + ID + " to int");
			}
		}
		
		
//		List<BugImage> images = getImagesOffcomputer();
//		
//		System.out.println("Local Image Count " + images.size());
//		for(BugImage i : images ){
//			System.out.println("\t" + i.link);
//		}
//		addImages(images);
		
	}

	public static void clearImages() {
		screenshotIDToDirectory.clear();
	}

	public static int getLocalImageCount() {
		return screenshotIDToDirectory.size();
	}

	public static int getImageCountOffComputer(){
		System.out.println("Deleting Local Images count '" + getImageDirectory() + "'");
		
		File directory = new File(getImageDirectory());
		if( directory == null || !directory.exists() ){
			System.out.println("Directory does not exist.");
			return 0;
		}
		
		int imageCount = directory.list().length;
		return imageCount;
	}
	
	/**
	 * Deletes all files in the ImageDirectory and returns the count of how mnay were deleted
	 * @return How many images were deleted
	 */
	public static int deleteLocalImages() {
		System.out.println("Deleting Local Images from '" + getImageDirectory() + "'");
			
		File directory = new File(getImageDirectory());
		if( directory == null || !directory.exists() ){
			System.out.println("Directory does not exist.");
			return 0;
		}
		
		int deleteCount = 0;
		for(File f : directory.listFiles()){
			boolean deleted = f.delete();
			if( deleted ){
				deleteCount++;
			}
		}
		
		System.out.println("Deleted " + deleteCount);
		return deleteCount;
	}
}














