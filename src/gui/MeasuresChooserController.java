package gui;

import java.sql.Date;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import pojos.Measurement;
import pojos.Patient;

public class MeasuresChooserController {

    private Patient patient;
    private ArrayList<Measurement> measurements;
    private ClientMainPanelController pane;
   

    @FXML
    private BorderPane measuresChooserPanel;

    @FXML
    private Label patientNameLabel;

    @FXML
    private Label patientBirthDateLabel;

    @FXML
    private Label patientSexLabel;

    @FXML
    private ListView<Patient.RiskFactor> patientRiskFactors;
    
    @FXML
    private Label measurementDateLabel;

    @FXML
    private Label measurementBPMLabel;

    public void setPatient(Patient patient, ArrayList<Measurement> measurements ){
       
        this.patient = patient;
       // this.measurements=measurements;

        patientNameLabel.setText( patient.getName() );
        patientBirthDateLabel.setText( patient.getBirthDate().toString());
        patientSexLabel.setText( patient.getSex().toString());
        patientRiskFactors.getItems().setAll( patient.getRiskFactor());
        //.setTextFill(new PropertyValueFactory<Measurement,Date>("date"));

    }

    public void init( Patient patient, ClientMainPanelController pane  ) {
        setPatient(patient, measurements);
        this.pane=pane;
       
    }
}
