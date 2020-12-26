package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import network.PatientClient.PatientClient;

public class Main extends Application {

	private Stage window;
	private PatientClient patientClient = null;

	@Override
	public void start(Stage stage) {
		try {
			 
			Parent root= FXMLLoader.load(getClass().getResource("clientSetIP.fxml"));

	        //clientSetIPController controller = loader.<clientSetIPController>getController();
	        //controller.initComponents();

	        Scene scene=new Scene(root, 560, 405);
	        stage.setScene(scene);
	        stage.centerOnScreen();
	        stage.show();
			/*this.window = stage;
			//TODO: Add clientSetIP.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));
			Parent root = loader.load();
			MainWindow controller = loader.getController();
			controller.setMain(this);
			this.window.setScene(new Scene(root));
			this.window.setResizable(true);
			this.window.show();
		
			patientClient = new PatientClient( "localhost" );
			if ( patientClient.connect() ) {
				//TODO: show loading circle while connecting.
				loadLogin();
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*	public void loadLogin() {
		
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml" ));
			root = loader.load();
			MainWindow controller = loader.getController();
			controller.setMain(this);
			this.window.setScene(new Scene(root));
			this.window.setResizable(true);
			this.window.show();
			this.window.setOnCloseRequest(e->closeConnection());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("There was some kind of error and we haven't even started!");
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void updateScene(Scene scene) {
		window.setScene(scene);
	}

	private void closeConnection() {
		if ( patientClient != null ) patientClient.disconnect();
	}

	protected PatientClient getClient (){
		return patientClient;
	}*/
}
