/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
