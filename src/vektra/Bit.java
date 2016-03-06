package vektra;

import java.util.ArrayList;
import java.util.List;

/**
 * Class related to what bit the reporter but is at.
 * The bit class stores the possible bit objects so there are no duplicates.
 * @author James
 *
 */
public class Bit {
	
	// Set Object for when there are no matches for the bit
	public static final Bit NULL = new Bit("NULL");
	
	// Possible bits for the game
	public static final Bit B64 = new Bit("64");
	public static final Bit B32= new Bit("32");
	public static final List<Bit> bitList = new ArrayList<Bit>(); 
	static{
		bitList.add(B64);
		bitList.add(B32);
	}

	/**
	 * String representation of the bit
	 */
	private final String bit;
	
	/**
	 * Constructor to be created from the set objects
	 * Stores the given string and is used for display.
	 * @param string Representation of the bit.
	 */
	private Bit(String string) {
		this.bit = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return bit;
	}
	
	/**
	 * Gets the appropriate bit object from our list of bits and returns it.
	 * If there are no strings bits that match the given parameter. Then NULL will be returned
	 * @param bit bit to look for amongst the set bits.
	 * @return final bit object or the Null bit
	 */
	public static Bit get(String bit) {
		for(Bit p : bitList){
			if( p.bit.equalsIgnoreCase(bit) ){
				return p;
			}
		}
		return NULL;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bit == null) ? 0 : bit.hashCode());
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
		if (!(obj instanceof Bit))
			return false;
		Bit other = (Bit) obj;
		if (bit == null) {
			if (other.bit != null)
				return false;
		} else if (!bit.equals(other.bit))
			return false;
		return true;
	}
}