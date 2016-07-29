/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.service;

import java.util.Objects;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

/**
 * Basic session info for ActiveMQ.
 * @author monejh
 */
public class AMQSessionInfo
{
    private Long LIVESessionId;
    private String controlTopicName;
    private Destination controlDestination;
    private MessageConsumer controlConsumer;
    private MessageProducer controlProducer;
    private AMQControlMessageConsumer controlConsumerThread;
    
    private String dataTopicName;
    private Destination dataDestination;
    private MessageConsumer dataConsumer;
    private MessageProducer dataProducer;
    private AMQDataMessageConsumer dataConsumerThread;

    public AMQSessionInfo(Long LIVESessionId)
    {
        this.LIVESessionId = LIVESessionId;
    }

    

    public Long getLIVESessionId()
    {
        return LIVESessionId;
    }

    public void setLIVESessionId(Long LIVESessionId)
    {
        this.LIVESessionId = LIVESessionId;
    }

    
    
    
    public String getControlTopicName()
    {
        return controlTopicName;
    }

    public void setControlTopicName(String controlTopicName)
    {
        this.controlTopicName = controlTopicName;
    }

    public Destination getControlDestination()
    {
        return controlDestination;
    }

    public void setControlDestination(Destination controlDestination)
    {
        this.controlDestination = controlDestination;
    }

    public MessageConsumer getControlConsumer()
    {
        return controlConsumer;
    }

    public void setControlConsumer(MessageConsumer controlConsumer)
    {
        this.controlConsumer = controlConsumer;
    }

    public MessageProducer getControlProducer()
    {
        return controlProducer;
    }

    public void setControlProducer(MessageProducer controlProducer)
    {
        this.controlProducer = controlProducer;
    }

    public String getDataTopicName()
    {
        return dataTopicName;
    }

    public void setDataTopicName(String dataTopicName)
    {
        this.dataTopicName = dataTopicName;
    }

    public Destination getDataDestination()
    {
        return dataDestination;
    }

    public void setDataDestination(Destination dataDestination)
    {
        this.dataDestination = dataDestination;
    }

    public MessageConsumer getDataConsumer()
    {
        return dataConsumer;
    }

    public void setDataConsumer(MessageConsumer dataConsumer)
    {
        this.dataConsumer = dataConsumer;
    }

    public MessageProducer getDataProducer()
    {
        return dataProducer;
    }

    public void setDataProducer(MessageProducer dataProducer)
    {
        this.dataProducer = dataProducer;
    }

    public AMQControlMessageConsumer getControlConsumerThread()
    {
        return controlConsumerThread;
    }

    public void setControlConsumerThread(AMQControlMessageConsumer controlConsumerThread)
    {
        this.controlConsumerThread = controlConsumerThread;
    }

    public AMQDataMessageConsumer getDataConsumerThread()
    {
        return dataConsumerThread;
    }

    public void setDataConsumerThread(AMQDataMessageConsumer dataConsumerThread)
    {
        this.dataConsumerThread = dataConsumerThread;
    }
    
    

    @Override
    public int hashCode()
    {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final AMQSessionInfo other = (AMQSessionInfo) obj;
        if (!Objects.equals(this.LIVESessionId, other.LIVESessionId))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "AMQSessionInfo{" + "LIVESessionId=" + LIVESessionId + ", controlTopicName=" + controlTopicName + ", controlDestination=" + controlDestination + ", controlConsumer=" + controlConsumer + ", controlProducer=" + controlProducer + ", dataTopicName=" + dataTopicName + ", dataDestination=" + dataDestination + ", dataConsumer=" + dataConsumer + ", dataProducer=" + dataProducer + '}';
    }
    
    
    
    
    
    
}
