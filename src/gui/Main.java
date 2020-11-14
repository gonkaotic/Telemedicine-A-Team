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

public class Main extends Application {

	private Stage window;
	private BorderPane rootLayout;

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
			SQLManager.connect("jdbc:sqlite:././Database/covid watchlist.db");

			loadLogin();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public void loadLogin() {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));
			rootLayout = loader.load();
			MainWindow controller = loader.getController();
			// controller.setMain(this);
			this.window.setScene(new Scene(rootLayout));
			this.window.setResizable(true);
			this.window.show();

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
		try {

			SQLManager.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
