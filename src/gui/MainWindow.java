package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import network.PatientClient.PatientClient;
import network.ProtocolException;
import pojos.Patient;

import java.io.IOException;

public class MainWindow {

	private clientSetIPController main;
	private PatientClient client;
	private Stage stage;
	private String dni;
	private Patient extractedPatient;
	private String password;

	@FXML
	private BorderPane loginPane;

	@FXML
	private TextField userField;

	@FXML
	private PasswordField passField;

	@FXML
	private Button signInButton;

	@FXML
	void signInClicked(ActionEvent event) {
		dni = userField.getText();
		password = passField.getText();
		this.stage = new Stage();

		if (login()) {
			FXMLLoader loaderPatient = new FXMLLoader(getClass().getResource("/gui/ClientMainPanel.fxml"));
			try {
				Parent clientPanel = (Parent) loaderPatient.load();
				ClientMainPanelController controller = loaderPatient.<ClientMainPanelController>getController();
				//TODO: give the patient to the new panel.
				controller.initComponents(extractedPatient, client);
				Stage stage = (Stage) (loginPane.getScene().getWindow());
				Scene scene = new Scene(clientPanel, 700,550);
				stage.setScene(scene);
				stage.setResizable(true);
				stage.centerOnScreen();
				//main.updateScene(new Scene(panel,800,800));
				// controller.setMainWindow(this);

			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR, "Error loading the patient view");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Wrong username or password");
			alert.showAndWait();
			client.disconnect(); //????
			//System.exit(0);
		}

	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setMain(clientSetIPController clientSetIPController) {

		this.main = clientSetIPController;

	}

	public void logout() {

		main.loadLogin();

	}





	private boolean login() {
		try {
			extractedPatient = client.login(new Patient(dni, password));
		} catch ( ProtocolException e){
			extractedPatient = null;
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, e.getErrorMessage());
			alert.showAndWait();
		}

		return extractedPatient != null;

	}

	public void initComponents(PatientClient client){
		this.client = client;
	}

}
