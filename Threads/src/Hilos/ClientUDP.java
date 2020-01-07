package Hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ClientUDP {
	private final static int ECHO_PORT = 8;
	
	//private final static String ECHO_IP = "10.1.6.63"; // IP para udp del servidor
	private final static int NUM_INTENTS = 5;
	private final static int SOCKET_TIMEOUT = 5000;
	private final static int BUFFER_MAX = 1024;

	private final static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		//Se prepara e inicia el hilo del cliente que escuchará en multicast
		/**
		 * No se usa esta clase en el programa con interficie
		 */
		///ClientMultiCast hilo = new ClientMultiCast();
		
		
		DatagramSocket udpSocket = null;
		
		
		//System.out.println("Escribe IP del servidor: ");
		//String echoIP = sc.nextLine();
		String echoIP = "10.1.6.63";
		System.out.println("Registrate: ");
		String user = sc.nextLine();
		while(user.equals("master")){
			System.out.println("Nombre reservado, vuelve a escribir tu nick: ");
			user = sc.nextLine();
		}
		byte[] userByte = user.getBytes();
		///hilo.start();
		
		try {		
			//Se prepara el socket para enviar
			InetAddress IPAddress = InetAddress.getByName(echoIP);
			udpSocket = new DatagramSocket();
			
			udpSocket.setSoTimeout(SOCKET_TIMEOUT);
			
			//Se prepara paquete
			DatagramPacket userPacket = new DatagramPacket(userByte, userByte.length, IPAddress, ECHO_PORT);
			
			//Se fija y envía el texto
			userPacket.setData(userByte);
			
			udpSocket.send(userPacket);
			
			
			
			
			
		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		//Entramos en el bucle en el que el usuario escribirá
		while(true){

			//System.out.print("[Escribe]>");
			String txStr = sc.nextLine();
			byte[] txByte = txStr.getBytes();
	
			try {		
				//Se prepara el socket para enviar
				InetAddress IPAddress = InetAddress.getByName(echoIP);
				udpSocket = new DatagramSocket();
				
				udpSocket.setSoTimeout(SOCKET_TIMEOUT);
				
				//Se prepara paquete
				DatagramPacket txPacket = new DatagramPacket(txByte, txByte.length, IPAddress, ECHO_PORT);
				
				
				//Se fija y envía el texto
				txPacket.setData(txByte);
				
				udpSocket.send(txPacket);
				
				
				
			} catch (SocketException e) {
				System.out.println("Socket error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IO error: " + e.getMessage());
				e.printStackTrace();
			} 
		}
	}

}
