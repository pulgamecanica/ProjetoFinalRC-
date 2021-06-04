package projetoRC;

import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File WHITELIST = new File("lista-branca.txt");
	private static final File BLACKLIST = new File("lista-negra.txt");
	private static ArrayList<String> onlineUsers = new ArrayList<String>();
	private static final int PORT = 7142;
    
	public static void main(String args[]) throws Exception {		
		ServerSocket server = new ServerSocket(PORT);
		InetAddress myIPaddress = InetAddress.getLocalHost();
		System.out.println(myIPaddress.toString() + ":" + PORT + "/TCP");
		Socket client = null;
		try {
			while (true){
				client = server.accept();
				Thread t = new Thread(new EchoClientThread(client));                             
				t.start();
			}
		}catch (Exception e) {
			System.err.println(e);
			server.close();
		}
	}	
	public static class EchoClientThread implements Runnable{
		private Socket socket;
		private static DatagramSocket datagramSocket;
		private static DatagramPacket inPacket, outPacket;
		private static byte[] buffer;
		private static InetAddress clientIP; 
		private static final int PORTUDP = 9031;
		
		public EchoClientThread(Socket socket) {
			this.socket = socket;
		}
		public void run() {	
			String threadName = Thread.currentThread().getName();
			String clientIP = socket.getInetAddress().toString();
			if (checkValidIP(clientIP)) {
				// create log
				onlineUsers.add(clientIP);
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
				String messageIn = null, messageOut = null;
				while (true) {
					messageIn = input.readLine();
					System.out.println (clientIP+": "+threadName+": "+messageIn);
					if (messageIn.equals("1")) {
						messageOut = "Online Users:-&-";
						for(String user: onlineUsers)
							messageOut = messageOut + String.valueOf(onlineUsers.indexOf(user)) + " - " + user + "-&-";
					} else if (messageIn.equals("2")) {
						sendMessageUDP();
						continue;
					} else if (messageIn.equals("3")) {
						messageOut = "Send General User";	
					} else if (messageIn.equals("4")) {
						messageOut = getIPs(WHITELIST, "Allowed");
					} else if (messageIn.equals("5")) {
						messageOut = getIPs(BLACKLIST, "Banned");
					} else if (messageIn.equals("99")) {
						onlineUsers.remove(clientIP);
						break;
					} else {
						messageOut = "Not a Valid option";
					}	
					output.println(messageOut);
				}
				input.close(); 
				output.close();
				socket.close();							
			}
			catch (Exception ex){
				try {
					socket.close();
					System.out.println("Something went wrong.... Socket Closed :" + clientIP + " | Err -> " + ex);
					return;
				}
				catch (Exception e) {
					System.err.println("Somehting went wrong... can't close the socket");
				}
				ex.printStackTrace();
			}
			onlineUsers.remove(clientIP);	
			System.out.println("Socket Closed :" + clientIP);
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
		private void sendMessageUDP() {
			try{
				datagramSocket = new DatagramSocket(PORTUDP);
				String messageIn = null, messageOut = null;
				do{
					buffer = new byte[256];
					inPacket = new DatagramPacket(buffer,buffer.length);
					datagramSocket.receive(inPacket);
					clientIP = inPacket.getAddress();
					int clientPort =inPacket.getPort();
					messageIn =	new String(inPacket.getData(),0,inPacket.getLength()); 
					System.out.println (clientIP.toString() + ": " + messageIn);
					messageOut = messageIn;      
					outPacket =	new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientIP, clientPort);
					datagramSocket.send(outPacket);
				} while (messageIn == null);			
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			finally {
				datagramSocket.close();
			}
		}
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