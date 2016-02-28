package vektra;

import javafx.scene.image.Image;
import vektra.dialogs.PopupError;

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
				PopupError.show("Get Online Image","Unable to obtain image.\n"+link+"\n"+e.getMessage());
				return null;
			}catch (OutOfMemoryError e) {
				e.printStackTrace();
				PopupError.show("Get Online Image","Unable to obtain image.\n"+link+"\nRan out of memory!");
			}
		}
					
		return image;	
	}

}
