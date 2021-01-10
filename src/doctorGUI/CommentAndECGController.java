package doctorGUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import network.DoctorClient.DoctorClient;
import network.ProtocolException;
import pojos.Measurement;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentAndECGController implements Initializable {
    @FXML
    private LineChart<Number, Number> chartECG;
    @FXML
    private NumberAxis timeAxis;

    @FXML
    private NumberAxis voltsAxis;

    @FXML
    private TextArea comment;
    @FXML
    private Button commentButton;

    private DoctorClient client;
    private Measurement measurement;

    private XYChart.Series dataSeries;


    public void init( Measurement m,  DoctorClient client ){
        measurement = m;
        if ( m.getComment() != null ){
            this.comment.setText( m.getComment() );
        }

        if ( m.getECG() != null ) {
            dataSeries.getData().clear();
            if (m.getECG().getTimes() != null && m.getECG().getEcg() != null) {
                for (int i = 0; i < m.getECG().getEcg().size(); i++) {
                    dataSeries.getData().add(new XYChart.Data<>(m.getECG().getTimes().get(i), m.getECG().getEcg().get(i)));
                }
                chartECG.getData().clear();
                chartECG.getData().add(dataSeries);
            }
        }

        this.client = client;
    }

    public void insertComment(MouseEvent e ) {
        if ( e.getSource() == commentButton ) {
            if ( comment.getText() != null) {
                if (client != null) {
                    measurement.setComment( comment.getText() );
                    try {
                        client.updateMeasurementComment( measurement );
                    } catch (ProtocolException protocolException) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, protocolException.getErrorMessage());
                        alert.showAndWait();
                    }
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataSeries = new XYChart.Series();
        chartECG.setAnimated(false);
        chartECG.setCreateSymbols(false);
        voltsAxis.setLabel("mV");
        timeAxis.setLabel("ms");
    }
}
