import java.net.InetAddress;
import java.net.Socket;
import java.io.*;

public class MessageServerHandler extends Object implements Runnable {

	MessageBoard messageBoard;
	Socket client;

	public MessageServerHandler(MessageBoard b, Socket cl) {
		messageBoard = b;
		client = cl;
	}

	public void run() {
		try {
			// get and display client's IP address
			InetAddress clientAddress = client.getInetAddress();
			System.out.println("connection : " + clientAddress);
			// Stream that reads the request from the client
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			// Stream that writes server response to the client
			Writer output = new OutputStreamWriter(client.getOutputStream());

			// variables used for processing client requests
			String messageBody = " ";
			int messageId = 0;
			MessageHeader mh;
			char identifier = MessageServer.threadId;

			String command = "BYE";
			do {
				// read client request
				command = input.readLine();

				if (command.matches("^SEND:(.*)")) { // SEND protocol
					// split SEND request and store in array
					String[] sendComponets = command.split(":");
					messageId = Integer.parseInt(sendComponets[1]);
					messageBody = sendComponets[2];
					// saving message to messageBoard
					mh = new MessageHeader(identifier, messageId);
					messageBoard.SaveMessage(mh, messageBody);
					// print statement for server side console
					System.out.printf("SEND:%d:%s%n", messageId, messageBody);

				} else if (command.matches("^GET:[A-Z]{1}[+]\\d(.*)")) { // GET
																			// protocol
					// print statement for server side console
					System.out.printf("%s%n", command);
					// removing "GET:" part from client request
					command = command.substring(4, command.length());
					// separating threadId and messageId
					String[] getComponents = command.split("[+]");
					char threadID = getComponents[0].charAt(0);
					messageId = Integer.parseInt(getComponents[1]);
					// retrieving message body from messageBoard
					mh = new MessageHeader(threadID, messageId);
					messageBody = messageBoard.GetMessage(mh);

					// Check if GET will not return null
					if (messageBody != null) {
						// send message body to client
						output.write(String.format("OK:%s%n", messageBody));
						// print statement for server side console
						System.out.printf("%c+%d=%s%nOK%n", threadID, messageId, messageBody);
					} else {
						// send error message to client
						output.write(String.format("ERR%n"));
						// print statement for server side console
						System.out.printf("%c+%d=null%nERR%n", threadID, messageId);
					}
					// flush GET output stream
					output.flush();

				} else if (command.equals("LIST")) { // LIST protocol
					// iterate through header set and send header to client
					//System.out.print(messageBoard.ListHeaders().toString() + "********");
					for (MessageHeader key : messageBoard.ListHeaders()) {
						output.write(String.format("%s%n", key.toString()));
					}
					// send "." to client so client knows to terminate request
					output.write(String.format(".%n"));
					output.flush();
					// print statement for server side console
					System.out.println("LIST");
				}

			} while (!(command.equals("BYE")));
			System.out.println("BYE");
			client.close();

		} catch (IOException e) {
			System.err.println(e);
		} catch (java.lang.NullPointerException e) {
			// if client does control+c to close connection
			System.err.println("Connection dropped unexpectedly.");
		}
	}

}
