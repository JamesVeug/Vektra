package vektra;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class BugImage {
	
	public final double width;
	public final double height;
	public final String link;
	
	
	// To dispose of
	private ImageView view;
	private Image image;
	
//	AsyncImageProperty imageProperty = new AsyncImageProperty();  // create async image loading property
//
//	ImageView view = new ImageView();  // create a View to display images
//	view.imageProperty().bind(imageProperty);  // bind to the image property so any changes become visible
//
//	imageProperty.imageHandleProperty().set("/my/image/to/load.png");  // set an image to load
	
	// Changes later
	public int screenshotID = -1;
	public BugImage(String link){
		this(-1,-1,link);
	}
	
	public BugImage(double w, double h, String link){
		this.link = link;
		width = w;
		height = h;
	}
	
	public Image getImage(){
		if( image == null ){
//			getImageView();
			//imageProperty.imageHandleProperty().set("/my/image/to/load.png");  // set an image to load
			try{
				
				
				System.out.println("Loading Image: '" + link + "'");
				BufferedImage buffimage = ImageIO.read(new File(link));
				WritableImage image = null;
				image = SwingFXUtils.toFXImage(buffimage, image);
				this.image = image;
			}catch( IllegalArgumentException e ){
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
					
		return image;	
	}
	
	public ImageView getImageView(){
		
		if( view == null ){
			view = new ImageView(getImage());  // create a View to display images
			view.setFitWidth(width);
			view.setFitHeight(height);
		}
		
		return view;
	}
	
	public ImageView cloneView(){	
		ImageView view = new ImageView(getImage());
		view.setFitWidth(width);
		view.setFitHeight(height);
		return view;
	}
	
	/**
	 * Clears unrequired content to free up heap space
	 */
	public void dispose(){
		view = null;
		image = null;
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
