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
public class JsonError extends ITypeName
{
    private String error;

    public JsonError()
    {
    }

    
    public JsonError(String error)
    {
        this.error = error;
    }

    
    
    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
    
    
}
