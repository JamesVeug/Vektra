package vektra;

public class Tag {
	final String message;
	final int tagid;
	final int bugid;
	
	public Tag(int tagid, int bugid, String message) {
		super();
		this.message = message;
		this.tagid = tagid;
		this.bugid = bugid;
	}
	
	@Override
	public String toString(){
		return ("(" + tagid + " : " + bugid + " : " + message + ")");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}
