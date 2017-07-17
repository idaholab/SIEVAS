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
package gov.inl.SIEVAS.entity;

import gov.inl.SIEVAS.common.IData;
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import java.util.Arrays;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author SZEWTG
 */
public class UavData  extends IData implements Serializable, IIdentifier<Long> {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "type")
    private String type;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "step")
    private long step;

    private double x;
    private double y;
    private double z;
    
    private double yaw;
    private double pitch;
    private double roll;
    private double gyaw;
    private double gpitch;
    private double groll;
    
    private double ttp;
    
    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double posX)
    {
        this.x = posX;
    }

    public void setY(double posY)
    {
        this.y = posY;
    }
    public double getY()
    {
        return y;
    }

    public void setZ(double posZ)
    {
        this.z = posZ;
    }
    public double getZ()
    {
        return z;
    }

    public void setYaw(double yaw)
    {
        this.yaw = yaw;
    }
    public double getYaw()
    {
        return yaw;
    }

    public void setPitch(double pitch)
    {
        this.pitch = pitch;
    }
    public double getPitch()
    {
        return pitch;
    }

    public void setRoll(double roll)
    {
        this.roll = roll;
    }
    public double getRoll()
    {
        return roll;
    }
    
    
    
    public void setGYaw(double gyaw)
    {
        this.gyaw = gyaw;
    }
    public double getGYaw()
    {
        return gyaw;
    }

    public void setGPitch(double gpitch)
    {
        this.gpitch = gpitch;
    }
    public double getGPitch()
    {
        return gpitch;
    }

    public void setGRoll(double groll)
    {
        this.groll = groll;
    }
    public double getGRoll()
    {
        return groll;
    }
    
    
    public long getStep()
    {
        return step;
    }

    public void setStep(long step)
    {
        this.step = step;
    }
    
    public double getTtp()
    {
        return ttp;
    }

    public void setTtp(double time)
    {
        this.ttp = time;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UavData))
        {
            return false;
        }
        UavData other = (UavData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "UavData{id=" + id + ", type=" + type + ", step=" + step + "time" + getTime() + "}";
    }
}
