/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.service;

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
 *
 * @author monejh
 */
public class AMQControlMessageConsumer implements MessageListener, Runnable
{

    private MessageProducer producer;
    private Session session;
    private boolean running = false;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    public AMQControlMessageConsumer(MessageProducer producer, Session session)
    {
        this.producer = producer;
        this.session = session;
    }
    
    @Override
    public void onMessage(Message msg)
    {
//        Logger.getLogger(AMQControlMessageConsumer.class.getName()).log(Level.INFO, "Client CONTROL Got message " + msg);
    }

    @Override
    public void run()
    {
        
        Logger.getLogger(AMQControlMessageConsumer.class.getName()).log(Level.INFO, "Starting Client Control Thread");
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
                try
                {
                    TextMessage txtMsg = session.createTextMessage(new Date().toString());
                    txtMsg.setStringProperty("MessageType", "PingMessage");
                    producer.send(txtMsg);
                }
                catch (JMSException ex)
                {
                    Logger.getLogger(AMQDataMessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Logger.getLogger(ActiveMQControlMessageConsumer.class.getName()).log(Level.INFO, "Client Control Thread TICK");
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
        Logger.getLogger(AMQControlMessageConsumer.class.getName()).log(Level.INFO, "Stopped Client Control Thread");
        
    }
    
    public void stop()
    {
        Logger.getLogger(AMQControlMessageConsumer.class.getName()).log(Level.INFO, "Stopping Client Control Thread");
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        running = false;
        lock.unlock();
        lock = null;
        Logger.getLogger(AMQControlMessageConsumer.class.getName()).log(Level.INFO, "Done");

    }
    
}
