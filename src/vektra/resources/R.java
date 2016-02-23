package vektra.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import vektra.BugImage;

public class R {
	private static BugImage NULL;
	
	public static BugImage getImage(String link, int screenshotid){
		
		BugImage localImage = LocalResources.getImage(link, screenshotid);
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
		
		BugImage localImage = LocalResources.getImage(link, w, h, screenshotid);
		if( localImage != null && localImage.getImage() != null  ){
			return localImage;
		}
		
		BugImage onlineImage = OnlineResources.getImage(link, w, h);
		if( onlineImage != null && onlineImage.getImage() != null  ){
			return onlineImage;
		}
		
		return getNullImage();
	}

	public static BugImage getNullImage() {
		if( NULL == null ){
			NULL = new BugImage(new Image("error.png"), "error.png");
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
}









