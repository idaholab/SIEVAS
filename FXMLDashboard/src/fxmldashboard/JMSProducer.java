package fxmldashboard;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
/**
 * Original class can be found at http://activemq.apache.org/version-5-hello-world.html
 * @author stermj
 */
public class JMSProducer implements Runnable{

    private String message = "";
    
    @Override
    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");          // NEEDS TO BE CHANGED TO TCP CONNECTION WHEN NOT RUNNING ON THE SAME PROCESS

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic("DASHBOARD");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
//            String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
//            TextMessage message = session.createTextMessage(text);
//
//            // Tell the producer to send the message
//            System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
            System.out.println("SENT: " + this.message);
            producer.send(session.createTextMessage(this.message));

            // Clean up
            session.close();
            connection.close();
            producer.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        } 
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
}
