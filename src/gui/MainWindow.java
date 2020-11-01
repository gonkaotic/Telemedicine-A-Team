package gui;

import java.io.IOException;
import java.sql.SQLException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pojos.*;

public class MainWindow {

	private Main main;
	private Stage stage;
	private String username;
	private String password;
	
    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button signInButton;

    @FXML
    void signInClicked(ActionEvent event) {
    	username = userField.getText();
    	password = passField.getText();
    	this.stage=new Stage();
    	FXMLLoader loaderPatient=new FXMLLoader(getClass().getResource("/gui/PatientPanel.fxml"));
		try {
			BorderPane panel= loaderPatient.load();
			PatientWindow controller=loaderPatient.<PatientWindow>getController();
			main.updateScene(new Scene(panel));
			//controller.setMainWindow(this);
		} catch (IOException e) {
			Alert alert=new Alert(AlertType.ERROR, "Error loading the patient view");
			alert.showAndWait();
		}

    }
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setMain(Main main) {
		
		this.main = main;
		
	}
	
	public void logout() {
		
		main.loadLogin();
		
	}
    
	

}

