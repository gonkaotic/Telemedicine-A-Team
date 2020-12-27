package doctorGUI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import pojos.Patient;

public class MeasuresChooserController {

    private Patient patient;
    private DoctorMainPanelController mainController;

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

    public void setPatient(Patient patient) {
        this.patient = patient;

        patientNameLabel.setText( patient.getName() );
        patientBirthDateLabel.setText( patient.getBirthDate().toString());
        patientSexLabel.setText( patient.getSex().toString());
        patientRiskFactors.getItems().setAll( patient.getRiskFactor());

    }

    public void init( Patient patient, DoctorMainPanelController mainController ) {
        setPatient(patient);
        this.mainController = mainController;
    }
}
