package vektra;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BugImage {

	public static Map<String, Image> images = new HashMap<String, Image>();
	
	public final ImageView view;
	private BugImage(String link){
		this(importImage(link),getWidth(link),getHeight(link));
	}
	
	private static double getHeight(String link) {
		Image i = images.get(link);
		if( i == null ){
			return 0;
		}
		return i.getHeight();
	}

	private static double getWidth(String link) {
		Image i = images.get(link);
		if( i == null ){
			return 0;
		}
		return i.getWidth();
	}

	private BugImage(String link, double w, double h){
		this(importImage(link),w,h);
	}

	private BugImage(Image image, double w, double h){
		view = new ImageView(image);
		view.setFitWidth(w);
		view.setFitHeight(h);
	}
	
	public static BugImage createImage(String link, double w, double h){
		
		// Return the object we already have
		if( images.keySet().contains(link) ){
			return new BugImage(images.get(link),w,h);
		}
		
		return new BugImage(link,w,h);
	}
	
	public static BugImage createImage(String link){
		
		
		
		// Return the object we already have
		if( images.keySet().contains(link) ){
			Image image = images.get(link);
			return new BugImage(image,image.getWidth(),image.getHeight());
		}
		
		return new BugImage(link);
	}
	
	private static Image importImage(String link) {
		
		Image i = new Image(link);
		if( i != null ){
			images.put(link, i);
		}
		
		return i;
	}
	
	public Image getImage(){
		return view.getImage();
	}
	
	public ImageView getImageView(){
		return view;
	}

	public ImageView cloneView() {
		return new ImageView(view.getImage());
	}
}
