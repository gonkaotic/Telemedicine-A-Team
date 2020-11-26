package gui;
import BITalino.BITalino;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BitalinoPanelController implements Initializable {
    private BITalino bitalino;
    private Integer ecgChannel;
    private Integer pulseChannel;
    private Integer saturationChannel;
    private ObservableList<Integer> channels = FXCollections.observableArrayList(1,2,3,4,5,6);

    @FXML
    private GridPane bitalinoPane;

    @FXML
    private Label macLabel;

    @FXML
    private Label ecgChannelLabel;

    @FXML
    private Label pulseChannelLabel;

    @FXML
    private Label saturationChannelLabel;

    @FXML
    private TextField macAddressTextfield;

    @FXML
    private ComboBox<Integer> ecgChannelBox;

    @FXML
    private ComboBox<Integer> pulseChannelBox;

    @FXML
    private ComboBox<Integer> saturationChannelBox;

    @FXML
    private Button okButton;


    @FXML
    void okButtonClicked(ActionEvent event) {
        boolean verification = verifyChannels();
        if (!verification){
            showErrorMessage("Unvalid channels: channels must be different");
        }
        else{

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ecgChannelBox.setItems(this.channels);
        pulseChannelBox.setItems(this.channels);
        saturationChannelBox.setItems(this.channels);
    }

    private boolean verifyChannels (){
        this.ecgChannel = ecgChannelBox.getValue();
        this.saturationChannel = saturationChannelBox.getValue();
        if (saturationChannel == ecgChannel || saturationChannel == null) return false;
        this.pulseChannel=pulseChannelBox.getValue();
        if (pulseChannel == saturationChannel || pulseChannel==ecgChannel || pulseChannel == null) return false;
        return true;
    }

    private void showErrorMessage (String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("ERROR");
        a.setContentText(message);
        a.showAndWait();

    }

    public void initComponents( BITalino bitalino){
        this.bitalino = bitalino;
    }
}