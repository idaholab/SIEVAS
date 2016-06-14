package fxmldashboard;

// JavaFX Imports
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// JMS Imports
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
/**
 * 
 * @author stermj
 */
public class FXMLDashboard extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDashboard.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("VR Dashboard");
        stage.setScene(scene);
        stage.show();
        
        // Testing the JMS System
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        Thread.sleep(1000);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        Thread.sleep(1000);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSProducer(), false);
//        Thread.sleep(1000);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSConsumer(), false);
//        thread(new JMSProducer(), false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    

    
}