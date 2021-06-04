package projetoRC;

import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File WHITELIST = new File("lista-branca.txt");
	private static final File BLACKLIST = new File("lista-negra.txt");
	private static ArrayList<InetAddress> onlineUsers = new ArrayList<InetAddress>();
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
		private static DatagramPacket outPacket;
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
				onlineUsers.add(socket.getInetAddress());
				System.out.println("conectado com " + clientIP);	
			}else {
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
					System.out.println (clientIP+" "+threadName+": "+messageIn);
					if (messageIn.equals("1")) {
						messageOut = "Online Users:-&-";
						for(InetAddress user: onlineUsers)
							messageOut = messageOut + String.valueOf(onlineUsers.indexOf(user)) + " - " + user + "-&-";
					} else if (messageIn.equals("2")) {
						messageIn = input.readLine();
						System.out.println (clientIP+" "+threadName+" trying to send message to an online User.");
						sendMessageUDP(onlineUsers.get(Integer.parseInt(messageIn)),input.readLine());
						continue;
					} else if (messageIn.equals("3")) { 
						System.out.println (clientIP+" "+threadName+" Sending message to ALL online Users.");
						sendMessageUDP(null, input.readLine());
						continue;
					} else if (messageIn.equals("4")) {
						messageOut = getIPs(WHITELIST, "Allowed");
					} else if (messageIn.equals("5")) {
						messageOut = getIPs(BLACKLIST, "Banned");
					} else if (messageIn.equals("99")) {
						onlineUsers.remove(socket.getInetAddress());
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
			onlineUsers.remove(socket.getInetAddress());	
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
		private void sendMessageUDP(InetAddress receiverIP, String message) { 
			try{
				datagramSocket = new DatagramSocket();
				clientIP = socket.getInetAddress();
				if(receiverIP == null) {
					for(InetAddress user: onlineUsers) {
						System.out.println (clientIP.toString() + " ->  " + user + ": " + message);   
						outPacket =	new DatagramPacket(message.getBytes(), message.length(), user, PORTUDP);
						datagramSocket.send(outPacket);
					}
				}else {
					System.out.println(clientIP.toString() + " ->  " + receiverIP + ": " + message);   
					outPacket =	new DatagramPacket(message.getBytes(), message.length(), receiverIP, PORTUDP);
					datagramSocket.send(outPacket);
				}			
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