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
package gov.inl.SIEVAS.service;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Handles the data messages from clients.
 * @author monejh
 */
public class AMQDataMessageConsumer implements MessageListener, Runnable
{

    private MessageProducer producer;
    private Session session;
    private boolean running = false;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    /***
     * The message
     * @param producer The producer for the topic.
     * @param session The AMQ session object.
     */
    public AMQDataMessageConsumer(MessageProducer producer, Session session)
    {
        this.producer = producer;
        this.session = session;
    }
    
    /***
     * Handles the data messages from clients
     * @param msg The message received.
     */
    @Override
    public void onMessage(Message msg)
    {
        //Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.INFO, "Client DATA Got message" + msg);
    }

    /***
     * Handles the background thread for the consumer. Sends a ping message.
     */
    @Override
    public void run()
    {
        Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.INFO, "Starting Client Data Thread");
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        running = true;
        lock.unlock();
        lock = null;
        
        lock = readWriteLock.readLock();
        lock.lock();
        while(running)
        {
            lock.unlock();
            try
            {
                //Logger.getLogger(ActiveMQDataMessageConsumer.class.getName()).log(Level.INFO, "Client Data Thread TICK");
                
                try
                {
                    TextMessage txtMsg = session.createTextMessage(new Date().toString());
                    txtMsg.setStringProperty("MessageType", "PingMessage");
                    //producer.send(txtMsg);
                }
                catch (JMSException ex)
                {
                    Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
                Thread.sleep(5000L);
            }
            catch(InterruptedException e)
            {
                return;
            }
            //do stuff here
            lock.lock();
        }
        lock.unlock();
        Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.INFO, "Stopped Client Data Thread");
    }
    
    /***
     * Stops the background thread for the data consumer.
     */
    public void stop()
    {
        Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.INFO, "Stopping Client Data Thread");
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        running = false;
        lock.unlock();
        lock = null;
        Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.INFO, "Done");

    }
    
}
