package vektra.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vektra.BugImage;
import vektra.LocalBugImage;
import vektra.dialogs.PopupError;

public class R {
	private static String FILE_DIRECTORY = null;
	private static BugImage NULL;
	
	public static BugImage getImage(String link, int screenshotid){
		
		BugImage localImage = LocalResources.getImage(screenshotid);
		if( localImage != null && localImage.getImage() != null ){
			return localImage;
		}
		
		BugImage onlineImage = OnlineResources.getImage(link);
		if( onlineImage != null && onlineImage.getImage() != null  ){
			return onlineImage;
		}
		
		return getNullImage();
	}
	
	public static BugImage getImage(String link, double w, double h, int screenshotid){
		
		BugImage localImage = LocalResources.getImage(w, h, screenshotid);
		if( localImage != null  ){
			return localImage;
		}
		
		BugImage onlineImage = OnlineResources.getImage(link, w, h);
		if( onlineImage != null  ){
			return onlineImage;
		}
		
		return getNullImage();
	}

	public static BugImage getNullImage() {
		if( NULL == null ){
			NULL = new LocalBugImage("error.png");
		}
		return NULL;
	}

	public static void removeImagesViaID(List<Integer> removedImages) {
		
		// Only need to remove from local storage
		LocalResources.removeImagesViaID(removedImages);
		
	}

	public static void removeImages(Collection<BugImage> images) {
		List<Integer> ids = new ArrayList<Integer>();
		for(BugImage i : images){
			ids.add(i.screenshotID);
		}
		removeImagesViaID(ids);
	}

	public static void addImages(Collection<BugImage> images) {

		// Only need to add to local storage
		LocalResources.addImages(images);
	}

	public static void addImage(BugImage image) {
		List<BugImage> list = new ArrayList<BugImage>();
		list.add(image);
		addImages(list);
	}

	public static void clearImages() {
		LocalResources.clearImages();
	}
	
	public static int getImageCount(){
		return LocalResources.getLocalImageCount();
	}

	public static String getDirectory() {
		if( FILE_DIRECTORY == null ){
			try{
				String APPDATA = System.getenv("APPDATA");
				File file = new File(APPDATA + "/VektraBugReporter");
				if( !file.exists() ){
					file.mkdirs();
				}
				
				FILE_DIRECTORY = file.getPath();
			}catch( SecurityException e ){
				FILE_DIRECTORY = R.class.getResource("v.png").getPath();
				FILE_DIRECTORY = FILE_DIRECTORY.substring(0, FILE_DIRECTORY.lastIndexOf("/"));
				
				PopupError.show("Getting File Directory", "Access Denied getting file directory\n"+e.getMessage() + "\n\nUsing default directory:\n" + FILE_DIRECTORY);
			}
		}
			
		System.out.println("FILE_DIRECTORY: " + FILE_DIRECTORY);
		return FILE_DIRECTORY;
	}
}









