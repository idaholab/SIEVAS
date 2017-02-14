package gov.inl.SIEVAS.ControlApp;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JMSException
    {
    	System.out.print( "Control Application - Sending Terminate..." );
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic("control");
        
        connection.start();
        
        MessageProducer producer = session.createProducer(destination);
		TextMessage message = session.createTextMessage("Terminate App");
		message.setBooleanProperty("terminate", true);
		producer.send(message);
        
        session.close();
        connection.close();
        System.out.println("Done.");
    }
}
