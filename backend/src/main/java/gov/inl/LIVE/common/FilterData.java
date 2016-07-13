/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

/**
 *
 * @author monejh
 */
public class FilterData
{
    private String value;
    private String matchMode;

    public FilterData()
    {
    }

    
    public FilterData(String value, String matchMode)
    {
        this.value = value;
        this.matchMode = matchMode;
    }

    
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getMatchMode()
    {
        return matchMode;
    }

    public void setMatchMode(String matchMode)
    {
        this.matchMode = matchMode;
    }
    
    
}
