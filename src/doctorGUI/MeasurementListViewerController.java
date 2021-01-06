package doctorGUI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import pojos.Measurement;
import pojos.Patient;

import java.sql.Date;
import java.util.ArrayList;


public class MeasurementListViewerController {

    @FXML
    private GridPane clientsViewMeasurements;
    @FXML
    private TableView<Measurement> measurementsTable;
    @FXML
    private TableColumn<Measurement, Date> dateColumn;
    @FXML
    private TableColumn<Measurement, Float> o2SaturationColumn;
    @FXML
    private TableColumn<Measurement, Float> temperatureColumn;
    @FXML
    private TableColumn<Measurement, ArrayList<Measurement.Symptom> > symptomsColumn;
    @FXML
    private TableColumn<Measurement, Integer> bpmColumn;

    private DoctorMainPanelController main;

    private ArrayList<Measurement> measurements;

    public void init( ArrayList<Measurement> measurements, DoctorMainPanelController main ){
        this.measurements = measurements;

        dateColumn.setCellValueFactory(new PropertyValueFactory<Measurement,Date>("date"));
        bpmColumn.setCellValueFactory(new PropertyValueFactory<Measurement,Integer>("BPM"));
        o2SaturationColumn.setCellValueFactory(new PropertyValueFactory<Measurement,Float>("spO2"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<Measurement,Float>("temperature"));
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<Measurement,ArrayList<Measurement.Symptom>>("symptomChecklist"));

        measurementsTable.getItems().addAll(  measurements );
        this.main = main;
    }

    public void showECGAndComment(MouseEvent event){
        if ( event.getClickCount() == 2 ){
            Measurement m = measurementsTable.getSelectionModel().getSelectedItem();
            if ( m != null ) main.showECGAndComment( m );
        }
    }
}
