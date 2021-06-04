package projetoRC;

import java.net.*;

public class ClientUDP {
	private static InetAddress host;
	private static final int PORT = 9031;
	private static DatagramSocket datagramSocket;
	private static DatagramPacket inPacket;
	private static byte[] buffer;

	public static void main(String[] args) throws Exception {
		if (args.length !=1){
			System.err.println ("Usage: java UDPClient <host>");
			System.exit(1);
		}	
		host = InetAddress.getByName(args[0]);			
		accessServer();
		System.exit(0);
	}

	private static void accessServer() {
		try {
			datagramSocket = new DatagramSocket(PORT, host);
			String messageIn = null;
			do {
				buffer = new byte[256];
				inPacket = new DatagramPacket(buffer, buffer.length);					
				datagramSocket.receive(inPacket);
				messageIn = new String(inPacket.getData(),0, inPacket.getLength());
				System.out.println(messageIn);
			} while(true);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			datagramSocket.close();
		}
	}
}
