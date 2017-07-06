/* 
 * Copyright 2017 Idaho National Laboratory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
