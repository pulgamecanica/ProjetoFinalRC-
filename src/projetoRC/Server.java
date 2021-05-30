package projetoRC;

import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class Server {
	private static final File LISTABRANCA = new File("lista-branca.txt");
	private static final File LISTANEGRA = new File("lista-negra.txt");
	private static final int PORT = 7142;
	static String[] sentences = new String[] { "Bom dia", "Bem disposto?", "Ola", "Boas", "Viva" };
    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    static DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    
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
		private Socket socket;
		public EchoClientThread(Socket socket) {
			this.socket = socket;
		}
		public void run() {	
			String threadName = Thread.currentThread().getName();
			String clientIP = socket.getInetAddress().toString();
			System.out.println("Recieved IP ADDRESS: " + clientIP);
			if (checkValidIP(clientIP)) {
				// create log
				System.out.println("conectado com " + clientIP);		
				try {
					///UDP Request!
					PrintStream output = new PrintStream(socket.getOutputStream(),true);
					output.println("---MENU---");
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
			
			try {				
				BufferedReader input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));			
				PrintStream output = new PrintStream(
						socket.getOutputStream(),true);
				String messageIn = null, messageOut = null;
				while ((messageIn = input.readLine()) !=null) {				
					System.out.println (clientIP+": "+threadName+": "+messageIn);
					if (messageIn.equalsIgnoreCase("tchau")){
						output.println("..");
						break;
					}	
					if (messageIn.equalsIgnoreCase("frase")) { 	
					    output.println(sentences[rand(0,sentences.length-1)]);
					} else if (messageIn.equalsIgnoreCase("listar")) {
						messageOut = Arrays.toString(sentences); 
						output.println(messageOut);
					} else if (messageIn.equalsIgnoreCase("data")) {
		                Date date = new Date();
						messageOut = dateFormat.format(date); 
						output.println(messageOut);	
					} else if (messageIn.equalsIgnoreCase("horas")) {
		                Date date = new Date();
						messageOut = timeFormat.format(date);
						output.println(messageOut);		
					} else {
						output.println("comando desconhecido!");
					}	
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


