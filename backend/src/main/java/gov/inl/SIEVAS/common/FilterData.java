/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.common;

/**
 * Class to handle FilterData from PrimeNG grid.
 * @author monejh
 */
public class FilterData
{
    private String value;
    private String matchMode;

    /***
     * Default constructor. Does nothing.
     */
    public FilterData()
    {
    }

    /***
     * Constructor to take value and matchMode
     * @param value The value of the filter
     * @param matchMode The match mode used. Can be "contains", "startsWith", or "endsWith"
     */
    public FilterData(String value, String matchMode)
    {
        this.value = value;
        this.matchMode = matchMode;
    }

    /***
     * Gets the value of the filter.
     * @return String of the value.
     */
    public String getValue()
    {
        return value;
    }

    /***
     * Sets the value of the filter
     * @param value The value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /***
     * Gets the match mode.
     * @return 
     */
    public String getMatchMode()
    {
        return matchMode;
    }

    /***
     * Sets the match mode.
     * @param matchMode The mode to set.
     */
    public void setMatchMode(String matchMode)
    {
        this.matchMode = matchMode;
    }
    
    
}
