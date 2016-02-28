package vektra;

import javafx.scene.image.Image;

public class OnlineBugImage extends BugImage{

	public OnlineBugImage(double w, double h, String link) {
		super(w, h, link);
	}

	public OnlineBugImage(String link) {
		super(link);
	}

	@Override
	public Image getImage() {
		if( image == null ){
			try{
				image = new Image(link);
			}catch( IllegalArgumentException e ){
				e.printStackTrace();
				return null;
			}
		}
					
		return image;	
	}

}
