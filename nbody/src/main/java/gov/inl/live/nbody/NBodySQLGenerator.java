/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.nbody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author monejh
 */
public class NBodySQLGenerator
{
    
    public double [] processLine(String line, long lineNum)
    {
        Scanner sc = new Scanner(line);
        double x,y,z;

        if (sc.hasNextDouble())
            x = sc.nextDouble();
        else
            throw new IllegalArgumentException("Invalid x value for line " + lineNum);
        if (sc.hasNextDouble())
            y = sc.nextDouble();
        else
            throw new IllegalArgumentException("Invalid y value for line " + lineNum);
        if (sc.hasNextDouble())
            z = sc.nextDouble();
        else
            throw new IllegalArgumentException("Invalid z value for line " + lineNum);
        
        return new double[]{x,y,z};
    }
    
    public void run() throws FileNotFoundException
    {
        double h = Math.pow(0.5,14);
        long steps_in_1AU = (long)(1.0/h);
        System.out.println("h = " + h);
        System.out.println("steps per day = " + steps_in_1AU);
        BufferedReader br = new BufferedReader(new FileReader("coord1"));
        try
        {
            String line = br.readLine();
            if (line!=null)
            {
                double[] coords = processLine(line, 1);
                System.out.println("1:" + coords[0] + " " + coords[1] + " " + coords[2]);
            }
            for(long lineNum = 2; (line = br.readLine()) != null; lineNum++)
            {
                double coords[] = processLine(line, lineNum);
                if (lineNum%steps_in_1AU == 0)
                    System.out.println(lineNum + ":" + coords[0] + " " + coords[1] + " " + coords[2]);
            }
        }
        catch(IOException e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Logger.getLogger(NBodySQLGenerator.class.getName()).log(Level.SEVERE, pw.toString());
        }
        
    }
}
