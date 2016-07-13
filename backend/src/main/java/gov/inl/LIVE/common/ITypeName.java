/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

/**
 *
 * @author monejh
 */
public abstract class ITypeName
{
    public String getTypeName()
    {
        return this.getClass().getSimpleName();
    }
}
