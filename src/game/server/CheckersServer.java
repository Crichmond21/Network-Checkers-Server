package game.server;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CheckersServer {
	private static final int PORT_NUMBER = 7065;
	private static ClientHandler t1;
	private static ClientHandler t2;
	
	public static void sendStatusCode(DataOutputStream out, int code) throws IOException {
		out.writeInt(code);
		out.flush();
	}
	
	/**
	 * 
	public static void switchActive(ClientHandler deactivate) throws InterruptedException {
		if(deactivate.equals(t1)) {
			t2.notify();
			t1.wait();
		}
		if(deactivate.equals(t2)) {
			t1.notify();
			t2.wait();
		}
	}
	*/

	public static void main(String[] args) {
		System.out.println("Starting Server");
		GameBoard game = new GameBoard();
		
		//Initiates Sockets and input/output steams outside the try block
		ServerSocket CheckersServerSocket;
		
		Socket CheckersClient1;
		Socket CheckersClient2;
		
		DataInputStream client1Requests = null;
		DataOutputStream serverResponseClient1 = null;
		DataInputStream client2Requests = null;
		DataOutputStream serverResponseClient2 = null;
		
		//Strings to hold message in and message out
		String msgin1 = "", msgout1 ="";
		String msgin2 = "", msgout2 ="";
		
		try {
			//Opens two server sockets for Player/client 1 and Player/client 2
			CheckersServerSocket = new ServerSocket(PORT_NUMBER);
			
			//Accepts socket for player 1 and creates data input and output streams
			System.out.println("Wating for connection 1");
			CheckersClient1 = CheckersServerSocket.accept();
			client1Requests = new DataInputStream(CheckersClient1.getInputStream());
			serverResponseClient1 = new DataOutputStream(CheckersClient1.getOutputStream());
			sendStatusCode(serverResponseClient1, 100);
			
			t1 = new ClientHandler(CheckersClient1, client1Requests, serverResponseClient1, game, "red");
			t1.start();
			//Thread.sleep(100);
			
			//Accepts socket for player 2 and creates data input and output streams
			System.out.println("Wating for connection 2");
			CheckersClient2 = CheckersServerSocket.accept();
			client2Requests = new DataInputStream(CheckersClient2.getInputStream());
			serverResponseClient2 = new DataOutputStream(CheckersClient2.getOutputStream());
			sendStatusCode(serverResponseClient2, 100);
			
			t2 = new ClientHandler(CheckersClient2, client2Requests, serverResponseClient2, game, "blue");
			t2.start();
			//t2.wait();
			
			//t1.setThread(t2);
			//t2.setThread(t1);
			
			//Thread.sleep(100);
			CheckersServerSocket.close();
			
			
		}catch (Exception e) {
			//Prints error message followed by exception if exception is caught
			System.out.println("Error while opening socket on port " + PORT_NUMBER);
			System.out.println(e);
			System.exit(1);
		}
		
		System.out.println("Server Running");
		
		//MAIN LOOP
		while(true) {
			
		}
	}
}
