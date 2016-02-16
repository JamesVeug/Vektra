package vektra.resources;

import vektra.BugImage;

public class R {

	
	public static BugImage getImage(String link, int screenshotid){
		
		BugImage localImage = LocalResources.getImage(link, screenshotid);
		if( localImage != null ){
			return localImage;
		}
		
		BugImage onlineImage = OnlineResources.getImage(link);
		if( onlineImage != null ){
			LocalResources.saveLocalFile(link,onlineImage, screenshotid);
			return onlineImage;
		}
		
		return null;
	}
	
	public static BugImage getImage(String link, double w, double h, int screenshotid){
		
		BugImage localImage = LocalResources.getImage(link, w, h, screenshotid);
		if( localImage != null ){
			return localImage;
		}
		
		BugImage onlineImage = OnlineResources.getImage(link, w, h);
		if( onlineImage != null ){
			LocalResources.saveLocalFile(link,onlineImage, screenshotid);
			return onlineImage;
		}
		
		return null;
	}
}
