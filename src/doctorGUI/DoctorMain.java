package doctorGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import network.DoctorClient.DoctorClient;
import pojos.Doctor;

import java.io.IOException;

public class DoctorMain extends Application {

    private Stage window;
    private DoctorClient client = null;

    private Doctor doctor;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        client = new DoctorClient( "localhost" );
        if ( client.connect() ) {
            //TODO: show loading circle while connecting.
            loadLogin();
        } else {
            client = null;
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to connect to server");
            alert.showAndWait();
            System.exit(0);
        }

    }


    private void closeConnection() {
        if ( client != null ) client.disconnect();
    }

    public void loadLogin() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctorGUI/DoctorLoginFXML.fxml"));
            root = loader.load();
            DoctorLoginController controller = loader.getController();
            controller.initComponents(client, window);
            this.window.setScene(new Scene(root));
            this.window.setResizable(false);
            this.window.setTitle("Doctor Client");
            this.window.show();
            this.window.setOnCloseRequest(e -> closeConnection());
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }


    protected DoctorClient getClient (){
        return client;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
