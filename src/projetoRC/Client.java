package projetoRC;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {	
	
	private static final int port = 6500;
	private static BufferedReader input;
	private static PrintStream output;
	
	public static void main(String args[]) throws Exception {
		if (args.length !=1){
			System.err.println ("Usage: java TCPClient <host>");
			System.exit(1);
		}				
		String host = args[0];
		String messageOut, messageIn;
		Socket client = new Socket(host,port);
		input = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
		output = new PrintStream(
				client.getOutputStream(),true);
		while(true) {			
			Scanner scan = new Scanner (System.in); 
			System.out.print(">");  			
			messageOut = scan.nextLine();	
			output.println(messageOut);  
			messageIn = input.readLine();						
			System.out.println (messageIn);					
			if (messageOut.equalsIgnoreCase("tchau")){
				System.out.println("a sair..");
				break;
			} 					
		}
		input.close();
		output.close();
		client.close();	
	}
}