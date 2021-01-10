package doctorGUI;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import network.DoctorClient.DoctorClient;


public class IPLoginController {

    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;

    @FXML
    private Button connectButton;

    private DoctorClient client;
    private DoctorMain main;

    public void init ( DoctorClient client, DoctorMain main){
        this.client = client;
        this.main = main;
    }

    //makes sure only numbers are introduced and the numbers are between 0 and 255
    public void checkAddress(KeyEvent e){
        if ( e != null ) {
            TextField source = (TextField) e.getSource();
            if (source != null){
                String text = source.getText();
                if ( text != null && !text.isEmpty()) {

                    char character = text.charAt(text.length() - 1);
                    if (!(character > 47 && character < 58)) {
                        source.deleteText(source.getLength() - 1, source.getLength());
                        text = source.getText();
                    }

                    if ( text != null ) {
                        int value = Integer.parseInt(text);
                        if (value > 255) {
                            source.setText("255");
                        } else if (value < 0) {
                            source.setText("0");
                        }
                    }
                }
            }
        }
    }

    public void checkConnection(MouseEvent e) {
        if ( e != null ){
            Button source = (Button)e.getSource();
            if ( source != null){
                if ( source == connectButton){
                    if (textField1.getText().isEmpty()) textField1.setText("0");
                    if (textField2.getText().isEmpty()) textField1.setText("0");
                    if (textField3.getText().isEmpty()) textField1.setText("0");
                    if (textField4.getText().isEmpty()) textField1.setText("0");
                    String address = textField1.getText() + "."+
                                     textField2.getText() + "."+
                                     textField3.getText() + "."+
                                     textField4.getText();
                    client.setServerIP(address);
                    if (client.connect()){
                        main.loadLogin();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown server or internet connection error");
                        alert.showAndWait();
                    }
                }
            }
        }
    }
}
