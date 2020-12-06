package gui;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import Database.SQLManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


import javafx.scene.control.DatePicker;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import pojos.Measurement;

public class ClientsViewMeasurementsController implements Initializable {

	private Pane mainPane;

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
	private TableColumn<Measurement, String> symptomsColumn;

	@FXML
	private Label measurementsLabel;

	@FXML
	private DatePicker dateSearch;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		dateColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Date>("Date"));
		bpmColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Integer>("BPM"));
		o2SaturationColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("O2 Saturation"));
		temperatureColumn.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("Temperature"));
		symptomsColumn.setCellValueFactory(new PropertyValueFactory<Measurement, String>("Symptoms"));
		try {
			measurementsTable.getItems().addAll(SQLManager.getAllMeasurements());
		} catch (SQLException | ClassNotFoundException | IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.show();
		}

	}
/* TODO: sacar solo medidas del cliente que se entre
	private ObservableList<Measurement> setMeasurements() {
    	ObservableList<Measurement> measurements = FXCollections.observableArrayList();
			
		try {
			NewMeasurementPanelController controller = SQLManager.
	        //controller.initComponents();
			measurements.addAll(controller.); 
			return measurements;
		} catch (Exception ex) {
			ex.printStackTrace();
			measurements.clear();
			return measurements;
		}
		
	  }*/

	public void initComponents(Pane mainPane) {
		this.mainPane = mainPane;
		//measurementsTable.setItems(setMeasurements());
	}

	private void setMeasurements(ArrayList<Measurement> measurements) {
		ObservableList<Measurement> observableMeasurement = FXCollections.observableArrayList();
		observableMeasurement.addAll(measurements);
		measurementsTable.getItems().clear();
		measurementsTable.getItems().setAll(observableMeasurement);
	}

}
