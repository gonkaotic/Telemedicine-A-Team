package gui;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import network.PatientClient.PatientClient;

public class clientSetIPController implements Initializable {

	private String ipAddress;
	private PatientClient patientClient = null;

	@FXML
	private TextField ipAddressTextfield;

	@FXML
	private Button okButton;

	@FXML
	void okButtonClicked(ActionEvent event) {
		ipAddress = ipAddressTextfield.getText();
		
		boolean verification = verifyIPAddress();
		if (!verification) {
			showErrorMessage("Unvalid IpAddres: check it!");
		}else patientClient = new PatientClient( ipAddress );;
		

	}

	private boolean verifyIPAddress() {
		this.ipAddress = ipAddressTextfield.getText();
		if (ipAddress == "")
			return false;
		if (this.ipAddress.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$"))
			return true;
		if (this.ipAddress.matches("localhost"))
			return true;
		return false;
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	private void showErrorMessage(String message) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("ERROR");
		a.setContentText(message);
		a.showAndWait();

	}

}
