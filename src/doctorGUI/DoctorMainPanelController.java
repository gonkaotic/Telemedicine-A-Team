package doctorGUI;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import network.DoctorClient.DoctorClient;
import network.ProtocolException;
import pojos.Doctor;
import pojos.Patient;

import java.io.IOException;

public class DoctorMainPanelController {

    private Doctor doctor = null;

    private Patient patient = null;

    private DoctorMain main = null;

    private Stage window;

    @FXML
    private Label doctorName;

    @FXML
    private BorderPane mainPane;

    @FXML
    private VBox leftVBox;

    @FXML
    private BorderPane bottomLeftBorderPane;

    private Pane patientList = null;
    private PatientsListController patientsListController = null;
    private DoctorClient client;

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;

        doctorName.setText( doctor.getName() );

        FXMLLoader patientListLoader = new FXMLLoader(getClass().getResource("/doctorGUI/PatientsList.fxml"));
        try {
            patientList = patientListLoader.load();
            patientsListController = patientListLoader.getController();
            patientsListController.init( doctor.getPatients(), this);
            setPatientChooser();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading the patient list");
            alert.showAndWait();
        }

        this.setPatientChooser();
    }

    private void setPatientChooser( ) {
        FXMLLoader patientChooserLoader = new FXMLLoader(getClass().getResource("/doctorGUI/PatientChooser.fxml"));
        try {
            BorderPane panel = patientChooserLoader.load();
            PatientChooserController controller = patientChooserLoader.getController();
            panel.setCenter( patientList);
            mainPane.setCenter( panel );

            window.sizeToScene();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading the patient chooser");
            alert.showAndWait();
        }
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void showNewPatient( Patient newPatient ){
        try {
            patient = client.getPatient(newPatient);
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

                panel.setCenter( listviewPane );
                mainPane.setCenter( panel );
                bottomLeftBorderPane.setCenter( patientList );

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

    public void initComponents(Doctor doctor, Stage window, DoctorClient client) {
        this.window = window;
        setDoctor(doctor);
        this.client = client;
    }
}
