package vektra;

public class Version {
	public final String version;
	public final Stage stage;
	public final Bit bit;
	
	public Version(String version, Stage stage, Bit bit) {
		super();
		this.version = version;
		this.stage = stage;
		this.bit = bit;
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
	
	/**
	 * @return the bit
	 */
	public Bit getBit(){
		return bit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bit == null) ? 0 : bit.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		if (bit == null) {
			if (other.bit != null)
				return false;
		} else if (!bit.equals(other.bit))
			return false;
		if (stage == null) {
			if (other.stage != null)
				return false;
		} else if (!stage.equals(other.stage))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	
}
