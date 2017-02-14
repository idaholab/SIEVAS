/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.common;

import javax.persistence.Transient;

/**
 *
 * @author monejh
 */
public abstract class IData
{
    @Transient
    private double time;

    public double getTime()
    {
        return time;
    }

    public void setTime(double time)
    {
        this.time = time;
    }
    
    
}
