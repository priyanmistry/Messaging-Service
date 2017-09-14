import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBoard extends Object {

	Map<MessageHeader, String> mBoard;

	public MessageBoard() {
		mBoard = new ConcurrentHashMap<MessageHeader, String>();
	}

	synchronized void SaveMessage(MessageHeader mh, String msg) {
		// check if message header is already in use
		// save message to message board
		mBoard.putIfAbsent(mh, msg);
	}

	String GetMessage(MessageHeader mh) {
		// return the body of the appropriate message; or null if there is no
		// such message.
		return mBoard.get(mh);
	}

	Set<MessageHeader> ListHeaders() {
		// returns the Set of all message headers
		return mBoard.keySet();
	}
}
