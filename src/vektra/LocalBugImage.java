package vektra;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import vektra.dialogs.PopupError;

public class LocalBugImage extends BugImage{
	public LocalBugImage(double w, double h, String link) {
		super(w, h, link);
	}
	
	public LocalBugImage(String link) {
		super(link);
	}

	public Image getImage(){
		if( image == null ){
			try{
				
				
				System.out.println("Loading Image: '" + link + "'");
				BufferedImage buffimage = null;
				
				InputStream input = LocalBugImage.class.getClass().getResourceAsStream(link);
				if( input != null ){
					buffimage = ImageIO.read(input);
				}
				else{
					input = LocalBugImage.class.getClass().getResourceAsStream("/"+link);
					if( input != null ){
						buffimage = ImageIO.read(input);
					}
					else{
						File file = new File(link);
						if( file.exists() ){
							buffimage = ImageIO.read(file);
						}
						else{
							throw new IOException("Could not find solution to loading file.");
						}
					}
				}
				
				
				WritableImage image = null;
				image = SwingFXUtils.toFXImage(buffimage, image);
				this.image = image;
			}catch( IllegalArgumentException e ){
				e.printStackTrace();
				PopupError.show("Get Local Image","Unable to obtain image from link\n"+link+"\n"+e.getMessage());
				return null;
			}catch (IOException e) {
				e.printStackTrace();
				PopupError.show("Get Local Image","Unable to obtain image from link\n"+link+"\n"+e.getMessage());
			}catch (OutOfMemoryError e) {
				e.printStackTrace();
				PopupError.show("Get Local Image","Unable to obtain image.\n"+link+"\nRan out of memory!");
			}
		}
					
		return image;	
	}
}
