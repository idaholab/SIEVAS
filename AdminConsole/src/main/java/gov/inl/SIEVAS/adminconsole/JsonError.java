/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

/**
 * Class for errors that include and error string as JSON.
 * @author monejh
 */
public class JsonError extends ITypeName
{
    private String error;

    /***
     * Default constructor. Does nothing.
     */
    public JsonError()
    {
    }

    /**
     * Constructor that takes the error string.
     * @param error The error.
     */
    public JsonError(String error)
    {
        this.error = error;
    }

    
    /***
     * Gets the error string
     * @return the error
     */
    public String getError()
    {
        return error;
    }

    /***
     * Sets the error string.
     * @param error The error message.
     */
    public void setError(String error)
    {
        this.error = error;
    }
    
    
}
