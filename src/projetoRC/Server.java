package projetoRC;

import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class Server {
	
	private static final int port = 6500;
	static String[] sentences = new String[] { "Bom dia", "Bem disposto?", "Ola", "Boas", "Viva" };
    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    static DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    
	public static void main(String args[]) throws Exception {		
		ServerSocket server = new ServerSocket(port);
		InetAddress myIPaddress = InetAddress.getLocalHost();
		System.out.println(myIPaddress.toString() + ":" + port + "/TCP");
		Socket client = null;
		while (true){
			client = server.accept();
			System.out.println("nova conexao..");
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
			String cientIP = socket.getInetAddress().toString();			
			System.out.println("conectado com " + cientIP);		
			try {				
				BufferedReader input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));			
				PrintStream output = new PrintStream(
						socket.getOutputStream(),true);
				String messageIn = null, messageOut = null;
				while ((messageIn = input.readLine()) !=null) {				
					System.out.println (cientIP+": "+threadName+": "+messageIn);
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
				ex.printStackTrace();
			}					
		}  
	}
	
    public static int rand(int min,int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }	
}


