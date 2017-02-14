/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.sievastestclient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple type name, copied from backend.
 * @author monejh
 */
public abstract class ITypeName
{
    @JsonIgnore
    public String getTypeName()
    {
        return this.getClass().getSimpleName();
    }
}
