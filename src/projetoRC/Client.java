package projetoRC;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {	
	
	private static final int PORT = 7142;
	private static final int PORTUDP = 9031;
//	private static BufferedReader input;
//	private static PrintStream output;
	
	public static void main(String args[]) throws Exception {
		if (args.length !=1){
			System.err.println ("Usage: java TCPClient <host>");
			System.exit(1);
		}				
		String host = args[0];
		String messageIn;
		Socket client = new Socket(host,PORT);
		DatagramSocket datagramSocket = new DatagramSocket(PORTUDP);
		try { // Receive The Menu display!
			do {		
				byte[] buffer = new byte[256];
				DatagramPacket inPacket = new DatagramPacket(buffer,buffer.length);
				datagramSocket.receive(inPacket);
				messageIn = new String(inPacket.getData(),0,inPacket.getLength());
				System.out.println(messageIn);
			} while (true);
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		finally {
			datagramSocket.close();
		}
//		input = new BufferedReader(
//		new InputStreamReader(client.getInputStream()));
//output = new PrintStream(
//		client.getOutputStream(),true);
		//			Scanner scan = new Scanner (System.in); 
		//			System.out.print(">");  			
		//			messageOut = scan.nextLine();	
		//			output.println(messageOut);  
		//			messageIn = input.readLine();						
		//			System.out.println (messageIn);					
		//			if (messageOut.equalsIgnoreCase("tchau")){
		//				System.out.println("a sair..");
		//				break;
		//			}
					
			//		input.close();
			//		output.close();
		client.close();
	}
}

