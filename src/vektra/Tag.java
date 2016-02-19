package vektra;

/**
 * Each Bug that gets reporter is required to have a Tag which explains what type of problem is being reporter.
 * Each Tag can display a Visual, Audio, Breaking... etc problem.
 * @author James
 *
 */
public class Tag {
	final String message; // Visual representation of the problem (Visual, Audio etc...)
	final int tagid; // Used to distinguish this Tag from any other Tag with the same Message.
	
	public Tag(int tagid, String message) {
		super();
		this.message = message;
		this.tagid = tagid;
	}
	
	@Override
	public String toString(){
		return ("(" + tagid + " : " + message + ")");
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
