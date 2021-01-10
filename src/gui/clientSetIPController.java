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
import network.PatientClient.PatientClient;

public class clientSetIPController implements Initializable {

	private Stage window;

	private String ipAddress;
	private PatientClient patientClient = null;

	@FXML
	private GridPane ipPanel;

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
		} else {
			patientClient = new PatientClient(ipAddress);
			if (patientClient.connect()) {
				changeWindow();
			} else {
				showErrorMessage("Couldn't connect to the server: check the ip!");
			}
		}

	}

	private void changeWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));
		Parent root;
		try {
			root = loader.load();

			MainWindow controller = loader.getController();
			controller.initComponents(patientClient);
			Stage stage = (Stage) (ipPanel.getScene().getWindow());
			stage.setOnCloseRequest(e->closeConnection());
			Scene scene = new Scene(root, 700,550);
			stage.setScene(scene);
			stage.setResizable(true);
			stage.centerOnScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	public void loadLogin() {

		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));
			root = loader.load();
			MainWindow controller = loader.getController();
			// controller.setMain(this);
			this.window.setScene(new Scene(root));
			this.window.setResizable(true);
			this.window.show();
			this.window.setOnCloseRequest(e -> closeConnection());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("There was some kind of error and we haven't even started!");
		}

	}

	public void updateScene(Scene scene) {
		window.setScene(scene);
	}

	private void closeConnection() {
		if (patientClient != null)
			patientClient.disconnect();
	}

	protected PatientClient getClient() {
		return patientClient;
	}

}
