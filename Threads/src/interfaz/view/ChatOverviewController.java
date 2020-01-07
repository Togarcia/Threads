package interfaz.view;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Hilos.ClientMultiCast;
import Hilos.Alias;
import interfaz.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ChatOverviewController {
	
	@FXML
    private TableView<Alias> aliasTable;
    @FXML
    private TableColumn<Alias, String> nickColumn;

    
    @FXML
    private TextField msgField;
    @FXML
    private TextArea chatField;
    
    
    private final static int ECHO_PORT = 8;
    private final static int NUM_INTENTS = 5;
	private final static int SOCKET_TIMEOUT = 5000;
	private final static int BUFFER_MAX = 1024;
	
	private static String ip;
    
    private MainApp mainApp;

    /**
     * Constructor llamado antes del initialize()
     * 
     */
    public ChatOverviewController() {
    	
    }

    /**
     * Initializes el controlador. Se llama automáticamente una vez se cargó el fxml
     */
    @FXML
    private void initialize() {
    	
    	// Inicializa la tabla de nicks
     
    	nickColumn.setCellValueFactory(new PropertyValueFactory<>("nick"));
    	
    }

    /**
     * Llamado por la main para acceder a referenciarlo desde aquí.
     * 
     * @param mainApp
     * @throws UnsupportedEncodingException 
     */
    public void setMainApp(MainApp mainApp, String ip, String nick) throws UnsupportedEncodingException {
        this.mainApp = mainApp;
        this.ip = ip;
        
        ClientMultiCast hilo = new ClientMultiCast(this);
		DatagramSocket udpSocket = null;
		byte[] userByte = nick.getBytes("UTF8");
		hilo.start();
        
		try {		
			//Se prepara el socket para enviar
			InetAddress IPAddress = InetAddress.getByName(this.ip);
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
		
		/*try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
        
    }
    
    public void addAlias(List<Alias> q){
    	// Añade la lista observable a la tabla
    	ObservableList<Alias> aliasData = FXCollections.observableArrayList();
    	aliasData.setAll(q); 
    	aliasTable.setItems(aliasData);
    }
    
    public void refreshTxt(String txt){
    	//Carga un sonido de recibir un mensaje
    	String musicFile = new File("").getAbsolutePath();
        musicFile += "/src/msg.mp3";    

        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    	chatField.appendText(txt+"\n");
    }
    
    public void handleEnviar() throws UnsupportedEncodingException{
    	if(isInputValid()){
    		byte[] txByte = msgField.getText().getBytes("UTF8");
    		DatagramSocket udpSocket = null;
    		msgField.clear();
    		try {		
				//Se prepara el socket para enviar
				InetAddress IPAddress = InetAddress.getByName(ip);
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
    
    
   
    
    /**
     * Valida el input del usuario
     * 
     * @return true si el input es válido
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (msgField.getText() == null || msgField.getText().length() == 0) {
            errorMessage += "¡Introduce texto antes de enviar!\n"; 
        }else if (msgField.getText().contains("master>")){
        	errorMessage += "¡No puedes usar palabras reservadas!\n";
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Muestra mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Campo inválido");
            alert.setHeaderText("Por favor introduce datos válidos.");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }    
    
}
