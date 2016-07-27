/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author monejh
 */
public class JsonListResult<T> extends ITypeName
{
    private long total;
    private Collection<T> data;

    public JsonListResult(long total, Collection<T> data)
    {
        this.total = total;
        this.data = data;
    }
    

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public Collection<T> getData()
    {
        return data;
    }

    public void setData(Collection<T> data)
    {
        this.data = data;
    }
    
    
}
