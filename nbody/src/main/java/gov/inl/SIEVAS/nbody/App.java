/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.nbody;

import java.io.FileNotFoundException;

/**
 *
 * @author monejh
 */
public class App
{
    public static void main(String[] args) throws FileNotFoundException 
    {
        //NBody nbody = new NBody();
        //nbody.run();
        NBody2 nbody = new NBody2();
        nbody.run();
//        NBodySQLGenerator generator = new NBodySQLGenerator();
//        generator.run();
    }
    
}
