package GUI;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private Stage window;
	private BorderPane rootLayout;
	

	@Override
	public void start(Stage primaryStage) {
		this.window=primaryStage;
	}
	
	
	public void loadLogin() {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindow.fxml"));	
			rootLayout = (BorderPane) loader.load();
			//MainWindow controller = loader.getController();
			//controller.setMain(this);
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
}
