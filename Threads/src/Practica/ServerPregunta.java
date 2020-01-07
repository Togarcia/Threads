package Practica;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import interfaz.MainApp;

public class ServerPregunta extends Thread {
	
	private final static int ECHO_PORT = 8;
	private final static int NUM_INTENTS = 5;
	private final static int SOCKET_TIMEOUT = 5000;
	private final static int BUFFER_MAX = 1024;

	private ServerUDP server;
	
    public ServerPregunta(ServerUDP server) {
    	this.server = server;
    	
    }
    
    public void run(){
    	while(true){
    		try {
    			Thread.currentThread().sleep(5000);
    			server.setIsPregunta(true);
    			enviarUDP(" ");
				Thread.currentThread().sleep(10000);
				server.setEscribeHilo(true);
				enviarUDP("Se terminó el tiempo.");
				
    		}catch (InterruptedException e) {
				System.out.println("Hilo terminado.");
				//e.printStackTrace();
			}
    	}
    	
    }
    
    public void enviarUDP(String cadena){
		DatagramSocket udpSocket = null;
		//String out = "Hilo terminado.";
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
