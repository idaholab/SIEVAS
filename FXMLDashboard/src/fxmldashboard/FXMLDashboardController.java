package fxmldashboard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
// JMS imports
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author stermj
 */
public class FXMLDashboardController implements Initializable {
    
    private JMSProducer producer;
        
    /**
     * Runs the script associated with the button that was pushed.
     * @param event 
     */
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
    
    /**
     * Terminates all programs run from the dashboard.
     * @param event 
     */
    @FXML
    private void handleTerminateAction(ActionEvent event) throws InterruptedException {
        thread(new JMSConsumer(), false);
        Thread.sleep(100);                                                           // RACE CONDITION HERE WHEN USING A JMS TOPIC. THE SLEEP (>10ms) IS A POOR WAY TO FIX THIS.
        producer.sendMessage("TERMINATE THE PROGRAM.");
    }
    
    /**
     * Initialize this class.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        producer = new JMSProducer();
        System.out.println("FXMLDashboardController initialization complete.");
    }    
    
    /**
     * Spawns a new thread running that runs the given object.
     * @param runnable - The object to be executed.
     * @param daemon 
     */
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
    
    /**
     * Handles cleanup for the FXMLDashboardController class.
     */
    public void destroy() {
        producer.close();
    }
}
