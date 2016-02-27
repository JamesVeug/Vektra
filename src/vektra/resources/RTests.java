package vektra.resources;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vektra.BugImage;

public class RTests {
	@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	private String linkToDownloadValid = "https://www.cancerresearchuk.org/about-cancer/cancer-chat/sites/all/modules/custom/crukc_smileys/packs/crukc/happy.gif";
	private String linkToDownloadInvalid = "https://www.cancerresearchuk.org/about-cancer/cancer-chat/sites/all/modules/custom/crukc_smileys/packs/crukc/happy";
	
	@Before
	public void setup(){
		
		// Make sure we don't have any images stored
		R.clearImages();

		// Delete local images off computer
		LocalResources.deleteLocalImages();		
	}

	@Test
	public void DownloadOnline() {
		
		// VALID IMAGE
		BugImage downloadValid = R.getImage(linkToDownloadValid, -1);
		if( downloadValid == null ){
			fail("Could not create bugImage");
		}
		else if( downloadValid == R.getNullImage() ){
			fail("Could not create bugImage (Returned NULL)");
		}
		else if( downloadValid.getImage() == null ){
			fail("Could not download image!");
		}
		else if( !downloadValid.link.equals(linkToDownloadValid) ){
			fail("Links do not match\nDownload: " + linkToDownloadValid + "\nSaved: " + downloadValid.link);
		}
		
		// INVALID IMAGE
		BugImage downloadInvalid = R.getImage(linkToDownloadInvalid, -1);
		if( downloadInvalid != R.getNullImage()){
			fail("Could not create bugImage");
		}
	}
	
	@Test 
	public void deleteLocalImages(){
		
		assertTrue(R.getImageCount() == 0, "Should be 0 but instead was " + R.getImageCount());
		
		// Download image from online
		BugImage image = R.getImage(linkToDownloadValid, 0);
		ObservableList<BugImage> list = FXCollections.observableArrayList(image);
		
		// Add this image to our list and delete all over images
		LocalResources.synchronizeLocalImages(list);
				
		int localImages = LocalResources.getImageCountOffComputer();
		assertTrue(localImages == 1, "Should only have 1 image in directory but instead have " + localImages);
		
		// Delete local images
		LocalResources.deleteLocalImages();		
		
		// Clear IDs
		LocalResources.clearImages();

		localImages = LocalResources.getImageCountOffComputer();
		assertTrue(localImages == 0, "Should only have 0 image in directory but instead have " + localImages);
	}
	
	@Test
	public void clearImages(){
		
		assertTrue(R.getImageCount() == 0, "Should be 0 but instead was " + R.getImageCount());
		
		BugImage image = R.getImage(linkToDownloadValid, 0);
		assertTrue(R.getImageCount() == 0, "Should be 0 but instead was " + R.getImageCount());
		
		// Add the image to local
		R.addImage(image);
		
		assertTrue(R.getImageCount() == 1, "Should be 1 but instead was " + R.getImageCount());
		
		R.clearImages();
		
		assertTrue(R.getImageCount() == 0, "Should be 0 but instead was " + R.getImageCount());
	}
	

	private void assertTrue(boolean b, String string) {
		if( !b ){
			fail(string);
		}
	}
}
