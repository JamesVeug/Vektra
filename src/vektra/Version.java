package vektra;

public class Version {
	public final String version;
	public final Stage stage;
	
	public Version(String version, Stage stage) {
		super();
		this.version = version;
		this.stage = stage;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}
	
}
