package vektra;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class BugImage {

	public static Map<Dimension, Map<String, Image>> images = new HashMap<Dimension, Map<String, Image>>();
	
	private BugImage(String link){
		
	}
	
	public static BugImage createImage(String link, int w, int h){
		Dimension d = new Dimension(w,h);
		Image image = null;
		
		// Check the dimension has not been created yet
		if( images.containsKey(d) ){
			
			// Check that we don't have this image yet
			if( images.get(d).containsKey(link) ){
				return null;
			}
			else{
				
				image = new Image(link,w,h,true,true);
				if( image != null ){
					images.get(d).put(link,image);
				}
			}
		}
		else{
			
			// Don't even have the dimension.
			// Add the dimension
			// Add the image
			
			// Create the image map
			Map<String, Image> newImageMap = new HashMap<String, Image>()
			
			// Add our image
			image = new Image(link,w,h,true,true);
			if( image != null ){
				newImageMap.put(link,image);
			}
			
			// Add the iamge map to the dimension map and add to everything.
			images.put(d, newImageMap);
		}
		
		return null;
	}
}
