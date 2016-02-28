package vektra.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;
import vektra.BugImage;
import vektra.OnlineBugImage;

public class OnlineResources {
	
	public static BugImage getImage(String link){
		return createImage(link);
	}
	
	public static BugImage getImage(String link, double w, double h){
		return createImage(link, w, h);
	}
	
	private static BugImage createImage(String link, double w, double h){
		
		// Download image online
		Image importedImage = downloadImage(link);
		if( importedImage == null ){
			
			// Could not download
			return null;
		}
		
		return new OnlineBugImage(w, h, link);
	}
	
	private static BugImage createImage(String link){
			
		// Download image online
		Image importedImage = downloadImage(link);
		if( importedImage == null ){
			
			// Could not download
			return null;
		}
		
		return new OnlineBugImage(importedImage.getWidth(),importedImage.getHeight(), link);
	}

	/**
	 * Download the given link off the internet
	 * @param link
	 * @return
	 */
	public static Image downloadImage(String link) {
		System.out.println("DOWNLOADING IMAGE '" + link + "'");
		
		// Check the link is valid
		System.out.println("Testing");
		InputStream input = null;
		try {
			input = new URL(link).openStream();
		} catch (IOException e) {
			return null;
		}
		System.out.println("Valid");
		
		System.out.println("Downloading");
		// Link is valid. Create an image
		Image image = new Image(input);
		System.out.println("Downloaded");
		return image;
	}
}
