package gui;

import Database.SQLManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import network.ProtocolException;
import pojos.Patient;

import java.io.IOException;
import java.sql.SQLException;

public class MainWindow {

	private Main main;
	private Stage stage;
	private String dni;
	private Patient extractedPatient;
	private String password;
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
			FXMLLoader loaderPatient = new FXMLLoader(getClass().getResource("/gui/clientMainPanelController.fxml"));
			try {
				BorderPane panel = loaderPatient.load();
				clientMainPanelController controller = loaderPatient.<clientMainPanelController>getController();
				//TODO: give the patient to the new panel.
				main.updateScene(new Scene(panel,800,800));
				// controller.setMainWindow(this);

			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR, "Error loading the patient view");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Wrong username or password");
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

	private boolean checkPassword() {
		if (extractedPatient != null) {
			if (extractedPatient.getPassword().equals(password)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean login() {
		try {
			extractedPatient = main.getClient().sendToServer(new Patient(dni, password));
		} catch ( ProtocolException e){
			extractedPatient = null;
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, e.getErrorMessage());
			alert.showAndWait();
		}

		if (extractedPatient == null) {
			return false;
		} else {
			return true;
		}

	}

}
