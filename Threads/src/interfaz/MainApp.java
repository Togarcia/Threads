package interfaz;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Hilos.Alias;
import Hilos.ServerUDP;
import interfaz.view.ChatOverviewController;
import interfaz.view.ClienteOverviewController;
import interfaz.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private Stage clientStage;
	private BorderPane rootLayout;
	private AnchorPane rootClient;
	
	private ObservableList<Alias> aliasData = FXCollections.observableArrayList();
	
	
	private String ip;
	private Integer puerto;
	private ServerUDP server;
	
	private RootLayoutController rootController;
	private ChatOverviewController chatController;
	
	
	
	public MainApp() {
        // Add some sample data
	}
	
	/**
     * Retorna la lista de Alias actualizada
     * @return
     */
    @SuppressWarnings("unchecked")
	public ObservableList<Alias> getAliasData() {
    	List<Alias> q = server.getAliasList();
    	aliasData.setAll(q);
		return aliasData;
    }
    
    
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("mIRC");

        initRootLayout();
        showInitOverview();
        
        //Música de fondo
        String musicFile = new File("").getAbsolutePath();
        musicFile += "/src/KH.mp3";    

        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        
	}
	
	/**
     * Inicializa el menú superior.
     */
    public void initRootLayout() {
        try {
            // Carga el fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Muestra la escena en el menú.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            // Se da acceso al controlador del main
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la vista inicial.
     */
    public void showInitOverview() {
        try {
            // Carga el fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/InitOverview.fxml"));
            AnchorPane initOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(initOverview);
            rootController = loader.getController();
            rootController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * Muestra la pantalla de channel
     * 
     */
    public void showChannelOverview(String ip, String nick){
    	try {
    		   
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ChatOverview.fxml"));
            AnchorPane channelOverview = (AnchorPane) loader.load();
            Scene scene = new Scene(channelOverview);
            clientStage.setScene(scene);
    
            chatController = loader.getController();
            chatController.setMainApp(this, ip, nick);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }
    
        
   
    /**
     * Retorna el main stage
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Retorna el cliente stage
     * @return
     */
    public Stage getClientStage() {
        return clientStage;
    }


	public static void main(String[] args) {
		launch(args);
	}
	

	/**
	 * Refresca el textarea usando su controlador
	 * 
	 * @param txt
	 */
	public void insertaTxtServer(String txt){
		rootController.refreshTxt(txt);
	}
	
	/**
	 * Inicia el cliente stage para requerir datos
	 * 
	 */
	public void handleClientStage() {
        clientStage = new Stage(); // new stage
        clientStage.initModality(Modality.APPLICATION_MODAL);
        clientStage.initOwner(primaryStage);
        
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ClienteOverview.fxml"));
			rootClient = (AnchorPane) loader.load();
			Scene scene = new Scene(rootClient);
	        clientStage.setScene(scene);
	        // Cargamos controlador y damos acceso del main
	        ClienteOverviewController controller = loader.getController();
	        controller.setMainApp(this);
	        clientStage.setTitle("Cliente");
	        clientStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
	
	/**
	 * Arranca el hilo del servidor
	 */
	public void startServer(){
		try {
	    	server = new ServerUDP(this);
	    	server.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
