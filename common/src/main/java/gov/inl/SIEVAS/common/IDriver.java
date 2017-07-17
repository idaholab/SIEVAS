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

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author monejh
 */
public interface IDriver
{

    /***
     * Handles the init of the driver. Passes the spring contect and list of option values.
     * @param context
     * @param options 
     */
    public void init(ApplicationContext context, List<DriverOption> options);
    
    /**
     * Handles getting a timeslice of data
     * @param startTime The start time to process
     * @param timestep The timestep to get data for.
     * @param resolution The resolution in data points in time.
     * @param maxResults The maximum objects to return.
     * @return
     */
    public List getData(double startTime, double timestep, double resolution, long maxResults);
    
    /***
     * Gets the start time of the data
     * @return Start time
     */
    default public double getStartTime(){return 0.0;};
    
    /***
     * Gets the end time of the data
     * @return End time
     */
    default public double getEndTime(){return 0.0;};
    
    /***
     * Gets the list of options with default values
     * @return The list of options
     */
    default public List<DriverOption> getOptionList() { return new ArrayList<DriverOption>(); }
    
    /***
     * Shuts down the driver and releases all memory
     */
    public void shutdown();
}
