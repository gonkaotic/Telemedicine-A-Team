package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import pojos.Measurement;
import pojos.Patient;

public class MeasuresChooserController implements Initializable {

	private Patient patient;
	private ArrayList<Measurement> measurements;
	private ClientMainPanelController pane;

	@FXML
	private GridPane clientsViewMeasurements;

	@FXML
	private Label patientNameLabel;

	@FXML
	private Label patientBirthDateLabel;

	@FXML
	private Label patientSexLabel;

	@FXML
	private Label measurementDateLabel;

	@FXML
	private Label measurementBPMLabel;

	@FXML
	private LineChart<Number, Number> ecgGraph;

	@FXML
	private NumberAxis timeAxis;

	@FXML
	private NumberAxis voltsAxis;

	@FXML
	private Label measurementO2SaturationLabel;

	@FXML
	private Label measurementTemperatureLabel;

	@FXML
	private Label measurementSimptomsLabel;

	@FXML
	private Label measurementComentLabel;
	private XYChart.Series dataSeries;

	public void init(Measurement m, Patient patient) {

		// this.measurements=measurements;

		patientNameLabel.setText(patient.getName());
		patientBirthDateLabel.setText(patient.getBirthDate().toString());
		patientSexLabel.setText(patient.getSex().toString());

		measurementDateLabel.setText(m.getDate().toString());
		measurementO2SaturationLabel.setText(m.getSpO2().toString());
		measurementTemperatureLabel.setText(m.getTemperature().toString());
		measurementSimptomsLabel.setText(m.getSymptomChecklist().toString());
		measurementBPMLabel.setText(m.getBPM().toString());

		if (m.getComment() != null) {
			this.measurementComentLabel.setText(m.getComment());
		} else {
			measurementComentLabel.setText(" ");
		}

		try {
			if (m.getECG() != null) {
				dataSeries.getData().clear();

				for (int i = 0; i < m.getECG().getEcg().size(); i++) {
					dataSeries.getData()
							.add(new XYChart.Data<>(m.getECG().getTimes().get(i), m.getECG().getEcg().get(i)));
				}
				ecgGraph.getData().clear();
				ecgGraph.getData().add(dataSeries);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading the ECG ");
			alert.showAndWait();
		}

		this.patient = patient;

		// .setTextFill(new PropertyValueFactory<Measurement,Date>("date"));

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ecgGraph.setAnimated(false);
		ecgGraph.setCreateSymbols(false);
		dataSeries = new XYChart.Series();
	}
}
