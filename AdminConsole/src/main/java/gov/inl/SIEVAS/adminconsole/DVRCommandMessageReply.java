/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import java.util.Objects;

/**
 *
 * @author monejh
 */
public class DVRCommandMessageReply
{
    private long id;
    private DVRCommandType commandType;
    private boolean success;
    private DVRPlayMode playMode;
    private double playSpeed;

    public DVRCommandMessageReply()
    {
        
    }
    
    public DVRCommandMessageReply(long id, DVRCommandType commandType, boolean success)
    {
        this.id = id;
        this.commandType = commandType;
        this.success = success;
    }

    
    
    
    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public DVRCommandType getCommandType()
    {
        return commandType;
    }

    public void setCommandType(DVRCommandType commandType)
    {
        this.commandType = commandType;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public DVRPlayMode getPlayMode()
    {
        return playMode;
    }

    public void setPlayMode(DVRPlayMode playMode)
    {
        this.playMode = playMode;
    }

    public double getPlaySpeed()
    {
        return playSpeed;
    }

    public void setPlaySpeed(double playSpeed)
    {
        this.playSpeed = playSpeed;
    }
    
    
    

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 23 * hash + Objects.hashCode(this.commandType);
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
        final DVRCommandMessageReply other = (DVRCommandMessageReply) obj;
        if (this.id != other.id)
        {
            return false;
        }
        if (this.commandType != other.commandType)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DVRCommandMessageReply{" + "id=" + id + ", commandType=" + commandType + ", success=" + success + ", playMode=" + playMode + ", playSpeed=" + playSpeed + '}';
    }

    

    
    
    
    
}
