/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.sievastestclient;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Session info, copied from backend and user/group info removed for ease.
 * @author monejh
 */
//this ignores the user/group/owner fields
@JsonIgnoreProperties(ignoreUnknown = true)
public class SIEVASSession
{
    private Long id;
    private String name;
    private String dataStreamName;
    private String controlStreamName;
    private String activemqUrl;

    public SIEVASSession()
    {
    }

    public SIEVASSession(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }
    

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.name);
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
        final SIEVASSession other = (SIEVASSession) obj;
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.id, other.id))
        {
            return false;
        }
        return true;
    }
    
    
    
    

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

   

    public String getDataStreamName()
    {
        return dataStreamName;
    }

    public void setDataStreamName(String dataStreamName)
    {
        this.dataStreamName = dataStreamName;
    }

    public String getControlStreamName()
    {
        return controlStreamName;
    }

    public void setControlStreamName(String controlStreamName)
    {
        this.controlStreamName = controlStreamName;
    }

    public String getActivemqUrl()
    {
        return activemqUrl;
    }

    public void setActivemqUrl(String activemqUrl)
    {
        this.activemqUrl = activemqUrl;
    }
    
    
    
    
    public String toString()
    {
        return "ID: " + id + ", Name:" + name
                    + ", Control Stream Name:" + controlStreamName
                    + ", Data Stream Name:" + dataStreamName
                    + ", ActiveMQ URL:" + activemqUrl;
    }
    
}

