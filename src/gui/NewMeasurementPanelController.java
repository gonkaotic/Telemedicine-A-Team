package gui;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class NewMeasurementPanelController {

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
    private Label breathLabel;

    @FXML
    private CheckBox breathCheckBox;

    @FXML
    private Button recordOxygenButton;

    @FXML
    void recordOxygenClicked(ActionEvent event) {

    }

}


