package Practica;

	import java.io.IOException;
	import java.net.DatagramPacket;
	import java.net.InetAddress;
	import java.net.MulticastSocket;
	import java.net.UnknownHostException;

	public class ClientMultiCast extends Thread {
	    final static String INET_ADDR = "224.0.0.7"; //IP multicast
	    final static int PORT = 8888; //Puerto multicast
	    InetAddress address;
	    
	    public ClientMultiCast() {
	    	try {
				address = InetAddress.getByName(INET_ADDR);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	    
	    public void run(){
			while(true){
				try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
		        	//Se une al grupo multicast del servidor
		        	clientSocket.joinGroup(address);
		            while (true) {
		            	byte[] buf = new byte[256];
		            	//Si recibe datos del servidor, la mostrará
		                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		                clientSocket.receive(msgPacket);
		                String msg = new String(buf, 0, buf.length);
		                System.out.println(msg);
		            }
		        }catch (IOException ex) {
		            ex.printStackTrace();
		        }
				
			}
		}

	   /* public static void main(String[] args) throws UnknownHostException {
	    		        
	        
	        //crea un Multicast socket (que permet a altres sockets/programes adherir-se
	        
	    }*/
	}

