/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.common.DriverOption;
import gov.inl.SIEVAS.common.IData;
import gov.inl.SIEVAS.common.IDriver;
import gov.inl.SIEVAS.entity.DVRCommandMessage;
import gov.inl.SIEVAS.entity.DVRCommandMessageReply;
import gov.inl.SIEVAS.entity.DVRPlayMode;
import gov.inl.SIEVAS.entity.DataSourceOption;
import gov.inl.SIEVAS.entity.Datasource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
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
 * Class for handling DVR functionality and control.
 * @author monejh
 */
public class DVRService implements Runnable, MessageListener
{
    
    private int THREAD_POOL_SIZE = 10;
    
    private HashMap<Datasource, IDriver> datasourceMap = new HashMap<>();
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
    private double startTime = -1.0;
    private ApplicationContext context;
    
    /***
     * Constructor for DVR service. Currently only loads the NBody driver. 
     * TODO: Implement dynamic loading based on session settings and 
     * configuration.
     * @param context The ApplicationContext from spring for loading other beans
     * @param amqSession The ActiveMQ Session
     * @param controlMessageConsumer The Control stream consumer
     * @param controlMessageProducer The control stream producer
     * @param dataMessageProducer  The data stream producer
     */
    public DVRService(ApplicationContext context, List<Datasource> datasourceList, Session amqSession, MessageConsumer controlMessageConsumer, MessageProducer controlMessageProducer, MessageProducer dataMessageProducer)
    {
        
        // get which driver should be ran from the user
        int option = 0;
        String input ="0";
                
        this.objMapper = context.getBean(ObjectMapper.class);
        //this.driver = new NbodyDriver();
        //this.driver = new WaterDriver();
        //this.driver = new UavDriver();
        this.amqSession = amqSession;
        this.controlMessageConsumer = controlMessageConsumer;
        this.controlMessageProducer = controlMessageProducer;
        this.dataMessageProducer = dataMessageProducer;
        this.context = context;
        
        
        for(Datasource ds : datasourceList)
        {
            
            IDriver driver;
            List<DriverOption> options = null;
            try
            {
                Class<? extends IDriver> clazz = (Class<? extends IDriver>) Class.forName(ds.getDriverName());
                driver = clazz.newInstance();
                options = (List<DriverOption>) clazz.getMethod("getOptionList").invoke(driver);
            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (InstantiationException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (IllegalAccessException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (NoSuchMethodException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (SecurityException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (IllegalArgumentException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            catch (InvocationTargetException ex)
            {
                Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            if (options==null)
                options = new ArrayList<DriverOption>();
            
            //set option values
            for(DataSourceOption opt : ds.getOptions())
            {
                for(DriverOption opt2: options)
                {
                    if (opt2.getOptionName().toLowerCase().equals(opt.getOptionName().toLowerCase()))
                    {
                        opt2.setOptionValue(opt.getOptionValue());
                    }
                }
            }
            
            //TODO UPDATE THESE
            driver.init(context,options);
            datasourceMap.put(ds,driver);
        }
        
    }
    
    /**
     * Starts the DVR service. Not called automatically.
     */
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
    
    
    
    public boolean updateDatasources(List<Datasource> datasources)
    {
        //update matches for options
        lock.readLock().lock();
        for(Datasource ds: datasources)
        {
            for(Datasource ds2: datasourceMap.keySet())
            {
                if (ds.getId().equals(ds2.getId()))
                {
                    IDriver driver = datasourceMap.get(ds2);
                    lock.readLock().unlock();
                    lock.writeLock().lock();
                    driver.shutdown();
                    //set option values
                    List<DriverOption> options = null;
                    try
                    {
                        Class<? extends IDriver> clazz = (Class<? extends IDriver>) Class.forName(ds.getDriverName());
                        driver = clazz.newInstance();
                        options = (List<DriverOption>) clazz.getMethod("getOptionList").invoke(driver);
                    }
                    catch (ClassNotFoundException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (InstantiationException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (IllegalAccessException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (NoSuchMethodException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (SecurityException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (IllegalArgumentException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    catch (InvocationTargetException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    for(DataSourceOption opt : ds.getOptions())
                    {
                        for(DriverOption opt2: options)
                        {
                            if (opt2.getOptionName().toLowerCase().equals(opt.getOptionName().toLowerCase()))
                            {
                                opt2.setOptionValue(opt.getOptionValue());
                            }
                        }
                    }
                    if (options == null)
                        options = new ArrayList<DriverOption>();
                    
                    driver.init(context, options);
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
            }
        }
        
        //add new
        for(Datasource ds: datasources)
        {
            if (!datasourceMap.containsKey(ds))
            {
                
                IDriver driver;
                List<DriverOption> options;
                try
                {
                    Class<? extends IDriver> clazz = (Class<? extends IDriver>) Class.forName(ds.getDriverName());
                    driver = clazz.newInstance();
                    options = (List<DriverOption>) clazz.getMethod("getOptionList").invoke(driver);
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (InstantiationException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (IllegalAccessException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (NoSuchMethodException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (SecurityException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (IllegalArgumentException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                catch (InvocationTargetException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }

                //set option values
                for(DataSourceOption opt : ds.getOptions())
                {
                    for(DriverOption opt2: options)
                    {
                        if (opt2.getOptionName().toLowerCase().equals(opt.getOptionName().toLowerCase()))
                        {
                            opt2.setOptionValue(opt.getOptionValue());
                        }
                    }
                }
                driver.init(context, options);
                lock.readLock().unlock();
                lock.writeLock().lock();
                datasourceMap.put(ds,driver);
                lock.writeLock().unlock();
                lock.readLock().lock();
            }
        }
        //remove old
        if (datasourceMap.size()>0)
            for(Datasource ds: datasourceMap.keySet().toArray(new Datasource[0]))
            {
                if (!datasources.contains(ds))
                {
                    IDriver driver = datasourceMap.get(ds);
                    if (driver!=null)
                    {
                        driver.shutdown();
                        lock.readLock().unlock();
                        lock.writeLock().lock();
                        datasourceMap.remove(ds);
                        lock.writeLock().unlock();
                        lock.readLock().lock();
                    }
                }
            }
        
        lock.readLock().unlock();
        return true;
    }
    /***
     * Gets the play speed of the DVR
     * @return 
     */
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
    
    /***
     * Sets the play speed of the DVR
     * @param playSpeed Speed to set. If playSpeed > 0, time moves forward. 
     * If playSpeed < 0, then DVR works backwards.
     */
    private void setPlaySpeed(double playSpeed)
    {
        lock.writeLock().lock();
        this.playSpeed = playSpeed;
        lock.writeLock().unlock();
    }
    
    /***
     * Gets the current time for the DVR
     * @return The time in seconds.
     */
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
    
    /***
     * Sets the current time for the DVR
     * @param currentTime The time to set.
     */
    private void setCurrentTime(double currentTime)
    {
        lock.writeLock().lock();
        this.currentTime = currentTime;
        lock.writeLock().unlock();
    }
    
    /***
     * Stops the playback of the DVR
     */
    private void stopPlayback()
    {
        lock.writeLock().lock();
        //this.playSpeed = 0.0;
        this.playing = false;
        lock.writeLock().unlock();
    }
    
    /***
     * Starts the playback of the DVR
     */
    private void startPlayback()
    {
        lock.writeLock().lock();
        //this.playSpeed = 1.0;
        if (this.playSpeed == 0.0)
            this.playSpeed = 1.0;
        this.playing = true;
        lock.writeLock().unlock();
    }
    
    /***
     * Handles the DVR playback if active
     */
    @Override
    public void run()
    {
        lock.readLock().lock();
    //    if (true)
        if (playing && (playSpeed!=0.0))
        {
            List<IData> dataList = new ArrayList<>();
            for(IDriver driver: datasourceMap.values())
            {
                
                try
                {
                    //getNextBlock of Data
                    List<IData> driverDataList = driver.getData(startTime, playSpeed, 60.0, 1000);
                    // reset start time so it doesn't get stuck at one specific time
                    startTime = -1.0;
                    
                    dataList.addAll(driverDataList);
                    Collections.sort(dataList, new Comparator<IData>() {
                        @Override
                        public int compare(IData o1, IData o2)
                        {
                            return Double.compare(o1.getTime(), o2.getTime());
                        }
                    });
                    
                }
                catch(Exception e)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            
            if (dataList!=null)
            {
                //Send Data
                dataList.stream().forEach((data) ->
                {
                    try
                    {
                        data.getClass();
                        TextMessage msg = amqSession.createTextMessage(objMapper.writeValueAsString(data));
                        msg.setStringProperty("ObjectName", data.getClass().getSimpleName());
                        dataMessageProducer.send(msg);

                    }
                    catch (JMSException | JsonProcessingException ex)
                    {
                        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            lock.readLock().unlock();
            lock.writeLock().lock();
            currentTime+= playSpeed*0.1;
            lock.writeLock().unlock();
        }
        else
            lock.readLock().unlock();


    }
    
    /***
     * Stops the DVR service
     */
    public void stop()
    {
        scheduleFuture.cancel(true);
        for(IDriver driver: datasourceMap.values())
        {
            driver.shutdown();
        }
        datasourceMap.clear();
    }

    
    /***
     * Handles the message from the control stream. Currently responds only to 
     * DVRCommandMessage.
     * @param msg The message to interpret. The Property "ObjectName" should be
     * set to the type of the object sent.
     */
    @Override
    public void onMessage(Message msg)
    {
        Logger.getLogger(DVRService.class.getName()).log(Level.SEVERE, "MSG:{0}", msg);
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
                        
                        double startTime = Double.MAX_VALUE;
                        double endTime = Double.MIN_VALUE;
                        
                        for(IDriver driver: datasourceMap.values())
                        {
                            startTime = Math.min(startTime, driver.getStartTime());
                            endTime = Math.max(endTime, driver.getEndTime());
                        }
                        
                        // if start and end time aren't equal, then it is saved data or atleast data that can be generated from a specific start time
                        if (startTime != endTime)
                        {
                            replyMsg.setStartTime(startTime);
                            replyMsg.setEndTime(endTime);
                            
                        }
                        else
                        {
                            replyMsg.setStartTime(0.0);
                            replyMsg.setEndTime(0.0);
                        }
                        
                        break;
                    case SetStart:
                        startTime = cmdMsg.getStartTime();
                        replyMsg.setStartTime(startTime);
                        replyMsg.setPlayMode((playing) ? DVRPlayMode.Started: DVRPlayMode.Stopped);
                        replyMsg.setPlaySpeed(playSpeed);
                        replyMsg.setSuccess(true);
                        break;
                    case SetPlaySpeed:
                        playSpeed = cmdMsg.getPlaySpeed();
                        if (playSpeed <= 0.0)
                        {
                            playSpeed = 1.0;
                        }
                        replyMsg.setSuccess(true);
                        replyMsg.setPlaySpeed(playSpeed);
                        replyMsg.setPlayMode((playing) ? DVRPlayMode.Started: DVRPlayMode.Stopped);
                        replyMsg.setPlaySpeed(playSpeed);
                        System.out.println("SET PLAY SPEED TO: " + cmdMsg.getPlaySpeed());
                }
                try
                {
                    TextMessage replyTxtMsg = amqSession.createTextMessage(objMapper.writeValueAsString(replyMsg));
                    replyTxtMsg.setStringProperty("ObjectName", replyMsg.getClass().getSimpleName());
                    controlMessageProducer.send(replyTxtMsg);
                } 
                catch (JsonProcessingException | JMSException ex)
                {
                    Logger.getLogger(DVRService.class.getName()).log(Level.INFO, null, ex);
                }
            }
        }
    }
    
}
