package gui;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import network.client.BitalinoHandler;
import pojos.ECG;

import java.net.URL;
import java.util.ResourceBundle;

public class NewMeasurementPanelController implements Initializable {

    private BitalinoHandler bitalino;
    private XYChart.Series dataSeries;

    @FXML
    private GridPane measurementsPanel;

    @FXML
    private Label TemperatureLabel;

    @FXML
    private Label PulseOxymeterLabel;

    @FXML
    private Label hearRateLabel;

    @FXML
    private Label oxygenLabel;

    @FXML
    private JFXTextField heartRateTextField;

    @FXML
    private JFXTextField oxygenTextField;

    @FXML
    private Label coughLabel;

    @FXML
    private CheckBox coughCheckBox;

    @FXML
    private TextField temperatureTextField;

    @FXML
    private Label breathLabel;

    @FXML
    private CheckBox breathCheckBox;

    @FXML
    private Button recordOxygenButton;

    @FXML
    private Label tirednessLabel;

    @FXML
    private CheckBox tirednessCheckBox;

    @FXML
    private Label smellLabel;

    @FXML
    private CheckBox smellCheckBox;

    @FXML
    private Label tasteLabel;

    @FXML
    private CheckBox tasteCheckBox;

    @FXML
    private Label ecgLabel;

    @FXML
    private Button recordEcgButton;

    @FXML
    private LineChart<Number, Number> ecgGraph;

    @FXML
    private NumberAxis timeAxis;

    @FXML
    private NumberAxis voltsAxis;

    @FXML
    private Label chestPainLabel;

    @FXML
    private CheckBox chestPainCheckBox;

    @FXML
    private Button submitButton;


    public void initComponents (){

    }

    /**
     * Records the heart rate and oxygen saturation and displays them in their corresponding textfields
     * To block the rest of components we add a progress bar.
     * @param event
     *          button clicked
     */
    @FXML
    void recordOxygenClicked(ActionEvent event) {
        //Maybe we should block the rest of the fields: ¿progress bar?
        try {
            float[] pulseoximeter = bitalino.recordPulseOximeter();
            this.heartRateTextField.setText(""+pulseoximeter[0]);
            this.oxygenTextField.setText(""+pulseoximeter[1]);
        } catch (Throwable throwable) {
            //This should be a pop up or somthing similar
            System.out.println("Problems when connecting with BITalino");
        }
    }

    @FXML
    void recordEcgClicked(ActionEvent event) {
        try {
            ECG ecgValues = bitalino.recordECG();
            dataSeries.getData().clear();
            for (int i=0; i<ecgValues.getEcg().size(); i++){
                dataSeries.getData().add(new XYChart.Data(ecgValues.getEcg().get(i), ecgValues.getTimes().get(i)));
            }
            ecgGraph.getData().clear();
            ecgGraph.getData().add(dataSeries);

        } catch (Throwable throwable) {
            //This should be a pop up or somthing similar
            System.out.println("Problems when connecting with BITalino");
        }
    }

    @FXML
    void submitClicked(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bitalino = new BitalinoHandler("20:17:09:18:49:21");
        dataSeries = new XYChart.Series();
        voltsAxis.setLabel("mV");
        timeAxis.setLabel("ms");
    }
}


