package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import network.ProtocolException;
import network.PatientClient.BitalinoHandler;
import network.PatientClient.PatientClient;
import pojos.Measurement;
import pojos.Patient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import doctorGUI.MeasurementListViewerController;
import doctorGUI.MeasuresChooserController;

public class ClientMainPanelController implements Initializable {
    private BitalinoHandler bitalino;
    private Patient patient;
    private PatientClient client;

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
            controller.initComponents(centralPane,bitalino,patient);

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
            controller.initComponents(centralPane,patient);

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bitalino = new BitalinoHandler("20:17:09:18:49:21");
    }

    public void initComponents(Patient patient, PatientClient client){
        this.patient=patient;
        this.client = client;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    
    public void showMeasurement( Measurement newMeasure ){
        try {
            //patient = client.getPatient(newMeasure);
            System.out.println("Showing Patient: " + patient.toString());
            FXMLLoader measureChooserLoader = new FXMLLoader(getClass().getResource("/doctorGUI/MeasuresChooser.fxml"));
            FXMLLoader measureListLoader = new FXMLLoader(getClass().getResource("/doctorGUI/MeasurementsListView.fxml"));
            try {

                BorderPane panel = measureChooserLoader.load();
                MeasuresChooserController controller = measureChooserLoader.getController();
                controller.setPatient(patient);

                GridPane listviewPane = measureListLoader.load();
                MeasurementListViewerController listController = measureListLoader.getController();
                listController.init( patient.getMeasurements() );

               // panel.setCenter( listviewPane );
               // mainPane.setCenter( panel );
                //bottomLeftBorderPane.setCenter( patientList );

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading the measure chooser");
                alert.showAndWait();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getErrorMessage());
            alert.showAndWait();
        }
    }

}
