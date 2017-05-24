/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
