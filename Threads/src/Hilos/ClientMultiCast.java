package Hilos;

	import java.io.IOException;
	import java.net.DatagramPacket;
	import java.net.InetAddress;
	import java.net.MulticastSocket;
	import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import interfaz.view.ChatOverviewController;

	public class ClientMultiCast extends Thread {
	    final static String INET_ADDR = "224.0.0.7"; //IP multicast
	    final static int PORT = 8888; //Puerto multicast
	    InetAddress address;
	    
	    ChatOverviewController chat;
	    
	    public ClientMultiCast(ChatOverviewController chat) {
	    	this.chat = chat;
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
		                String msg = new String(buf, 0, buf.length); //cambio utf8
		                //Procesamos para la interfaz
		                String text = filtro(msg);
		                if (text.startsWith("master>")){
		        			text = text.substring(7, text.length());
		        			String[] array = text.split(",");
		        			List<Alias> a = new ArrayList<Alias>();
		        			for (String s : array){
		        				a.add(new Alias(null, 0, s));
		        			}
		        			chat.addAlias(a);
		        		}else{
		        			//System.out.println(msg);
		        			chat.refreshTxt(msg);
		        		}
		                
		                
		            }
		        }catch (IOException ex) {
		            ex.printStackTrace();
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
	}

