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

    public JsonListResult()
    {
        this.total = 0;
        this.data = null;
    }
    
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
