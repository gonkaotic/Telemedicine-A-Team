package gui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import network.PatientClient.BitalinoHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class BitalinoPanelController implements Initializable {
    private BitalinoHandler bitalino;
    private Integer ecgChannel;
    private Integer pulseChannel;
    private Integer saturationChannel;
    private String macAddress;
    private ObservableList<Integer> channels = FXCollections.observableArrayList(1,2,3,4,5,6);

    private Pane mainPane;

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
            verification = verifyMacAddress();
            if (!verification) {
                showErrorMessage("Invalid address");
            }
            else{
                bitalino.setMacAddress(this.macAddress);
                bitalino.setAcquisitionChannels(new int[]{ecgChannel-1, pulseChannel-1, saturationChannel-1});
                changePannel();
            }
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

    private boolean verifyMacAddress(){
        this.macAddress= macAddressTextfield.getText().toLowerCase();
        if (macAddress=="") return false;
        if(this.macAddress.matches("([0-9a-f]{2}:){5}[0-9a-f]{2}")) return true;
        return false;
    }

    private void showErrorMessage (String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("ERROR");
        a.setContentText(message);
        a.showAndWait();

    }

    private void changePannel(){
        try{
            Pane blankPane = new Pane();
            this.mainPane.getChildren().clear();
            this.mainPane.getChildren().add(blankPane);
            blankPane.prefHeightProperty().bind(mainPane.heightProperty());
            blankPane.prefWidthProperty().bind(mainPane.widthProperty());

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void initComponents(Pane mainPane, BitalinoHandler bitalino){
        this.mainPane = mainPane;
        this.bitalino = bitalino;
        macAddressTextfield.setText(bitalino.getMacAddress());
        ecgChannelBox.setValue(bitalino.getAcquisitionChannels()[0]+1);
        pulseChannelBox.setValue(bitalino.getAcquisitionChannels()[1]+1);
        saturationChannelBox.setValue(bitalino.getAcquisitionChannels()[2]+1);
    }
}