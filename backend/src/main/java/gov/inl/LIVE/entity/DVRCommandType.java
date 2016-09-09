/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.entity;

/**
 * Enum for commands for DVR controls
 * @author monejh
 */
public enum DVRCommandType
{
    GetStatus(1), Start(2), Stop(3);

    private int value;
    
    private DVRCommandType(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return this.value;
    }
    
    public static DVRCommandType getById(int value)
    {
        for(DVRCommandType type: values())
        {
            if (type.value == value)
                return type;
        }
        return null;
    }
    
    
}
