package gov.inl.LIVE;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static void thread(Runnable runnable, boolean daemon)
	{
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
 
    public static void main( String[] args ) throws Exception
    {
   /* 	BrokerService broker = new BrokerService();
		broker.addConnector("tcp://localhost:61616");
		broker.start();
	*/	
		
    	System.out.println( "Starting Message Generator..." );
        
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic("control");
        
        thread(new TestProducer(connection, session, destination), true);
        connection.start();
        
    }
    
    public static class TestProducer implements Runnable, MessageListener
    {
    	private Connection connection;
    	private Session session;
    	private Destination destination;
    	private boolean running = false;
    	
    	public TestProducer(Connection connection, Session session, Destination destination)
    	{
    		this.connection = connection;
    		this.session = session;
    		this.destination = destination;
    	}
    	
    	public void terminate()
    	{
    		running = false;
    	}
    	
    	public void onMessage(Message message)
		{
			System.out.println("Message received:" + message);
			if (message instanceof TextMessage)
			{
				TextMessage txtMsg = (TextMessage) message;
				try
				{
					System.out.println("Message got:" + txtMsg.getText());
					if (txtMsg.propertyExists("terminate"))
					{
//						if (txtMsg.getBooleanProperty("terminate"))
//							this.terminate();
					}
				}
				catch (JMSException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		public void run()
		{
			running = true;
			MessageConsumer consumer;
			try
			{
				consumer = session.createConsumer(destination);
				consumer.setMessageListener(this);
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			while(running)
			{
				try
				{
					MessageProducer producer = session.createProducer(destination);
					String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode() + " - " + (new Date());
					TextMessage message = session.createTextMessage(text);
					producer.send(message);
					Thread.sleep(5000);
					
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try
			{
				session.close();
				connection.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.exit(0);
			
		}
    	
    }
    
}

