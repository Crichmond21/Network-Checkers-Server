package game.server;

import java.io.*;
import java.net.Socket;
import java.text.*;

class ClientHandler extends Thread  
{ 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
  
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
    public void run() { 
    	System.out.println("100");
    	String received;
        while (true) { 
            try { 
            	received = dis.readUTF(); 
            	System.out.println(received);
            	
            	if(received.equals("movePiece")) {
            		int one = dis.readInt();
            		int two = dis.readInt();
            		
            		System.out.printf("%d %d %n", one, two);
            	}
            	
            	if(received.equals("Exit")) 
                {  
                    System.out.println("Client " + this.s + " sends exit..."); 
                    System.out.println("Closing this connection."); 
                    this.s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
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
