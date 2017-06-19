/* 
 * Copyright 2017 Idaho National Laboratory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import gov.inl.SIEVAS.nbody.Polynomial;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author monejh
 */

public class TestPoly
{
    Polynomial pol1 = new Polynomial(new double[]{1.0, 2.0,3.0});
    Polynomial pol2 = new Polynomial(new double[]{4.0, 5.0,6.0,7.0});
    Polynomial pol3 = new Polynomial(new double[]{12.3});
    Polynomial pol4 = new Polynomial(new double[]{});
        
    @Before
    public void setup()
    {
        
    }
    
    @Test
    public void TestDegree()
    {
        
        
        assertTrue(pol1.getDegree() == 2);
        assertTrue(pol2.getDegree() == 3);
        assertTrue(pol3.getDegree() == 0);
        assertTrue(pol4.getDegree() == 0);
    }
    
    @Test
    public void TestEquals()
    {
        assertTrue(!pol1.equals(pol2));
        assertTrue(!pol2.equals(pol1));
        assertTrue(!pol4.equals(pol3));
        assertTrue(!pol3.equals(pol4));
        assertTrue(pol1.equals(pol1));
    }
    
    @Test
    public void TestIntegrate()
    {
        assertTrue(pol1.integrate().equals(new Polynomial(new double[]{0,1,1,1})));
        assertTrue(pol2.integrate().equals(new Polynomial(new double[]{0,4,5.0/2.0,2,7.0/4.0})));
        assertTrue(pol3.integrate().equals(new Polynomial(new double[]{0,12.3})));
        assertTrue(pol4.integrate().equals(new Polynomial(new double[]{0})));
    }
    
    
    @Test
    public void TestGetCoefficient()
    {
        assertTrue(pol1.getCoefficient(0) == 1);
        assertTrue(pol1.getCoefficient(1) == 2);
        assertTrue(pol1.getCoefficient(2) == 3);
    }
    
    @Test
    public void TestSetCoefficient()
    {
        double val1 = pol1.getCoefficient(0);
        double val2 = pol1.getCoefficient(1);
        pol1.setCoefficient(0, 5.0);
        pol1.setCoefficient(1, 2.0);
        
        assertTrue(pol1.getCoefficient(0) == 5.0);
        assertTrue(pol1.getCoefficient(1) == 2.0);
        
        pol1.setCoefficient(0,val1);
        pol1.setCoefficient(1,val2);
    }
    
    @Test
    public void TestScale()
    {
        assertTrue(pol2.scale(5.0).equals(new Polynomial(new double[]{20.0, 25.0,30.0,35.0})));
        assertTrue(pol2.scale(0.0).equals(new Polynomial(new double[]{})));
        assertTrue(pol2.scale(-5.0).equals(new Polynomial(new double[]{-20.0, -25.0,-30.0,-35.0})));
    }
    
    @Test
    public void TestAdd()
    {
        assertTrue(pol1.add(pol2).equals(new Polynomial(new double[]{5,7,9,7})));
        assertTrue(pol1.add(pol3).equals(new Polynomial(new double[]{13.3,2,3})));
        assertTrue(pol1.add(pol4).equals(new Polynomial(new double[]{1.0, 2.0,3.0})));
    }
    
    @Test
    public void TestSubtract()
    {
        assertTrue(pol1.subtract(pol2).equals(new Polynomial(new double[]{-3,-3,-3,-7})));
        assertTrue(pol1.subtract(pol3).equals(new Polynomial(new double[]{-11.3,2,3})));
        assertTrue(pol1.subtract(pol4).equals(new Polynomial(new double[]{1.0, 2.0,3.0})));
    }
    
    @Test
    public void TestMultiply()
    {
        assertTrue(pol1.multiply(pol2).equals(new Polynomial(new double[]{4,13,28,34,32,21})));
        assertTrue(pol1.multiply(pol3).equals(new Polynomial(new double[]{12.3,24.6,36.9})));
        assertTrue(pol1.multiply(pol4).equals(new Polynomial(new double[]{})));
    }
    
    @Test
    public void TestPower()
    {
        assertTrue(pol1.power(0).equals(new Polynomial(new double[]{1.0})));
        assertTrue(pol1.power(1).equals(pol1));
        assertTrue(pol1.power(2).equals(new Polynomial(new double[]{1,4,10,12,9})));
    }
    
    @Test
    public void TestEvaluate()
    {
        assertTrue(pol1.evaluate(0.5) == 2.75);
        assertTrue(pol2.evaluate(10) == 7654);
        assertTrue(pol3.evaluate(0) == 12.3);
        assertTrue(pol4.evaluate(1) == 0);
    }
    
    @Test
    public void TestPicard()
    {
        double x0 = 1.0;
        Polynomial p = new Polynomial(new double[]{x0});
        for(int ii=0;ii<8;ii++)
        {
            Polynomial newP = p.integrate();
            newP.setCoefficient(0, x0);
            p = newP;
            System.out.println((ii+1) + ":" + p);
        }
        assertTrue(p.equals(new Polynomial(new double[]{1, 1, 0.5, 1.0/6.0, 1.0/24.0, 1.0/120.0, 1.0/720.0, 1.0/5040.0, 1.0/40320.0})));
        System.out.println(p.evaluate(0.01));
        System.out.println(1.010050167084168);
        assertTrue(Math.abs(p.evaluate(0.01) - 1.010050167084168) < Math.ulp(1)*100);
        
        p = new Polynomial(new double[]{x0});
        for(int ii=0;ii<8;ii++)
        {
            Polynomial newP = p.scale(2.0).integrate();
            newP.setCoefficient(0, x0);
            p = newP;
            System.out.println((ii+1) + ":" + p);
        }
        assertTrue(p.equals(new Polynomial(new double[]{1, 2*1, 4*0.5, 8*1.0/6.0, 16*1.0/24.0, 32*1.0/120.0, 64*1.0/720.0, 128*1.0/5040.0, 256*1.0/40320.0})));
        System.out.println(p.evaluate(0.01));
        System.out.println(1.020201340026756);
        assertTrue(Math.abs(p.evaluate(0.01) - 1.020201340026756) < Math.ulp(1)*100);
    }
}
