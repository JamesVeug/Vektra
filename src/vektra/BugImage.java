package vektra;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BugImage {
	
	public final String link;
	public final ImageView view;
	
	// Changes later
	public int screenshotID = -1;
	public BugImage(ImageView v, String link){
		view = v;
		this.link = link;
	}

	public BugImage(Image image, double w, double h, String link){
		view = new ImageView(image);
		view.setFitWidth(w);
		view.setFitHeight(h);
		this.link = link;
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
