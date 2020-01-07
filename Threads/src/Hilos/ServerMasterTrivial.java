package Hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import interfaz.MainApp;

public class ServerMasterTrivial extends Thread {
	
	/*final static String INET_ADDR = "224.0.0.3"; //IP multicast
    final static int PORT = 8888; //Puerto multicast
    InetAddress address;*/
	
	private final static int ECHO_PORT = 8;
	private final static int NUM_INTENTS = 5;
	private final static int SOCKET_TIMEOUT = 5000;
	private final static int BUFFER_MAX = 1024;
	
	private ServerUDP server;
	
    public ServerMasterTrivial(ServerUDP server) {
    	this.server = server;
    	
    }
	
	
	public void run(){
		
		
		while(true){
			try {
				enviarUDP("master");
				Thread.currentThread().sleep(10000);
				if (!server.getComenzar()){
					server.setEscribeHilo(true);
					server.setComenzar(true);
					
					enviarUDP("Comienza la partida. Se abren inscripciones. Escribe '!participar'.");
					//Lanza salida
					try {
						Thread.currentThread().sleep(30000);
						server.setEscribeHilo(true);
						server.setCierraParticipar(true);
						enviarUDP("Se acabaron las inscripciones. Participantes:");
						this.interrupt();
						break;
					}catch (InterruptedException e) {
						enviarUDP("fin");
						//e.printStackTrace();
					}
				}
				/*ServerCierraParticipar hilo = new ServerCierraParticipar(server);
				hilo.start();*/
				
			} catch (InterruptedException e) {
				System.out.println("Hilo terminado.");
				//e.printStackTrace();
			} 
		}
	}
	
	public static String filtro(String cadena){
		StringBuilder retorna = new StringBuilder();
		for(char c : cadena.toCharArray()){
			if(c != '\u0000'){
				retorna.append(c);
			}
		}
		return retorna.toString();
	}
	
	public void enviarUDP(String cadena){
		DatagramSocket udpSocket = null;
		
		byte[] salida = cadena.getBytes();
		try {		
			//Se prepara el socket para enviar
			InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
			udpSocket = new DatagramSocket();
			udpSocket.setSoTimeout(SOCKET_TIMEOUT);
			//Se prepara paquete
			DatagramPacket userPacket = new DatagramPacket(salida, salida.length, IPAddress, ECHO_PORT);
			//Se fija y envía el texto
			userPacket.setData(salida);
			udpSocket.send(userPacket);
		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
