package game.server;

import java.io.*;  
import java.net.*;

/**
 * Checkers Server 
 * 
 * @author Carter Richmond
 *
 */
public class CheckersServer {
	//Instance variables for pot number and the two client handlers
	private static final int PORT_NUMBER = 7065;
	private static ClientHandler t1;
	private static ClientHandler t2;
	
	//Sends status code to dataOutputStream
	public static void sendStatusCode(DataOutputStream out, int code) throws IOException {
		out.writeInt(code);
		out.flush();
	}
	
	public static void setModified(ClientHandler t) {
		if(t.equals(t1)) {
			t2.setModified(true);
			t2.setTurn(true);
			t1.setTurn(false);
		}else if(t.equals(t2)) {
			t1.setModified(true);
			t1.setTurn(true);
			t2.setTurn(false);
		}
	}

	public static void main(String[] args) {
		System.out.println("Starting Server");
		GameBoard game = new GameBoard();
		
		//Initiates Sockets and input/output steams outside the try block
		ServerSocket CheckersServerSocket;
		
		//Sockets for clients to communicate one
		Socket CheckersClient1;
		Socket CheckersClient2;
		
		//Data streams to and from clients
		DataInputStream client1Requests = null;
		DataOutputStream serverResponseClient1 = null;
		DataInputStream client2Requests = null;
		DataOutputStream serverResponseClient2 = null;

		try {
			//Opens two server sockets for Player/client 1 and Player/client 2
			CheckersServerSocket = new ServerSocket(PORT_NUMBER);
			
			//Accepts socket for player 1 and creates data input and output streams
			System.out.println("Wating for connection 1");
			CheckersClient1 = CheckersServerSocket.accept();
			client1Requests = new DataInputStream(CheckersClient1.getInputStream());
			serverResponseClient1 = new DataOutputStream(CheckersClient1.getOutputStream());
			sendStatusCode(serverResponseClient1, 100);
			
			//Creates new client Handler for the Client1
			t1 = new ClientHandler(CheckersClient1, client1Requests, serverResponseClient1, game, "red");
			t1.start();
			
			//Accepts socket for player 2 and creates data input and output streams
			System.out.println("Wating for connection 2");
			CheckersClient2 = CheckersServerSocket.accept();
			client2Requests = new DataInputStream(CheckersClient2.getInputStream());
			serverResponseClient2 = new DataOutputStream(CheckersClient2.getOutputStream());
			sendStatusCode(serverResponseClient2, 100);
			
			//Creates new Client Handler for Client 2
			t2 = new ClientHandler(CheckersClient2, client2Requests, serverResponseClient2, game, "blue");
			t2.start();
			t2.setTurn(false);

			//Closes the server socket to no longer accept connections
			CheckersServerSocket.close();
			
			
		}catch (Exception e) {
			//Prints error message followed by exception if exception is caught
			System.out.println("Error while opening socket on port " + PORT_NUMBER);
			System.out.println(e);
			System.exit(1);
		}
		System.out.println("Server Running");
	}
}
