/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.common;

import java.util.Collection;
import java.util.List;

/**
 * Class that encodes the JSON result for a list. Has the total and data fields.
 * @author monejh
 */
public class JsonListResult<T> extends ITypeName
{
    private long total;
    private Collection<T> data;

    /***
     * Constructor with the total and data values
     * @param total
     * @param data 
     */
    public JsonListResult(long total, Collection<T> data)
    {
        this.total = total;
        this.data = data;
    }
    

    /***
     * Gets the total number of records (not current count in data).
     * @return The total.
     */
    public long getTotal()
    {
        return total;
    }

    /***
     * Sets the total number of records.
     * @param total The number of all records.
     */
    public void setTotal(long total)
    {
        this.total = total;
    }

    /***
     * Gets the data collection
     * @return The collection of the data of type T.
     */
    public Collection<T> getData()
    {
        return data;
    }

    /***
     * Sets the data field
     * @param data The value to use for data as a collection of T.
     */
    public void setData(Collection<T> data)
    {
        this.data = data;
    }
    
    
}
