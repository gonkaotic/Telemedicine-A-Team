package doctorGUI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import pojos.Patient;

import java.util.ArrayList;
import java.sql.Date;


public class PatientsListController {

    private ArrayList<Patient> patientList;

    private DoctorMainPanelController mainPanel;

    @FXML
    private TableView<Patient> patientListView;

    @FXML
    private TableColumn<Patient, String> patientNameColumn;
    @FXML
    private TableColumn<Patient, Patient.Sex> patientSexColumn;
    @FXML
    private TableColumn<Patient, Date> patientBirthColumn;
    @FXML
    private TableColumn<Patient, ArrayList<Patient.RiskFactor> > patientRiskFactorColumn;

    public void setPatientList(ArrayList<Patient> patientList) {
        this.patientList = patientList;
        patientListView.getItems().addAll( this.patientList);
    }
    @FXML
    void showNewPatient(MouseEvent event){
        if ( event.getClickCount() <= 2 ){
            Patient p = patientListView.getSelectionModel().getSelectedItem();
            if ( p != null ) mainPanel.showNewPatient( p );
        }
    }

    public void init ( ArrayList<Patient> patients, DoctorMainPanelController parent ) {
        mainPanel = parent;

        patientNameColumn.setCellValueFactory(new PropertyValueFactory<Patient,String>("name"));
        patientSexColumn.setCellValueFactory(new PropertyValueFactory<Patient,Patient.Sex>("sex"));
        patientBirthColumn.setCellValueFactory(new PropertyValueFactory<Patient,Date>("birthDate"));
        patientRiskFactorColumn.setCellValueFactory(new PropertyValueFactory<Patient, ArrayList<Patient.RiskFactor>>("riskFactor"));
        setPatientList( patients );
    }



}
