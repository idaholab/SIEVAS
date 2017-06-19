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
package gov.inl.SIEVAS.dvrcontrol;


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

