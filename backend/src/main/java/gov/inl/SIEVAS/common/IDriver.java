/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.common;

import java.util.List;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author monejh
 */
public interface IDriver
{

    public void init(ApplicationContext context);
    
    /**
     *
     * @param startTime
     * @param timestep
     * @param resolution
     * @param maxResults
     * @return
     */
    public List getData(double startTime, double timestep, double resolution, long maxResults);
    
    public void shutdown();
}
