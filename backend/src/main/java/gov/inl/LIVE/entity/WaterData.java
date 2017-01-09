/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.entity;

import gov.inl.LIVE.common.IData;
import gov.inl.LIVE.common.IIdentifier;
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
public class WaterData  extends IData implements Serializable, IIdentifier<Long> {
    
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
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "temp")
    private final double[] temp = new double[11];

    
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

    public long getStep()
    {
        return step;
    }

    public void setStep(long step)
    {
        this.step = step;
    }

    public double[] getTemp()
    {
        return temp;
    }

    public void setTemp(double temp, int loc)
    {
        this.temp[loc] = temp;
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
        if (!(object instanceof WaterData))
        {
            return false;
        }
        WaterData other = (WaterData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "WaterData{id=" + id + ", type=" + type + ", step=" + step + "temp" + Arrays.toString(temp) + "time" + getTime() + "}";
    }
}
