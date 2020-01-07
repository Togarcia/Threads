package interfaz.view;

import interfaz.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class ClienteOverviewController {
	
	@FXML
    private TextField ipField;
	
	@FXML
    private TextField nickField;
	
	private MainApp mainApp;

    /**
     * Llamado por la main para acceder a referenciarlo desde aquí.
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
    private void handleEnviar() {
    	if (isInputValid()){
    		mainApp.showChannelOverview(ipField.getText(), nickField.getText());
    	}
    }
    
    /**
     * Valida el input del usuario
     * 
     * @return true si el input es válido
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (ipField.getText() == null || ipField.getText().length() == 0) {
            errorMessage += "¡Introduce una IP!\n"; 
        }
        
        if (nickField.getText() == null || nickField.getText().length() == 0) {
            errorMessage += "¡Introduce un nick!\n"; 
        }else if(nickField.getText() == "master"){
        	 errorMessage += "¡Nombre reservado, vuelve a escribir tu nick!\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Muestra mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(mainApp.getClientStage());
            alert.setTitle("Campo inválido");
            alert.setHeaderText("Por favor introduce datos válidos.");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

}
