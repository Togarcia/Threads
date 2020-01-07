package Practica;

public class ServerCierraParticipar extends Thread {
	
	private ServerUDP server;
	
	public ServerCierraParticipar(ServerUDP server) {
    	this.server = server;
    }

	public void run(){
		while(true){
			try {
				Thread.currentThread().sleep(30000);
				server.setCierraParticipar(true);
				this.interrupt();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
