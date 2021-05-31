package projetoRC;

import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File WHITELIST = new File("lista-branca.txt");
	private static final File BLACKLIST = new File("lista-negra.txt");
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
			// CReate UDP CLIENT!!!!
		}		
	}	
	
	public static class EchoClientThread implements Runnable{
//		private static final int PORTUDP = 9031;
//		private static DatagramSocket datagramSocket;
//		private static DatagramPacket outPacket;
//		private static InetAddress clientInnetAddressIP;
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
			
			try {				
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
				PrintStream output = new PrintStream(socket.getOutputStream(),true);
				String messageIn = "0", messageOut = null;
				do {			
					System.out.println (clientIP+": "+threadName+": "+messageIn);
					if (messageIn.equals("0")){
						messageOut = getMenu();
					} else if (messageIn.equals("1")) { 	
						messageOut = "Online Users";
					} else if (messageIn.equals("2")) {
						messageOut = "Send Message to user";
					} else if (messageIn.equals("3")) {
						messageOut = "Send General User";	
					} else if (messageIn.equals("4")) {
						messageOut = getIPs(WHITELIST, "Allowed");
					} else if (messageIn.equals("5")) {
						messageOut = getIPs(BLACKLIST, "Banned");
					} else if (messageIn.equals("99")) {
						break;
					} else {
						messageOut = "Not a Valid option";
					}	
					output.println(messageOut);
				} while ((messageIn = input.readLine()) !=null);
				input.close(); 
				output.close();
				socket.close();							
			}
			catch (Exception ex){
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
			System.out.println("Socket Closed :" + clientIP);
		}
		private String getMenu() {
			return "MENU CLIENT-&-0  - Menu Inicial-&-1  - Listar utilizadores online-&-2  - Enviar mensagem a um utilizador-&-3  - Enviar mensagem a todos os utilizadores-&-4  - lista branca de utilizadores-&-5  - lista negra de utilizadores-&-99 – Sair";
		}
	
		private String getIPs(File file, String name) {
			String result = name +"IP Addresses: -&-";
			try { 
	    		Scanner sWhite = new Scanner(file);
	    		if(!sWhite.hasNextLine())
	    			result = "There are no" + name + "IP Addresses";
	    		while(sWhite.hasNextLine())
	    			result = result + " - IP Addresss: " + sWhite.nextLine() + "-&-";
	    		sWhite.close();
			}catch(FileNotFoundException e) {
	    		System.err.println(e);
	    		return "Sorry we could't retrieve any information... :(";
	    	}
			return result;
		}
//		private void sendMessageUDP(String message) {
//			try {
//				clientInnetAddressIP = socket.getInetAddress();
//				datagramSocket = new DatagramSocket();
//				outPacket =	new DatagramPacket(message.getBytes(),message.length(), clientInnetAddressIP, PORTUDP);
//				datagramSocket.send(outPacket);
//			}catch (Exception ex){
//				ex.printStackTrace();
//			}		
//		}
	}
    public static int rand(int min,int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    public static boolean checkValidIP(String ipAddress)  {
    	try {
    		Scanner sBlack = new Scanner (BLACKLIST);
    		while(sBlack.hasNextLine()) {
    			if(ipAddress.contains(sBlack.nextLine())) {
    				sBlack.close();
    				return false;
    			}
    		}
    		sBlack.close();
    		Scanner sWhite = new Scanner(WHITELIST);
	    	if(!sWhite.hasNextLine()) {
	    		sWhite.close();
	    		return true;
	    	}
	    	while(sWhite.hasNextLine()) {
	    		String linha = sWhite.nextLine();
    			if(ipAddress.contains(linha)) {
    				sWhite.close();
    				return true;
    			}
	    	}
	    	sWhite.close();
	    	return false;
    	}catch(FileNotFoundException e) {
    		System.err.println(e);
    		return false;
    	}
    }
}