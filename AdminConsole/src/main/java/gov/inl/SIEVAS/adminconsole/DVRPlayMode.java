/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

/**
 *
 * @author monejh
 */
public enum DVRPlayMode
{
    Started(1), Stopped(2);

    private int value;
    
    private DVRPlayMode(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return this.value;
    }
    
    public static DVRPlayMode getById(int value)
    {
        for(DVRPlayMode type: values())
        {
            if (type.value == value)
                return type;
        }
        return null;
    }
    
    
}

