package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MeasurementTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/NewMeasurementPanel.fxml"));
        Parent root= (Parent) loader.load();
        NewMeasurementPanelController controller = loader.<NewMeasurementPanelController>getController();
        controller.initComponents();

        Scene scene=new Scene(root, 560, 405);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
