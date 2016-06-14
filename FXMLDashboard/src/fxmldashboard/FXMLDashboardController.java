package fxmldashboard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author stermj
 */
public class FXMLDashboardController implements Initializable {
    
    //JMSProducer producer;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            String command = (String) ((Button)(event.getSource())).getProperties().get("script");
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                System.out.println("NullPointerException. This button likely needs a \"script\" property.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleTerminateAction(ActionEvent event) {
        System.out.println("This will terminate any programs being run from the dashboard.");
        // Create a message and send it using the JMS Producer
        JMSProducer newProducer = new JMSProducer();
        newProducer.setMessage("TERMINATE THE PROGRAM.");
        thread(newProducer, false);
        thread(new JMSConsumer(), false);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
//        producer = new JMSProducer();
//        thread(producer, false);
    }    
    
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
    
}
