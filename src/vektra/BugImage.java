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
	
	public BugImage(Image image, String link){
		view = new ImageView(image);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + screenshotID;
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
		if (!(obj instanceof BugImage))
			return false;
		BugImage other = (BugImage) obj;
		if (screenshotID != other.screenshotID)
			return false;
		return true;
	}
}
