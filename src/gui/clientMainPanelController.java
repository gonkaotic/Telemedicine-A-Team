package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import network.PatientClient.BitalinoHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class clientMainPanelController implements Initializable {
    private BitalinoHandler bitalino;

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
        try{
            bitalino.disconnect();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("BitalinoPanel.fxml"));
            GridPane bitalinoPanel = (GridPane) loader.load();
            centralPane.getChildren().clear();
            centralPane.getChildren().add(bitalinoPanel);
            bitalinoPanel.prefHeightProperty().bind(centralPane.heightProperty());
            bitalinoPanel.prefWidthProperty().bind(centralPane.widthProperty());

            BitalinoPanelController controller = loader.<BitalinoPanelController>getController();
            controller.initComponents(centralPane,bitalino);

        }catch(Exception ex){
            ex.printStackTrace();
        }
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

            NewMeasurementPanelController controller = loader.<NewMeasurementPanelController>getController();
            controller.initComponents(centralPane,bitalino);

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void recordsButtonClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientViewMesurements.fxml"));
            GridPane recordsPane = (GridPane) loader.load();
            centralPane.getChildren().clear();
            centralPane.getChildren().add(recordsPane);
            recordsPane.prefHeightProperty().bind(centralPane.heightProperty());
            recordsPane.prefWidthProperty().bind(centralPane.widthProperty());

            ClientsViewMeasurementsController controller = loader.<ClientsViewMeasurementsController>getController();
            controller.initComponents(centralPane);

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bitalino = new BitalinoHandler("20:17:09:18:49:21");
    }

    private void initComponents(BitalinoHandler bitalino){
        this.bitalino = bitalino;
    }
}
