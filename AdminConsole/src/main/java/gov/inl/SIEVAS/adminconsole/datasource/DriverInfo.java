/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.datasource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 *
 * @author monejh
 */
public class DriverInfo
{
    public DriverInfo()
    {
        
    }
    
    
    public DriverInfo(String packageName, String driverName)
    {
        this.driverName = driverName;
        this.packageName = packageName;
    }
    
    
    
    private String driverName;
    private String packageName;

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
    
    @JsonIgnore
    public String getFullName()
    {
        return this.packageName + "." + this.driverName;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.driverName);
        hash = 67 * hash + Objects.hashCode(this.packageName);
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
        final DriverInfo other = (DriverInfo) obj;
        if (!Objects.equals(this.driverName, other.driverName))
        {
            return false;
        }
        if (!Objects.equals(this.packageName, other.packageName))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DriverInfo{" + "driverName=" + driverName + ", packageName=" + packageName + '}';
    }
    
    
    
    
}
