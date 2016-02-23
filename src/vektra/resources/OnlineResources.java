package vektra.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import vektra.BugImage;

public class OnlineResources {

	// All images we have downloaded online
	public static Map<String, Image> images = new HashMap<String, Image>();
	
	public static BugImage getImage(String link){
		return createImage(link);
	}
	
	public static BugImage getImage(String link, double w, double h){
		return createImage(link, w, h);
	}
	
	private static BugImage createImage(String link, double w, double h){
		
		// Return the object we already have
		if( images.keySet().contains(link) ){
			return new BugImage(images.get(link),w,h,link);
		}
		
		// Download image online
		Image importedImage = downloadImage(link);
		if( importedImage == null ){
			
			// Could not download
			return null;
		}
		
		return new BugImage(importedImage, w, h, link);
	}

	/**
	 * Download the given link off the internet
	 * @param link
	 * @return
	 */
	private static Image downloadImage(String link) {
		System.out.println("DOWNLOADING IMAGE '" + link + "'");
		
		// Check the link is valid
		InputStream input = null;
		try {
			input = new URL(link).openStream();
		} catch (IOException e) {
			return null;
		}
		
		// Link is valid. Create an image
		Image i = new Image(input);
		if( i != null && !i.isError() ){
			images.put(link, i);
		}
		
		return i;
	}
	
	
	private static BugImage createImage(String link){
		
		// Return the object we already have
		if( images.keySet().contains(link) ){
			Image image = images.get(link);
			return new BugImage(image,image.getWidth(),image.getHeight(), link);
		}
			
		// Download image online
		Image importedImage = downloadImage(link);
		if( importedImage == null ){
			
			// Could not download
			return null;
		}
		
		return new BugImage(importedImage,importedImage.getWidth(),importedImage.getHeight(), link);
	}
}
