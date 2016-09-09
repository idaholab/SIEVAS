/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.common.IData;
import gov.inl.LIVE.common.IDriver;
import gov.inl.LIVE.driver.NbodyDriver;
import gov.inl.LIVE.entity.DVRCommandMessage;
import gov.inl.LIVE.entity.DVRCommandMessageReply;
import gov.inl.LIVE.entity.DVRPlayMode;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author monejh
 */
public class DVRService implements Runnable, MessageListener
{
    
    private int THREAD_POOL_SIZE = 10;
    
    private IDriver driver;
    private ObjectMapper objMapper;
    private Session amqSession;
    private MessageConsumer controlMessageConsumer;
    private MessageProducer controlMessageProducer;
    private MessageProducer dataMessageProducer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    private ScheduledFuture scheduleFuture;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    private double playSpeed = 1.0;
    private boolean playing = false;
    private double currentTime = 0.0;
    
    public DVRService(ApplicationContext context, Session amqSession, MessageConsumer controlMessageConsumer, MessageProducer controlMessageProducer, MessageProducer dataMessageProducer)
    {
        this.objMapper = context.getBean(ObjectMapper.class);
        this.driver = new NbodyDriver();
        this.amqSession = amqSession;
        this.controlMessageConsumer = controlMessageConsumer;
        this.controlMessageProducer = controlMessageProducer;
        this.dataMessageProducer = dataMessageProducer;
        driver.init(context);
        
    }
    
    public void start()
    {
        scheduleFuture = scheduler.scheduleAtFixedRate(this, 0, 100, TimeUnit.MILLISECONDS);
        try
        {
            controlMessageConsumer.setMessageListener(this);
        }
        catch(JMSException e)
        {
            Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public double getPlaySpeed()
    {
        lock.readLock().lock();
        try
        {
            if (playing)
                return playSpeed;
            else
                return 0.0;
        }
        finally
        {
            lock.readLock().unlock();
        }
        
    }
    
    private void setPlaySpeed(double playSpeed)
    {
        lock.writeLock().lock();
        this.playSpeed = playSpeed;
        lock.writeLock().unlock();
    }
    
    public double getCurrentTime()
    {
        lock.readLock().lock();
        try
        {
            return currentTime;
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    
    private void setCurrentTime(double currentTime)
    {
        lock.writeLock().lock();
        this.currentTime = currentTime;
        lock.writeLock().unlock();
    }
    
    private void stopPlayback()
    {
        lock.writeLock().lock();
        //this.playSpeed = 0.0;
        this.playing = false;
        lock.writeLock().unlock();
    }
    
    private void startPlayback()
    {
        lock.writeLock().lock();
        //this.playSpeed = 1.0;
        if (this.playSpeed == 0.0)
            this.playSpeed = 1.0;
        this.playing = true;
        lock.writeLock().unlock();
    }
    
    @Override
    public void run()
    {
        lock.readLock().lock();
        if (playing && (playSpeed!=0.0))
        {
            
            double startTime;
            if (playSpeed>0)
                startTime = currentTime;
            else
                startTime = currentTime - 0.1*playSpeed;

            //getNextBlock of Data
            List<IData> dataList = driver.getData(startTime, 0.1*playSpeed, 60.0, 1000);
            //Send Data
            dataList.stream().forEach((data) ->
            {
                try
                {
                    TextMessage msg = amqSession.createTextMessage(objMapper.writeValueAsString(data));
                    msg.setStringProperty("ObjectName", data.getClass().getSimpleName());
                    dataMessageProducer.send(msg);
                }
                catch (JMSException | JsonProcessingException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            lock.readLock().unlock();
            lock.writeLock().lock();
            currentTime+= playSpeed*0.1;
            lock.writeLock().unlock();
        }
        else
            lock.readLock().unlock();


    }
    
    
    public void stop()
    {
        scheduleFuture.cancel(true);
    }

    @Override
    public void onMessage(Message msg)
    {
        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, "MSG:" + msg);
        if (msg instanceof TextMessage)
        {
            String txt = "", objName="";
            TextMessage txtMsg = (TextMessage)msg;
            try
            {
                objName = txtMsg.getStringProperty("ObjectName");
            }
            catch (JMSException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try
            {
                txt = txtMsg.getText();
            }
            catch (JMSException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ((objName!=null) && objName.equals(DVRCommandMessage.class.getSimpleName()))
            {
                DVRCommandMessage cmdMsg;
                try
                {
                    cmdMsg = objMapper.readValue(txt, DVRCommandMessage.class);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                DVRCommandMessageReply replyMsg = new DVRCommandMessageReply(cmdMsg.getId(), cmdMsg.getCommandType(), false);
                switch(cmdMsg.getCommandType())
                {
                    case Start:
                        startPlayback();
                        replyMsg.setPlayMode((playing) ? DVRPlayMode.Started: DVRPlayMode.Stopped);
                        replyMsg.setPlaySpeed(playSpeed);
                        replyMsg.setSuccess(true);
                        break;
                    case Stop:
                        stopPlayback();
                        replyMsg.setPlayMode((playing) ? DVRPlayMode.Started: DVRPlayMode.Stopped);
                        replyMsg.setPlaySpeed(playSpeed);
                        replyMsg.setSuccess(true);
                        break;
                    case GetStatus:
                        //return result
                        replyMsg.setPlayMode((playing) ? DVRPlayMode.Started: DVRPlayMode.Stopped);
                        replyMsg.setPlaySpeed(playSpeed);
                        replyMsg.setSuccess(true);
                        break;
                }
                try
                {
                    TextMessage replyTxtMsg = amqSession.createTextMessage(objMapper.writeValueAsString(replyMsg));
                    replyTxtMsg.setStringProperty("ObjectName", replyMsg.getClass().getSimpleName());
                    controlMessageProducer.send(replyTxtMsg);
                } 
                catch (JsonProcessingException | JMSException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
