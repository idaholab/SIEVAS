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
