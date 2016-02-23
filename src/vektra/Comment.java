package vektra;

import java.sql.Date;

public class Comment {
	public final int id;
	public final String poster;
	public final Date timePosted;
	public final String message;
	public final int bugid;
	
	public Comment(String poster, Date timePosted, String message, int bugid) {
		this(message, -1, poster, timePosted, bugid);
	}
	
	public Comment(String comment, int commentid, String whocommented, Date datecommented, int bugid) {
		super();
		this.poster = whocommented;
		this.timePosted = datecommented;
		this.message = comment;
		this.id = commentid;
		this.bugid = bugid;
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
	public Date getTimePosted() {
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
