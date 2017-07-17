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
package gov.inl.SIEVAS.common;

import java.util.Objects;

/**
 * Class for driver options and values
 * @author monejh
 */
public class DriverOption
{
    private String optionName;
    private String optionValue;

    public String getOptionName()
    {
        return optionName;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    public String getOptionValue()
    {
        return optionValue;
    }

    public void setOptionValue(String optionValue)
    {
        this.optionValue = optionValue;
    }

    public DriverOption(String optionName, String optionValue)
    {
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public DriverOption()
    {
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.optionName);
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
        final DriverOption other = (DriverOption) obj;
        if (!Objects.equals(this.optionName, other.optionName))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DriverOption{" + "optionName=" + optionName + ", optionValue=" + optionValue + '}';
    }
    
    
    
    
}
