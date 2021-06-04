package projetoRC;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private static final int PORT = 7142;
	private static BufferedReader input;
	private static PrintStream output;
	private static InetAddress host;
	
	public static void main(String args[]) throws Exception {
		if (args.length !=1){
			System.err.println ("Usage: java Client <host>");
			System.exit(1);
		}				
		host = InetAddress.getByName(args[0]);
		String messageOut, messageIn;
		Socket client = new Socket(host,PORT);

		input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		output = new PrintStream(client.getOutputStream(),true);
		System.out.println(getMenu());
		Scanner scan;
		while(true) {
			scan = new Scanner (System.in); 
			System.out.print(">");  			
			messageOut = scan.nextLine();
			output.println(messageOut);
			if (messageOut.equals("99")){
				System.out.println("a sair..");
				break;
			}else if (messageOut.equals("0")){
				System.out.println(getMenu());
				messageIn = input.readLine();
			}else if (messageOut.equals("2")){
				System.out.println("Please choose a User: ");
				output.println(scan.nextLine());
				System.out.print("Enter your message: ");
				output.println(scan.nextLine());
//				accessServerUDP(scan.nextLine());
			}else if (messageOut.equals("3")){
				System.out.print("Enter your message for all Users: ");
				output.println(scan.nextLine());
//				accessServerUDP(scan.nextLine());
			}else {
				messageIn = input.readLine();
				if(messageIn != null) {
					String stringVec[] = messageIn.split("-&-");
					messageIn = "";
					for(int i = 0; i < stringVec.length; i++)
						messageIn = messageIn + stringVec[i] + "\n";
				}
				System.out.println(messageIn);
			}
		}
		scan.close();
		input.close();
		output.close();
		client.close();	
	}
	
//	private static void accessServerUDP(String message) {
//		try {
//			datagramSocket = new DatagramSocket();
//			String messageIn = null;
//			outPacket = new DatagramPacket(message.getBytes(),message.length(),host,PORTUDP);				
//			datagramSocket.send(outPacket);
//			buffer = new byte[256];
//			inPacket = new DatagramPacket(buffer, buffer.length);		
//			//Should not print message back.....
//			datagramSocket.receive(inPacket);
//			messageIn = new String(inPacket.getData(),0, inPacket.getLength());
//			System.out.println(messageIn);
//		}
//		catch(Exception ex) {
//			ex.printStackTrace();
//		}
//		finally {
//			datagramSocket.close();
//		}
//	}
	
	private static String getMenu() {
		return "MENU CLIENT\n0  - Menu Inicial\n1  - Listar utilizadores online\n2  - Enviar mensagem a um utilizador\n3  - Enviar mensagem a todos os utilizadores\n4  - lista branca de utilizadores\n5  - lista negra de utilizadores\n99 - Sair";
	}
}
