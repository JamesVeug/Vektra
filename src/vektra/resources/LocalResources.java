package vektra.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import vektra.BugImage;
import vektra.dialogs.PopupError;

public class LocalResources {
	
//	private static final String NAME = "NAME";
//	private static final String LINK = "LINK";
//	private static final String ID = "ID";
	
	private static final String LOCALRESOURCEFILE_IMAGES_DIRECTORY = "resources/images/";
//	private static final String LOCALRESOURCEFILE_NAME = "local.info";
//	private static InputStream localResourceFile;
	
	// All images we have downloaded online
	public static Map<String, String> imageLinkToName = new HashMap<String, String>();
	public static Map<String, Image> imageLinkToImage = new HashMap<String, Image>();
	
	public static Map<String, BugImage> toSync = new HashMap<String,BugImage>();
	
	
	public static Map<Integer, Image> screenshotIDToImage = new HashMap<Integer,Image>();
	
	//public static Map<Integer, List<String>> bugIDToLinkList = new HashMap<Integer, List<String>>();
	
	
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
		
		// Load local images
		if( screenshotIDToImage.isEmpty() ){
			loadLocalImages();
		}
		

		Image imageViaLink = imageLinkToImage.get(link);
		if( imageViaLink != null ){
			
			if( w == -1 ){
				w = imageViaLink.getWidth();
			}
			
			if( h == -1 ){
				h = imageViaLink.getHeight();
			}
			
			System.out.println("USING LOCAL IMAGE!");
			return new BugImage(imageViaLink, w,h, link);
		}
		
		Image imageViaID = screenshotIDToImage.get(screenshotid);
		if( imageViaID != null ){
			imageLinkToImage.put(link, imageViaID);
			
			if( w == -1 ){
				w = imageViaID.getWidth();
			}
			
			if( h == -1 ){
				h = imageViaID.getHeight();
			}
			
			System.out.println("USING LOCAL IMAGE!");
			return new BugImage(imageViaID, w,h, link);
		}
		
		// Could not find it!
		System.out.println("Could not find local image '" + screenshotid + "'");
		return null;
	}

	@SuppressWarnings("unused")
	private static void loadLocalImages() {
		System.out.println("LOCAL: Loading local images");
		
		File imagesDirectory = new File(LOCALRESOURCEFILE_IMAGES_DIRECTORY);
		
		if( !imagesDirectory.exists() || !imagesDirectory.isDirectory() ){
			PopupError.show("Could not load local images", "Can not file directory '" + LOCALRESOURCEFILE_IMAGES_DIRECTORY + "'.");
			return;
		}

		System.out.println("Files Count: " + imagesDirectory.listFiles().length);
		for( File file : imagesDirectory.listFiles() ){
//			System.out.println("File: " + file.getName());
			
			String ID = file.getName().substring(0,file.getName().indexOf("."));
//			System.out.println("ID: " + ID);
			
			try{
				String path = file.getPath();
//				System.out.println("Path: " + path);
				BufferedImage buffimage = ImageIO.read(file);
				WritableImage image = null;
				image = SwingFXUtils.toFXImage(buffimage, image);
				
				System.out.println("Image: " + image);
				
				
				if( image != null ){
					screenshotIDToImage.put(Integer.parseInt(ID), image);
					System.out.println("Saved " + ID);	
				}
				else{
					System.out.println("image could not be loaded");
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
		
		System.out.println("LOCAL: Finished Loading local images (" + screenshotIDToImage.size() + ")");
	}
	
	public static void synchronizeLocalImages(){
		System.out.println("Syncing " + toSync.size());
		
		List<String> toRemove = new ArrayList<String>();
		
		for(Entry<String,BugImage> entry : toSync.entrySet()){
			String name = entry.getKey();
			BugImage onlineImage = entry.getValue();
			System.out.println("Syncing '" + name + "'");
			
			System.out.println("Saving local file ");
			if( onlineImage.screenshotID == -1 ){
				System.out.println("id = -1");
				System.out.println("Can not save image locally: '" + name + "'");
				continue;
			}
			
			String ext = name.substring(name.lastIndexOf(".")+1);			
			String filename = onlineImage.screenshotID + "." + ext;
			
			System.out.println("ext: " + ext);
			System.out.println("File: " + filename);
			
			
			File file = new File(LOCALRESOURCEFILE_IMAGES_DIRECTORY + filename);
			BufferedImage image = toBufferedImage(onlineImage.getImage());
			try {
			   ImageIO.write(image, ext, file);  // ignore returned boolean
			   toRemove.add(name);
			} catch(IOException e) {
				System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
			}
		}

		// Clear both lists
		for(String name : toRemove){
			toSync.remove(name);
		}
		toSync.clear();
		System.out.println("Finished Syncing");

	}

	public static void saveLocalFile(String link, BugImage onlineImage, int screenshotid) {

		System.out.println("Recorded local file '" + link + "'");
		
		String name = screenshotid + link.substring(link.indexOf("."));
		if( name.contains("?") ){
			name = name.substring(0,name.indexOf("?"));
		}
		
		toSync.put(name, onlineImage);
		
		imageLinkToName.put(link, name);
		imageLinkToImage.put(link, onlineImage.getImage());
	}
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{

//	    // Create a buffered image with transparency
//	    BufferedImage bimage =  new BufferedImage((int)img.getWidth(), (int)img.getHeight(), BufferedImage.TYPE_INT_ARGB);
//	    bimage.getData().get
		
		BufferedImage image = SwingFXUtils.fromFXImage(img, null);

	    // Return the buffered image
	    return image;
	}
}














