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
package gov.inl.SIEVAS.adminconsole;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import gov.inl.SIEVAS.adminconsole.user.UserInfo;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import gov.inl.SIEVAS.adminconsole.datasource.Datasource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * SIEVAS Session object for connecting.
 * @author monejh
 */
@Entity
@Table(name = "session")
@NamedQueries(
{
    @NamedQuery(name = "SIEVASSession.findAll", query = "SELECT p FROM SIEVASSession p"),
    @NamedQuery(name = "SIEVASSession.findById", query = "SELECT p FROM SIEVASSession p WHERE p.id = :id"),
    @NamedQuery(name = "SIEVASSession.findByName", query = "SELECT p FROM SIEVASSession p WHERE p.name = :name"),
})
public class SIEVASSession implements IIdentifier<Long>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    
    @ManyToOne(optional = false, targetEntity = UserInfo.class)
    private UserInfo owner;
    
    @JoinTable(name = "session_users", joinColumns =
    {
        @JoinColumn(name = "session_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    @ManyToMany
    private List<UserInfo> users = new ArrayList<>();
    
    
    @JoinTable(name = "session_permission_groups", joinColumns =
    {
        @JoinColumn(name = "session_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "permission_group_id", referencedColumnName = "id")
    })
    @ManyToMany
    private Collection<PermissionGroup> groups = new ArrayList<>();
    
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "data_stream_name")
    private String dataStreamName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "control_stream_name")
    private String controlStreamName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "activemq_url")
    private String activemqUrl;
    
    @JoinTable(name = "session_datasource", joinColumns =
    {
        @JoinColumn(name = "session_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "datasource_id", referencedColumnName = "id")
    })
    @ManyToMany
    private Collection<Datasource> datasources = new ArrayList<>();

    /***
     * Default contructor, does nothing.
     */
    public SIEVASSession()
    {
    }

    /***
     * Creates session with id, name, and owner.
     * @param id The ID to use. 
     * @param name The name to use.
     * @param owner  The owner of the session.
     */
    public SIEVASSession(Long id, String name, UserInfo owner)
    {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    /***
     * Create session with id and name.
     * @param id The ID of the session.
     * @param name THe name of the session.
     */
    public SIEVASSession(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    
    public UserInfo getOwner()
    {
        return owner;
    }

    public void setOwner(UserInfo owner)
    {
        this.owner = owner;
    }
    
    @JsonIgnore
    public String getOwnerName()
    {
        if (owner!=null)
            return owner.getUsername();
        else
            return "";
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

    public List<UserInfo> getUsers()
    {
        return users;
    }

    public void setUsers(List<UserInfo> users)
    {
        this.users = users;
    }

    public Collection<PermissionGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(Collection<PermissionGroup> groups)
    {
        this.groups = groups;
    }

    /***
     * Gets the data stream name for ActiveMQ.
     * @return The name of the data stream.
     */
    public String getDataStreamName()
    {
        return dataStreamName;
    }

    /***
     * Sets the data stream name for ActiveMQ.
     * @param dataStreamName data stream name
     */
    public void setDataStreamName(String dataStreamName)
    {
        this.dataStreamName = dataStreamName;
    }

    /***
     * Gets the control stream name for ActiveMQ.
     * @return The name of the control stream.
     */
    public String getControlStreamName()
    {
        return controlStreamName;
    }

    /***
     * Sets the control stream name for ActiveMQ.
     * @param controlStreamName Control stream name to use.
     */
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

    public Collection<Datasource> getDatasources()
    {
        return datasources;
    }

    public void setDatasources(Collection<Datasource> datasources)
    {
        this.datasources = datasources;
    }
    
    
    
    
    
    
    @Override
    public String toString()
    {
        return "ID: " + id + ", Name:" + name
                    + ", Owner:" + owner
                    + ", Users:" + users
                    + ",Groups:" + groups
                    + ", Control Stream Name:" + controlStreamName
                    + ", Data Stream Name:" + dataStreamName
                    + ", ActiveMQ URL:" + activemqUrl
                    + ", Datasources:[" + datasources + "]";
    }
    
}
