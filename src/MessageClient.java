import java.io.*;
import java.net.*;

public class MessageClient extends Object {

	public MessageClient() {
	}

	public static void main(String[] args) throws IOException {

		// command line arguments for MessageClient
		String servername = args[0];
		int port = Integer.parseInt(args[1]);

		// ID to keep track of number of messages SENT
		int messageId = 1;

		try (Socket server = new Socket(servername, port)) {

			// Stream that reads input from the server
			BufferedReader input = new BufferedReader(new InputStreamReader(server.getInputStream(), "UTF-8"));
			// Stream that writes command to the server
			Writer output = new OutputStreamWriter(server.getOutputStream());
			// Stream that reads input from command
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

			String command = "BYE";
			String serverResponse;

			do {
				// for command line UI
				System.out.print("? ");

				// command is set to the user input
				command = userInput.readLine();

				if (command.matches("^SEND:(.*)")) { // SEND PROTOCOL
					// Adding messageID to the SEND client request
					String[] sendComponets = command.split(":");
					String messageBody = sendComponets[1];
					output.write(String.format("SEND:%d:%s%n", messageId, messageBody));
					output.flush();
					messageId++; // Increment messageID

				} else if (command.matches("^GET:[A-Z]{1}[+]\\d(.*)")) { // GET
																			// PROTOCOL
					// sending GET client request
					output.write(String.format("%s%n", command));
					output.flush();

					// receiving server response
					serverResponse = input.readLine();
					if (serverResponse.startsWith("ERR")) {
						System.out.println("No such message");
					} else if (serverResponse.startsWith("OK:")) {
						System.out.printf("%s%n", serverResponse.substring(3));
					}

				} else if (command.equals("LIST")) { // LIST PROTOCOL
					// sending LIST client request
					output.write(String.format("%s%n", command));
					output.flush();
					// receiving LIST headers one by one from server
					do {
						serverResponse = input.readLine();
						if (!(serverResponse.equals("."))) {
							System.out.printf("%s%n", serverResponse);
						}
					} while (!(serverResponse.equals(".")));

				} else if (command.matches("BYE")) { // BYE PROTOCOL
					// sending BYE to close connection with server
					output.write(String.format("%s%n", command));
					output.flush();
				}

			} while (!(command.equals("BYE")));
			System.out.println("Client shutdown");
			server.close();

		} catch (java.net.SocketException e) {
			// if server is closed and client is still running
			System.err.println("Server closed connection");
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}catch (NullPointerException e) {
			System.err.println("Server closed connection");
			System.exit(1);
		}
	}
}
