package gui;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import network.PatientClient.BitalinoHandler;
import pojos.ECG;
import pojos.Measurement;
import pojos.Patient;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class NewMeasurementPanelController implements Initializable {

    private BitalinoHandler bitalino;
    private XYChart.Series dataSeries;
    private Pane centralPane;
    private ECG ecg;
    private Patient patient;

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
            showErrorMessage("BITalino connection error","Problems when connecting with Bitalino: check out your device and the configuration");
        }
    }

    @FXML
    void recordEcgClicked(ActionEvent event) {
        try {
            ECG ecgValues = bitalino.recordECG();
            this.ecg=ecgValues;
            dataSeries.getData().clear();
            for (int i=0; i<ecgValues.getEcg().size(); i++){
                dataSeries.getData().add(new XYChart.Data<>(ecgValues.getTimes().get(i),ecgValues.getEcg().get(i)));
            }
            ecgGraph.getData().clear();
            ecgGraph.getData().add(dataSeries);

        } catch (Throwable throwable) {
            showErrorMessage("BITalino connection error","Problems when connecting with Bitalino: check out your device and the configuration");
            //System.out.println("Problems when connecting with BITalino");
        }
    }

    @FXML
    //TODO send new measurement to server
    void submitClicked(ActionEvent event) {
        String temperature = this.temperatureTextField.getText();
        if (temperature==""){
            showErrorMessage("Temperature error", "Temperature field is empty");
        }
        else{
            try {
                Float temp = Float.parseFloat(temperature);
                String text = heartRateTextField.getText();
                if(text==""){
                    showErrorMessage("Pulse oxymeter error", "No data was recorded from the pulse oxymeter");
                }
                else {
                    try {
                        Integer heartRate = Integer.parseInt(text);//This will work because it is set internally
                        text= oxygenTextField.getText();
                        if (text==""){
                            showErrorMessage("Pulse oxymeter error", "No data was recorded from the pulse oxymeter");
                        }
                        else{
                            try {
                                Float oxygen = Float.parseFloat(text);

                                //Symptoms checklist
                                LinkedList<Measurement.Symptom> symptomsList = new LinkedList<>();
                                if(coughCheckBox.isSelected()){
                                    symptomsList.add(Measurement.Symptom.DRY_COUGH);
                                }
                                if(smellCheckBox.isSelected()){
                                    symptomsList.add(Measurement.Symptom.ANOSMIA);
                                }
                                if(breathCheckBox.isSelected()){
                                    symptomsList.add(Measurement.Symptom.DIFF_BREATH);
                                }
                                if(tasteCheckBox.isSelected()){
                                    symptomsList.add(Measurement.Symptom.AUGEUSIA);
                                }
                                if(tirednessCheckBox.isSelected()){
                                    symptomsList.add(Measurement.Symptom.TIREDNESS);
                                }

                                if(symptomsList.isEmpty()) {
                                   boolean confirmation = showConfirmationMessage("Empty symprtoms", "No symptoms selected. " +
                                            "Do you want to continue?");
                                   if (confirmation){
                                       if(this.ecg!=null){
                                           Measurement measurement = new Measurement()
                                       }
                                       else{

                                       }
                                   }
                                }

                            }catch(NumberFormatException ex){
                                showErrorMessage("Oxymeter error", "Oxygen saturation should be a number");
                            }
                        }
                    }catch(NumberFormatException ex){
                        showErrorMessage("Heart rate error", "Heart rate should be a number");
                    }
                }
            }catch(NumberFormatException ex){
                showErrorMessage("Temperature error", "Temperature must be a number");
            }
        }
    }

    private void showErrorMessage (String title, String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(message);
        a.showAndWait();

    }

    private boolean showConfirmationMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message,
                new ButtonType("Yes", ButtonBar.ButtonData.YES), ButtonType.NO);
        a.setTitle(title);
        String confirmation = a.showAndWait().get().getText();
        if (confirmation.equals("Yes")) {
            return true;
        }
        return false;
    }


    public void initComponents(Pane centralPane, BitalinoHandler bitalino, Patient patient){
        this.centralPane=centralPane;
        this.bitalino=bitalino;
        this.patient = patient;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //bitalino = new BitalinoHandler("20:17:09:18:49:21");
        dataSeries = new XYChart.Series();
        ecgGraph.setCreateSymbols(false);
        voltsAxis.setLabel("mV");
        timeAxis.setLabel("ms");
    }

}


