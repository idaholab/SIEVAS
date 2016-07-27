package com.inl.fxmldashboard;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
/**
 * Used to handle messages sent by the Dashboard.
 * @author stermj
 */
public class JMSProducer {

    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    
    public JMSProducer() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");//("vm://localhost");

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            destination = session.createTopic("DASHBOARD");

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);           
        } 
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
    
    /**
     * Sends a given string to the broker where it is distributed to all listening consumers.
     * @param message 
     */
    public void sendMessage(String message) {
        try {
            System.out.println("SENT: " + message);
            TextMessage tm = session.createTextMessage(message);
            tm.setJMSType("text");
            producer.send(tm);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }   
    }
    
    /**
     * Cleans up the necessary items for a safe closing of this object.
     */
    public void close() {
        try { 
            session.close();
            connection.close();
            producer.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }  
}
