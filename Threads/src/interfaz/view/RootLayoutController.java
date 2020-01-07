package interfaz.view;

import java.io.IOException;

import interfaz.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class RootLayoutController {
	
	@FXML
    private TextArea serverField;
	
	private MainApp mainApp;

    /**
     * Llamado por la main para acceder a referenciarlo desde aqu�.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    /**
     * Abre el channel
     */
    @FXML
    private void handleChannel() {
    	mainApp.handleClientStage();
    }
    
    /**
     * Abre un di�logo mostrando info acerda del proyecto
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("mIRC");
        alert.setHeaderText("Acerca del proyecto");
        alert.setContentText("Desarrollado para M09-UF3 por los alumnos Jordi Palomar y Jos� Mena.");

        alert.showAndWait();
    }

    /**
     * Cierra la aplicaci�n.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    /**
     * Arranca el servidor
     */
    @FXML
    private void handleServer() {
        mainApp.startServer();
    }
    
    /**
     * Refresca el campo de servidor.
     */
    public void refreshTxt(String txt) {
    	serverField.appendText(txt+"\n");
    }
    
}
