public class MessageHeader {

	private final char _thread;
	private final int _msgID;

	MessageHeader(char t, int m) {
		_thread = t;
		_msgID = m;
	}

	/**
	 * Implement equals so that instances are only equal if both attributes are
	 * equal.
	 * 
	 * @param o
	 *            the object to compare
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof MessageHeader))
			return false;

		MessageHeader otherMessageHeader = (MessageHeader) o;
		return (_thread == otherMessageHeader._thread) && (_msgID == otherMessageHeader._msgID);
	}

	/**
	 * Implement hashCode so that Map based structures work properly
	 */
	@Override
	public int hashCode() {
		return _msgID * (int) _thread;
	}

	/**
	 * @return a string version of the MessageHeader
	 */
	@Override
	public String toString() {
		return (String.format("%c+%d", _thread, _msgID));
	}

}