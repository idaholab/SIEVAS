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
