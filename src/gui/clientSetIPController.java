package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import network.client.BitalinoHandler;
import network.client.Client;

public class clientSetIPController implements Initializable {

	private String ipAddress;

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
		}
		

	}

	private boolean verifyIPAddress() {
		this.ipAddress = ipAddressTextfield.getText();
		if (ipAddress == "")
			return false;
		if (this.ipAddress.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$"))
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
