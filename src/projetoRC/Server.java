package projetoRC;

import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File LISTABRANCA = new File("lista-branca.txt");
	private static final File LISTANEGRA = new File("lista-negra.txt");
	private static final int PORT = 7142;
    
	public static void main(String args[]) throws Exception {		
		ServerSocket server = new ServerSocket(PORT);
		InetAddress myIPaddress = InetAddress.getLocalHost();
		System.out.println(myIPaddress.toString() + ":" + PORT + "/TCP");
		Socket client = null;
		while (true){
			client = server.accept();
			Thread t = new Thread(new EchoClientThread(client));                             
			t.start();
		}		
	}	
	
	public static class EchoClientThread implements Runnable{
		private static final int PORTUDP = 9031;
		private static DatagramSocket datagramSocket;
		private static DatagramPacket outPacket;
		private static InetAddress clientInnetAddressIP;
		private Socket socket;
		public EchoClientThread(Socket socket) {
			this.socket = socket;
		}
		public void run() {	
			String threadName = Thread.currentThread().getName();
			String clientIP = socket.getInetAddress().toString();
			if (checkValidIP(clientIP)) {
				// create log
				System.out.println("conectado com " + clientIP);		
				try { //Print Menu(UDP)
					sendMessageUDP(getMenu());
					System.out.println("Im here Dude!");
				}catch (Exception ex){
					ex.printStackTrace();
				}		
			}else {
				// create log
				try {
					socket.close();
					System.err.println("conexao rejeitada e socket fechado" + clientIP);
					return;
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			try { // Receive Options (TCP) -> Send Response UDP
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
				String messageIn = null;
				while ((messageIn = input.readLine()) !=null) {				
					System.out.println (clientIP+": "+threadName+": "+messageIn);
					if (messageIn.contains("99")){ // Client Exit -> Change Online Status
						System.out.println("...");
						System.out.println("Socket Closed :" + clientIP);
						socket.close();
						break;
					}else if (messageIn.contains("0")) {
						sendMessageUDP(getMenu());
					} else if (messageIn.contains("1")) {
						sendMessageUDP("You choose the option 1");
					} else if (messageIn.contains("2")) {
						sendMessageUDP("You choose the option 2");
					} else if (messageIn.contains("3")) {
						sendMessageUDP("You choose the option 3");		
					} else if (messageIn.contains("4")) {
						sendMessageUDP("You choose the option 4");
					} else if (messageIn.contains("5")) {
						sendMessageUDP("You choose the option 5");
					} else {
						sendMessageUDP("Not a valid Option");
					}
				} 
				input.close(); 							
			}catch (Exception ex){
				try {
					socket.close();
					System.out.println("Socket Closed :" + clientIP);
					return;
				}
				catch (Exception e) {
					System.err.println("Somehting went wrong... can't close the socket");
				}
				ex.printStackTrace();
			}
		}
		private String getMenu() {
			return "MENU CLIENTE\n0  - Menu Inicial\n1  - Listar utilizadores online\n2  - Enviar mensagem a um utilizador\n3  - Enviar mensagem a todos os utilizadores\n4  - lista branca de utilizadores\n5  - lista negra de utilizadores\n99 – Sair\n";
		}
		private void sendMessageUDP(String message) {
			try {
				clientInnetAddressIP = socket.getInetAddress();
				datagramSocket = new DatagramSocket();
				outPacket =	new DatagramPacket(message.getBytes(),message.length(), clientInnetAddressIP, PORTUDP);
				datagramSocket.send(outPacket);
				datagramSocket.close();
			}catch (Exception ex){
				ex.printStackTrace();
			}		
		}
	}
    public static int rand(int min,int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    public static boolean checkValidIP(String ipAddress)  {
    	try {
    		Scanner sNegra = new Scanner (LISTANEGRA);
    		while(sNegra.hasNextLine()) {
    			if(ipAddress.contains(sNegra.nextLine())) {
    				sNegra.close();
    				return false;
    			}
    		}
    		sNegra.close();
    		Scanner sBranca = new Scanner(LISTABRANCA);
	    	if(!sBranca.hasNextLine()) {
	    		sBranca.close();
	    		return true;
	    	}
	    	while(sBranca.hasNextLine()) {
	    		String linha = sBranca.nextLine();
    			if(ipAddress.contains(linha)) {
    				sBranca.close();
    				return true;
    			}
	    	}
	    	sBranca.close();
	    	return false;
    	}catch(FileNotFoundException e) {
    		System.err.println(e);
    		return false;
    	}
    }
}