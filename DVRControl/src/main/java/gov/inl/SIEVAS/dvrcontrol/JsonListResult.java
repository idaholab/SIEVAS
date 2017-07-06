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
package gov.inl.SIEVAS.dvrcontrol;

import java.util.Collection;

/**
 * Simple list result, copied from backend.
 * @author monejh
 */
public class JsonListResult<T> extends ITypeName
{
    private long total;
    private Collection<T> data;

    public JsonListResult()
    {
        
    }
    
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
    
    
    public String toString()
    {
        return "Total:" + total + ", Data:" + data;
    }
    
}
