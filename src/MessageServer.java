import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MessageServer extends Object {

	static char threadId = 64; // number before 'A'

	// constructor
	MessageServer() {
	}

	public static void main(String[] args) {

		// port command line argument
		int port = Integer.parseInt(args[0]);

		try (ServerSocket server = new ServerSocket(port)) {
			System.out.println("Starting Message server on port " + port);
			// main MessageBoard object
			MessageBoard messageBoard = new MessageBoard();
			// ThreadPoolExecutor that will execute threads
			ThreadPoolExecutor threadPoolExe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			// waiting for incoming client connections
			while (true) {
				Socket client = server.accept();
				threadId++; // increment threadId for each new client connection
				// execute new MessageServerHandler
				threadPoolExe.execute(new MessageServerHandler(messageBoard, client));
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
