import java.io.*;
import java.net.*;
import java.util.*;

public class Client {	
	
	private static final int PORT = 7142;
	private static final int PORTUDP = 9031;
	private static BufferedReader input;
	private static PrintStream output;
	private static DatagramSocket datagramSocket;
	private static DatagramPackets inPacket;
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
		System.out.println(getMenu());
		while(true) {
			Scanner scan = new Scanner (System.in); 
			System.out.print(">");  			
			messageOut = scan.nextLine();
			output.println(messageOut);
			if (messageOut.equals("99")){
				System.out.println("a sair..");
				break;
			}else if (messageOut.equals("0")){
				System.out.println(getMenu());
			}
			messageIn = input.readLine();
			if(messageIn != null) {
				String stringVec[] = messageIn.split("-&-");
				messageIn = "";
				for(int i = 0; i < stringVec.length; i++)
					messageIn = messageIn + stringVec[i] + "\n";
			}
			System.out.println(messageIn);
		}
		input.close();
		output.close();
		client.close();	
	}
	private static String getMenu() {
		return "MENU CLIENT\n0  - Menu Inicial\n1  - Listar utilizadores online\n2  - Enviar mensagem a um utilizador\n3  - Enviar mensagem a todos os utilizadores\n4  - lista branca de utilizadores\n5  - lista negra de utilizadores\n99 - Sair";
	}
}
