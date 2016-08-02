/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LIVE2 Session object for connecting.
 * @author monejh
 */
public class LIVESession
{
    private Long id;
    private String name;
    private UserInfo owner;
    private List<UserInfo> users = new ArrayList<>();
    private List<PermissionGroup> groups = new ArrayList<>();
    private String dataStreamName;
    private String controlStreamName;

    /***
     * Default contructor, does nothing.
     */
    public LIVESession()
    {
    }

    /***
     * Creates session with id, name, and owner.
     * @param id The ID to use. 
     * @param name The name to use.
     * @param owner  The owner of the session.
     */
    public LIVESession(Long id, String name, UserInfo owner)
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
    public LIVESession(Long id, String name)
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
        final LIVESession other = (LIVESession) obj;
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

    public List<PermissionGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(List<PermissionGroup> groups)
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
    
    
    @Override
    public String toString()
    {
        return "ID: " + id + ", Name:" + name
                    + ", Owner:" + owner
                    + ", Users:" + users
                    + ",Groups:" + groups
                    + ", Control Stream Name:" + controlStreamName
                    + ", Data Stream Name:" + dataStreamName;
    }
    
}
