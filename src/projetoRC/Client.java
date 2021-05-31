package projetoRC;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {	
	
	private static final int PORT = 7142;
	private static final int PORTUDP = 9031;
	private static BufferedReader input;
	private static PrintStream output;
	public static void main(String args[]) throws Exception {
		if (args.length !=1){
			System.err.println ("Usage: java TCPClient <host>");
			System.exit(1);
		}				
		String host = args[0];
		String messageOut, messageIn;
		Socket client = new Socket(host,PORT);

		input = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
		output = new PrintStream(
				client.getOutputStream(),true);
		while(true) {
			messageIn = input.readLine();
			String stringVec[] = messageIn.split("-&-");
			messageIn = "";
			for(int i = 0; i < stringVec.length; i++)
				messageIn = messageIn + stringVec[i] + "\n";
			System.out.println(messageIn);
			Scanner scan = new Scanner (System.in); 
			System.out.print(">");  			
			messageOut = scan.nextLine();	
			output.println(messageOut);  
			if (messageOut.equals("99")){
				System.out.println("a sair..");
				break;
			} 					
		}
		input.close();
		output.close();
		client.close();	
	}
}