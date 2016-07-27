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
 *
 * @author monejh
 */
public class LIVESession
{
    private Long id;
    private String name;
    private UserInfo owner;
    private List<UserInfo> users = new ArrayList<>();
    private List<PermissionGroup> groups = new ArrayList<>();

    public LIVESession()
    {
    }

    public LIVESession(Long id, String name, UserInfo owner)
    {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    
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
    
    
    public String toString()
    {
        return "ID: " + id + ", Name:" + name
                    + ", Owner:" + owner
                    + ", Users:" + users
                    + ",Groups:" + groups;
    }
    
}
