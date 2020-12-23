package gui;

import java.io.IOException;
import java.sql.SQLException;

import Database.SQLManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import network.client.Client;

public class Main extends Application {

	private Stage window;
	private BorderPane rootLayout;
	private Client client = null;

	@Override
	public void start(Stage stage) {
		this.window = stage;
		try {

			this.window = stage;

			Parent root = FXMLLoader.load(getClass().getResource("/gui/MainWindow.fxml"));
			this.window.setScene(new Scene(root));
			this.window.setResizable(true);
			this.window.show();
			this.window.setOnCloseRequest(e -> closeConnection());
			client = new Client( "localhost" );
			if ( client.connect() ) {
				//TODO: show loading circle while connecting.
				loadLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadLogin() {
		
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
		if ( client != null ) client.disconnect();
	}

	protected Client getClient (){
		return client;
	}
}
