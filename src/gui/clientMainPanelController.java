package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class clientMainPanelController {

    @FXML
    private BorderPane clientPane;

    @FXML
    private MenuBar menuBarClient;

    @FXML
    private Menu bitalinoMenu;

    @FXML
    private MenuItem configureBitalinoItem;

    @FXML
    private VBox vBoxClient;

    @FXML
    private Button newMeasurementButton;

    @FXML
    private Button recordsButton;

    @FXML
    private Pane centralPane;

    @FXML
    //TODO openBitalinoPanel
    void configureBitalinoClicked(ActionEvent event) {

    }

    @FXML
    void newMeasurementClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMeasurementPanel.fxml"));
            GridPane measurementsPane = (GridPane) loader.load();
            centralPane.getChildren().clear();
            centralPane.getChildren().add(measurementsPane);
            measurementsPane.prefHeightProperty().bind(centralPane.heightProperty());
            measurementsPane.prefWidthProperty().bind(centralPane.widthProperty());

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void recordsButtonClicked(ActionEvent event) {

    }

}
