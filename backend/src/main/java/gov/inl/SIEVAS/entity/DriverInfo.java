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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 * Class for in-memory driver information options
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
