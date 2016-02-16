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
import javafx.scene.image.WritableImage;
import vektra.BugImage;
import vektra.SQLData;
import vektra.dialogs.PopupError;

public class LocalResources {
	
//	private static final String NAME = "NAME";
//	private static final String LINK = "LINK";
//	private static final String ID = "ID";
	
//	private static final String LOCALRESOURCEFILE_NAME = "local.info";
//	private static InputStream localResourceFile;
	
	// All images we have downloaded online
//	public static Map<String, String> imageLinkToName = new HashMap<String, String>();
	public static Map<String, Image> imageLinkToImage = new HashMap<String, Image>();
	public static Map<Integer, String> imageNameToLink = new HashMap<Integer, String>();
	
	public static List<SyncData> toSync = new ArrayList<SyncData>();
	public static List<SyncData> toRemove = new ArrayList<SyncData>();
	
	
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
		loadLocalImages();
		

		Image imageViaLink = imageLinkToImage.get(link);
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
		
		Image imageViaID = screenshotIDToImage.get(screenshotid);
		if( imageViaID != null ){
			System.out.println("Via ID");
			imageLinkToImage.put(link, imageViaID);
			imageNameToLink.put(new Integer(screenshotid), link);
			
			System.out.println("Name to Links:");
			for(int i : imageNameToLink.keySet()){
				System.out.println("\t" + i);
			}
			
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
		
		File imagesDirectory = new File(getImageDirectory());
		
		if( !imagesDirectory.exists() || !imagesDirectory.isDirectory() ){
			PopupError.show("Could not load local images", "Can not file directory '" + getImageDirectory() + "'.");
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
		
		List<SyncData> toRemove = new ArrayList<SyncData>();
		
		for(SyncData data: toSync){
			String name = data.name;
			BugImage onlineImage = data.image;
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
			
			
			File file = new File(getImageDirectory() + filename);
			BufferedImage image = toBufferedImage(onlineImage.getImage());
			try {
			   ImageIO.write(image, ext, file);  // ignore returned boolean
			   toRemove.add(data);
			} catch(IOException e) {
				System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
			}
		}

		// Clear both lists
		for(SyncData data : toRemove){
			toSync.remove(data);
		}
		toSync.clear();
		System.out.println("Finished Syncing");

	}
	
	public static void removeImagesViaID(List<Integer> ids){
		System.out.println("Removing local images (" + ids.size() + ")");
		
		for(Integer id : ids){
			System.out.println("\tID: " + id);
			String link = imageNameToLink.get(new Integer(id));
			if( link == null ){
				System.out.println("\tNo link supplied");
				
				for(Integer s : imageNameToLink.keySet()){
					System.out.println("\t\t" + s);
				}
				continue;
			}
			
			imageNameToLink.remove(id);
			imageLinkToImage.remove(link);
			screenshotIDToImage.remove(id);
			
			String ext = link.substring(link.lastIndexOf("."));
			if( ext.contains("?") ){
				ext = ext.substring(0, ext.indexOf("?"));
			}
			
			// delete from directory
			String filename = getImageDirectory() + id + ext;
			File file = new File(filename);
			if( file != null && file.exists() ){
				file.delete();
				System.out.println("\tDeleted");
			}
			else{
				System.out.println("\tCould not delete file '" + filename + "'");
			}
		}
		
	}

	private static String getImageDirectory() {
		return "resources/" + SQLData.getServer() + "/images/";
	}

	public static void saveLocalFile(String link, BugImage onlineImage, int screenshotid) {

		System.out.println("Recording local file '" + link + "'");
		
		String name = screenshotid + link.substring(link.lastIndexOf("."));
		if( name.contains("?") ){
			name = name.substring(0,name.indexOf("?"));
		}
		
		System.out.println("Record Name " + name);
		toSync.add(new LocalResources().new SyncData(name, link, onlineImage));
		
		// Uncomment this to reduce harddrive space
		// But this will confuse people when looking in the folder!
		imageNameToLink.put(screenshotid, link);
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
	
	
	private class SyncData{
		
		public final String name;
		public final String link;
		public final BugImage image;

		public SyncData(String name, String link, BugImage image){
			this.name = name;
			this.link = link;
			this.image = image;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			if (!(obj instanceof SyncData))
				return false;
			SyncData other = (SyncData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private LocalResources getOuterType() {
			return LocalResources.this;
		}
		
	}
}














