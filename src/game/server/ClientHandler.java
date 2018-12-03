package game.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.*;

import game.server.GameBoard.gamePiece;

class ClientHandler extends Thread  
{ 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    
    private GameBoard game;
    private String team;
    private boolean modified = false;
    private static int[] modifiedPair = new int[4];
    ClientHandler t2;
      
  
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, GameBoard game, String team)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.game = game;
        this.team = team;
    } 
    
    public void sendGameBoard() {
    	//if modified write modified ints to client
    	if(modified) {
    		try {
    			dos.writeInt(210);
    			dos.writeInt(modifiedPair[0]);
    			dos.writeInt(modifiedPair[1]);
    			dos.writeInt(modifiedPair[2]);
    			dos.writeInt(modifiedPair[3]);
    			dos.flush();
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    		//set modified to false
    		modified = false;
    	}else {
    		//if not modified send code 200
    		try {
    			dos.writeInt(200);
    			dos.flush();
    		}catch(Exception e) {
    			e.printStackTrace();
    		}	
    	}
    }
    
    public void setThread(ClientHandler t) {
    	t2 = t;
    }
    
    public void setModified(boolean b) {
    	modified = b;
    }
    
    @Override
    public void run() { 
    	System.out.println("100");
    	String received;
    	
    	//Indexes for piece and destination for moving
    	int initialRow = -1;
    	int initialColumn = -1;
    	int moveRow = -1;
    	int moveColumn = -1;
    	
        while (true) { 
        	
        	try { 
               	received = dis.readUTF();       
             
               	switch(received) {
               		case "selectPiece":
               			initialRow = dis.readInt();
               			initialColumn = dis.readInt();
               			dos.writeInt(200);
               			break;
               		
               		case "selectMovementSpot":
               			moveRow = dis.readInt();
               			moveColumn = dis.readInt();
               			if(moveRow < 0 || moveColumn < 0) {
               				dos.writeInt(300);
               				dos.flush();
               				break;
               			}
               			try {
               				if(team.equals(game.getTeam(initialRow, initialColumn))) {
               					//Attempt to move piece
                   				game.movePiece(initialRow, initialColumn, moveRow, moveColumn);
                   				
                   				//If no exception caught set modified == true
                   				//t2.setModified(true);
                   				//Set modified array
                   				modifiedPair[0] = initialRow;
                   				modifiedPair[1] = initialColumn;
                   				modifiedPair[2] = moveRow;
                   				modifiedPair[3] = moveColumn;
                   				
                   				//Send code 200
                       			dos.writeInt(200);
                       			dos.flush();
                       			break;
               				}else {
               					dos.writeInt(300);
               					dos.flush();
               				}
               				Thread.sleep(100);
                   			//CheckersServer.switchActive(this);
               			}catch(IllegalStateException e) {
               				//If exception send code 300 invalid move
               				e.printStackTrace();
               				dos.writeInt(300);
                   			dos.flush();
               			}
               			break;	
               		
               		case "getGameState":
               			sendGameBoard();
               			break;
               	}                	
                	
               	if(received.equals("Exit")) {  
               		System.out.println("Client " + this.s + " sends exit..."); 
               		System.out.println("Closing this connection."); 
               		this.s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
               	} 
            } catch (SocketException e1) {
            	System.exit(1);
            } catch (Exception e2) { 
            	e2.printStackTrace(); 
            }
        } 
          
        try { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
}
