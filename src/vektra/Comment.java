package vektra;

public class Comment {
	final int id;
	final String poster;
	final String timePosted;
	final String message;
	
	public Comment(String poster, String timePosted, String message) {
		this(message, -1, poster, timePosted);
	}
	
	public Comment(String comment, int commentid, String whocommented, String datecommented) {
		super();
		this.poster = whocommented;
		this.timePosted = datecommented;
		this.message = comment;
		this.id = commentid;
	}
	/**
	 * @return the poster
	 */
	public String getPoster() {
		return poster;
	}
	/**
	 * @return the timePosted
	 */
	public String getTimePosted() {
		return timePosted;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		if (!(obj instanceof Comment))
			return false;
		Comment other = (Comment) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
