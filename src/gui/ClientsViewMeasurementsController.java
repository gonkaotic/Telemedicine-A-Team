package gui;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import Database.SQLManager;
import doctorGUI.DoctorMainPanelController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.DatePicker;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import pojos.Measurement;
import pojos.Patient;

public class ClientsViewMeasurementsController implements Initializable {

	private Pane mainPane;
	private Patient patient;
	private ClientMainPanelController mainPanel;

	@FXML
	private GridPane clientsViewMeasurements;

	@FXML
	private TableView<Measurement> measurementsTable;;

	@FXML
	private TableColumn<Measurement, Date> dateColumn;

	@FXML
	private TableColumn<Measurement, Integer> bpmColumn;

	@FXML
	private TableColumn<Measurement, Float> o2SaturationColumn;

	@FXML
	private TableColumn<Measurement, Float> temperatureColumn;

	@FXML
	private TableColumn<Measurement, ArrayList<Measurement.Symptom>> symptomsColumn;

	@FXML
	private TableColumn<Measurement, String> commentsColumn;

	@FXML
	private Label measurementsLabel;

	@FXML
	private DatePicker dateSearch;

	@FXML
	void showNewMeasurement(MouseEvent event) {

		if (event.getClickCount() <= 2) {

			Measurement m = measurementsTable.getSelectionModel().getSelectedItem();
			if (m != null) {
				showNewMeasurement(m);
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		dateColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Date>("Date"));
		bpmColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Integer>("BPM"));
		o2SaturationColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("O2 Saturation"));
		temperatureColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("Temperature"));
		symptomsColumn.setCellValueFactory(
				new PropertyValueFactory<Measurement, ArrayList<Measurement.Symptom>>("symptomChecklist"));
		commentsColumn.setCellValueFactory(new PropertyValueFactory<Measurement, String>("Doctor Comments"));

	}

	public void initComponents(Pane centralPane, Patient patient) {
		dateColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Date>("date"));
		bpmColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Integer>("BPM"));
		o2SaturationColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("spO2"));
		temperatureColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("temperature"));
		symptomsColumn.setCellValueFactory(
				new PropertyValueFactory<Measurement, ArrayList<Measurement.Symptom>>("symptomChecklist"));
		commentsColumn.setCellValueFactory(new PropertyValueFactory<Measurement, String>("Doctor Comments"));
		measurementsTable.getItems().addAll(patient.getMeasurements());
		
		this.patient = patient;
		this.mainPane=centralPane;
	}

	public void showNewMeasurement(Measurement newMeasure) {
		try {
			System.out.println("Showing Patient: " + newMeasure.toString());
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MeasuresChooser.fxml"));
			GridPane measuresPane = (GridPane) loader.load();
			mainPane.getChildren().clear();
			mainPane.getChildren().add(measuresPane);
			measuresPane.prefHeightProperty().bind(mainPane.heightProperty());
			measuresPane.prefWidthProperty().bind(mainPane.widthProperty());

			MeasuresChooserController controller = loader.<MeasuresChooserController>getController();
			controller.init(newMeasure, patient);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
