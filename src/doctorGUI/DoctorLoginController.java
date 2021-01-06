package doctorGUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import network.DoctorClient.DoctorClient;
import network.ProtocolException;
import pojos.Doctor;

import java.io.IOException;

public class DoctorLoginController {

    private DoctorClient client;
    private Stage window;
    private Doctor doctor;
    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button signInButton;


    @FXML
    void signInClicked(ActionEvent event) {

        if ( event.getSource() == signInButton ) {
            if (login()) {
                FXMLLoader doctorLoader = new FXMLLoader(getClass().getResource("/doctorGUI/DoctorMainPanelFXML.fxml"));
                try {
                    BorderPane panel = doctorLoader.load();
                    DoctorMainPanelController controller = doctorLoader.getController();
                    controller.initComponents(doctor, window, client);
                    window.setScene(new Scene(panel));

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading the Doctor view");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong username or password");
                alert.showAndWait();
                if ( !client.isConnected() ) client.connect();
            }
        }

    }

    public void initComponents( DoctorClient client, Stage window ) {

        this.client = client;
        this.window = window;

    }

    public Stage getWindow() {
        return window;
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public void setClient(DoctorClient client) {

        this.client = client;

    }

    private boolean login() {
        try {
            doctor = client.login(new Doctor(userField.getText(),  passField.getText()));
        } catch ( ProtocolException e){
            doctor = null;
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getErrorMessage());
            alert.showAndWait();
        }

        if (doctor == null) {
            return false;
        } else {
            return true;
        }

    }

}
