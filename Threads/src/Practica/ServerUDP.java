package Practica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ServerUDP {

	private final static int ECHO_PORT = 8; //Puerto para recibir UDP
	//private final static String ip = "10.1.6.63"; // IP para recibir UDP de los clientes
	private final static int BUFFER_MAX = 1024; 
	final static String INET_ADDR = "224.0.0.7"; // IP para enviar multicast
	final static int PORT = 8888; //Puerto multicast
	final static String csvFile = "/src/test.csv";
	private static List<String> preguntas = new ArrayList<String>();
	private static Set<String> auxPreguntas = new LinkedHashSet<String>();
	private static List<Pregunta> trivial = new ArrayList<Pregunta>();
	private boolean comenzar = false;
	private boolean cierraParticipar = false;
	private List<Alias> alias = new ArrayList<Alias>();
	private boolean escribeHilo = false;
	private boolean iniciaTrivial = false;
	private boolean isPregunta = false;
	private boolean isRespuesta = false;
	private int numPregunta = -1;
	private boolean acertada = false;
	private boolean fallada = false;
	private boolean existeNick = false;
	
	private int numPreguntasTotal = 5;
	
	private static String version = "0.0.3";

	public ServerUDP() throws InterruptedException{
		try {
			InetAddress addr = InetAddress.getByName(INET_ADDR);
			
			ServerMasterTrivial hilo = new ServerMasterTrivial(this);
			ServerPregunta hiloPregunta = new ServerPregunta(this);
			
			cargarPreguntas();
			reiniciarTest(numPreguntasTotal);
			hilo.start();
			System.out.println("V. "+version);
			while (true) {
				//Se prepara el socket UDP que escucha
				
				DatagramSocket udpSocket = new DatagramSocket(ECHO_PORT);
				byte[] rxData = new byte[BUFFER_MAX];
				DatagramPacket packet = new DatagramPacket(rxData,
						rxData.length);
				//Recibe y muestra en el servidor
				udpSocket.receive(packet);
				String user = new String(packet.getAddress().toString());
				int userPort = packet.getPort();
				String rx = new String(packet.getData());
				boolean ipAlias = false;
				boolean enviar = false;
				
				String nombre = new String("");
				String rxMulti = "";
				
				if (alias.isEmpty()){
					Alias nuevoAlias = new Alias(user, userPort, filtro(rx));
					alias.add(nuevoAlias);
					nombre = filtro(rx);
					rxMulti = "¡Bienvenido a mIRC "+nombre+"!";
				}else{
					//Lleno
					
					for(Alias a : alias){
						
						if(a.getIp().equals(user)){
							ipAlias = true;
							enviar = true;
							nombre = a.getNick();
						}
					}
					
					if(!ipAlias){
						existeNick = false;
						for (Alias a : alias){
							if(a.getNick().equals(filtro(rx))){
								existeNick = true;
							}
						}
						if (!existeNick){
							Alias nuevoAlias = new Alias(user, userPort, filtro(rx));
							alias.add(nuevoAlias);
							nombre = filtro(rx);
							rxMulti = "¡Bienvenido a mIRC "+nombre+"!";
						}
						
					}
				}
				
				/***********/
				//Valorar inscripciones
				String mensaje = filtro(rx);
				if(comenzar && !cierraParticipar && !iniciaTrivial){
					if(mensaje.equals("!participar")){
						//Inscribe
						for(Alias p : alias){
							if(p.getIp().equals(user)){
								p.setEstado(true);
							}
						}
					}
					
				}
				//Cierra participaciones
				if(cierraParticipar){
					StringBuilder inscritos = new StringBuilder().append("\n");
					for(Alias p : alias){
						if(p.isEstado()){
							inscritos.append("["+p.getNick()+"] ");
						}
					}
					rx = filtro(rx) + inscritos;
					cierraParticipar = false;
					comenzar = false;
					iniciaTrivial = true;
				}
				
				/*******INICIA TRIVIAL*********/
				if(iniciaTrivial && !hiloPregunta.isAlive() && numPregunta < numPreguntasTotal){
					hiloPregunta.start();
				}
				if(iniciaTrivial && isPregunta && numPregunta < numPreguntasTotal){
					numPregunta++;
				}else if(iniciaTrivial && isRespuesta){
					for(Alias p : alias){
						if(p.getIp().equals(user) && p.isEstado()){
							String respuesta = filtro(rx);
							if (respuesta.equals(trivial.get(numPregunta).getRespuesta())){
								acertada = true;
								hiloPregunta.interrupt();
								p.sumaPuntos();
							}else{
								fallada = true;
								p.restaPuntos();
							}
						}
					}
					
				}
				
				
				/****************/
				
				//user = user + ":" + packet.getPort();
				
				/*for(Alias a : alias){
					if(a.getIp().equals(user) && a.getPuerto() == userPort){
						nombre.append(a.getNick());
					}
				}*/
				System.out.println("Paquete de: "+nombre);
				
				/**
				 * 
				 * Finaliza la partida
				 * 
				 */
				
				if(numPregunta >= numPreguntasTotal){
					StringBuilder txtEstado = new StringBuilder("");
					String ganador = "";
					int maxpuntos = -10000;
					for(Alias p : alias){
						if(p.isEstado()){
							if(p.getPuntuacion() > maxpuntos){
								maxpuntos = p.getPuntuacion();
								ganador = p.getNick();
							}
							txtEstado.append(" "+p.getNick()+"->"+p.getPuntuacion()+"p");
						}
					}
					
					rxMulti = new StringBuilder("La partida terminó. Se cierra el servidor.")
							.append(" Resultado final:"+txtEstado)
							.append("\nLa mayor puntuación es de: "+maxpuntos+" puntos de "+ganador)
							.toString();
					
					enviaMulti(rxMulti, addr);
					break;
				}
				
				/**
				 * Salidas multicast
				 * 
				 * */
				if(existeNick){
					rxMulti = new StringBuilder("El nombre ya está registrado, no puede usarse.").toString();
					existeNick = false;
				}else if(acertada){
					StringBuilder txtEstado = new StringBuilder("");
					for(Alias p : alias){
						if(p.isEstado()){
							txtEstado.append(" "+p.getNick()+"->"+p.getPuntuacion()+"p");
						}
					}
					rxMulti = new StringBuilder("El participante "+nombre+" acertó.")
							.append("\nEstado partida: "+txtEstado.toString())
							.toString();
					
					//Sacar mensaje de estado también
					acertada = false;
					isPregunta = true;
				}else if(fallada){
					rxMulti = new StringBuilder("El participante "+nombre+" NO acertó.").toString();
					fallada = false;
				}else if(isPregunta){
					rxMulti = new StringBuilder(trivial.get(numPregunta).toString()).toString();
					isPregunta = false;
					isRespuesta = true;
				}else if(!escribeHilo && rxMulti.equals("") && !mensaje.equals("!participar")){
					rxMulti = new StringBuilder(nombre).append(">").append(rx).toString();
				}else if(escribeHilo){
					rxMulti = new StringBuilder().append(rx).toString();
					
					if(isRespuesta){
						//Significa que contesta el ServerPregunta al terminar el tiempo
						StringBuilder txtEstado = new StringBuilder("");
						for(Alias p : alias){
							if(p.isEstado()){
								txtEstado.append(" "+p.getNick()+"->"+p.getPuntuacion()+"p");
							}
						}
						rxMulti = filtro(rxMulti) + "\nLa respuesta era: " + trivial.get(numPregunta).getRespuesta()
						+ "\nEstado partida: "+txtEstado.toString();
					}
					
					escribeHilo = false;
					isRespuesta = false;
				}else if(mensaje.equals("!participar")){
					rxMulti = new StringBuilder("El usuario ").append(nombre).append(" participa.").toString();
				}
				
				
				System.out.println("Data: "+rx);
				if(rx.equals("fin") && user.equals("master")){
					
				}
				if(enviar){
					//Preparamos el serversocket para realizar multicast del paquete recibido
					enviaMulti(rxMulti, addr);
				}else {
					enviaMulti(rxMulti, addr);
					
				}
				
				//Cerramos el socket udp que escucha
				udpSocket.close();
				
			}
		} catch (SocketException e) {
			System.out.println("Socket error: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		try {
			ServerUDP server = new ServerUDP();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static void cargarPreguntas(){
		BufferedReader br = null;
        String line = "";
        
        String archivo = new File("").getAbsolutePath();
		
		try {

            br = new BufferedReader(new FileReader(archivo+csvFile));
            while ((line = br.readLine()) != null) {
            	preguntas.add(line);
            }
            
            /*for(String p : preguntas){
            	System.out.println(p);
            	String[] pa = p.split(",");
            	System.out.println(pa[0]);
            	System.out.println(pa[1]);
            	System.out.println(pa[2]);
            	System.out.println(pa[3]);
            	System.out.println(pa[4]);
            	System.out.println(pa[5]);
            }*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public static void reiniciarTest(int total){
		int numPreguntas = total;
        Random rn = new Random();
		
		System.out.println("Escogiendo preguntas...");
		auxPreguntas.clear();
        
        while(auxPreguntas.size() < numPreguntas){
        	int next = rn.nextInt() % preguntas.size();
        	next = Math.abs(next);
        	auxPreguntas.add(preguntas.get(next));
        }
        
        for(String linea : auxPreguntas){
        	String [] lineaPreparada = linea.split(";");
        	Pregunta preguntaPreparada = new Pregunta(lineaPreparada[0], 
        			lineaPreparada[1],
        			lineaPreparada[2],
        			lineaPreparada[3], 
        			lineaPreparada[4],
        			lineaPreparada[5]);
        	trivial.add(preguntaPreparada);
        }
        
       /* for(Pregunta trivia : trivial){
        	System.out.println(trivia.toString());
        }*/
	}

	public void setComenzar(boolean estado){
		this.comenzar = estado;
	}
	
	public boolean getComenzar(){
		return comenzar;
	}
	
	public int getEchoPort(){
		return ECHO_PORT;
	}
	
	public boolean getCierraParticipar(){
		return cierraParticipar;
	}
	
	public void setEscribeHilo(boolean estado){
		escribeHilo = estado;
	}
	
	public void setCierraParticipar(boolean estado){
		cierraParticipar = estado;
	}
	
	public void setIsPregunta(boolean estado){
		isPregunta = estado;
	}
	
	public String getAlias(String user){
		String cadena = "";
		for(Alias a : alias){
			if(a.getIp().equals(user)){
				cadena = a.getNick();
			}
		}
		return cadena;
	}
	
	public void enviaMulti(String rxMulti, InetAddress addr) throws InterruptedException{
		try (DatagramSocket serverSocket = new DatagramSocket()) {
            DatagramPacket msgPacket = new DatagramPacket(rxMulti.getBytes(),
                    rxMulti.getBytes().length, addr, PORT);
            serverSocket.send(msgPacket);
            System.out.println("Servidor envia mensaje: " + rxMulti);
            //serverSocket.close();
            Thread.sleep(500);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	public String getGanador(){
		
		return "";
	}
}
