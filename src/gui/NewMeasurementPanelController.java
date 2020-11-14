package gui;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import network.client.BitalinoHandler;

public class NewMeasurementPanelController {

    private BitalinoHandler bitalino;

    @FXML
    private Label TemperatureLabel;

    @FXML
    private Label PulseOxymeterLabel;

    @FXML
    private Label hearRateLabel;

    @FXML
    private Label oxygenLabel;

    @FXML
    private TextField temperatureTextField;

    @FXML
    private JFXTextField heartRateTextField;

    @FXML
    private JFXTextField oxygenTextField;

    @FXML
    private Label coughLabel;

    @FXML
    private CheckBox coughCheckBox;

    @FXML
    private Label breathLabel;

    @FXML
    private CheckBox breathCheckBox;

    @FXML
    private Button recordOxygenButton;

    public void initComponents (){
        bitalino = new BitalinoHandler("20:17:09:18:49:21");
    }

    @FXML
    void recordOxygenClicked(ActionEvent event) {
        //Maybe we should block the rest of the fields
        try {
            float[] pulseoximeter = bitalino.recordPulseOximeter();
            this.heartRateTextField.setText(""+pulseoximeter[0]);
            this.oxygenTextField.setText(""+pulseoximeter[1]);
        } catch (Throwable throwable) {
            //This should be a pop up or somthing similar
            System.out.println("Problems when connecting with BITalino");
        }
    }


}


