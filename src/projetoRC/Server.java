import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File WHITELIST = new File("lista-branca.txt");
	private static final File BLACKLIST = new File("lista-negra.txt");
	private static ArrayList<String> onlineUsers = new ArrayList();
	private static final int PORT = 7142;
	private static final int PORTUDP = 9031;
	private static DatagramSocket datagramSocket;
	private static DatagramPackets outPacket;
    
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
				while ((messageIn = input.readLine()) !=null) {			
					System.out.println (clientIP+": "+threadName+": "+messageIn);
					if (messageIn.equals("1")) {
						messageOut = "Online Users:-&-";
						for(String user: onlineUsers)
							messageOut = messageOut + String.valueOf(onlineUsers.indexOf(user)) + " - " + user + "-&-";
					} else if (messageIn.equals("2")) {
						messageOut = "Send Message to user";
					} else if (messageIn.equals("3")) {
						messageOut = "Send General User";	
					} else if (messageIn.equals("4")) {
						messageOut = getIPs(WHITELIST, "Allowed");
					} else if (messageIn.equals("5")) {
						messageOut = getIPs(BLACKLIST, "Banned");
					} else if (messageIn.equals("99")) {
						onlineUsers.remove(clientIP);
						break;
					} else if (!messageIn.equals("0")) {
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
					System.out.println("Socket Closed :" + clientIP);
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