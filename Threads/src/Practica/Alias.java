package Practica;

import java.util.List;

public class Alias {
	
	String ip;
	int puerto;
	String nick;
	boolean estado;
	int puntuacion;
	
	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public int getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(int puntuacion) {
		this.puntuacion = puntuacion;
	}
	
	public void restaPuntos(){
		puntuacion--;
	}
	
	public void sumaPuntos(){
		puntuacion += 3;
	}

	public Alias(String ip, int puerto, String nick){
		this.ip = ip;
		this.puerto = puerto;
		this.nick = nick;
		this.puntuacion = 0;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
}
