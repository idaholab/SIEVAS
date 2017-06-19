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
package gov.inl.SIEVAS.nbody;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author monejh
 */
public class NBody
{
    private static int SIZE = 10;
    private static long SECONDS_IN_YEAR = 60*60*24*365;
    private static long YEARS = 1;
    private double mass[];
    private double position[][];
    private double velocity[][];
    private double sigma[][];
    public NBody()
    {
        /*  mass of body Sun        =  1.
      mass of body Mercury    =  1.66013679527193035E-7
      mass of body Venus      =  2.44783959796682464E-6
      mass of body Earth      =  3.04043273871083524E-6
      mass of body Mars       =  3.22714936215392876E-7
      mass of body Jupiter    =  9.54790662147324233E-4
      mass of body Saturn     =  2.85877644368210402E-4
      mass of body Uranus     =  4.35540069686411149E-5
      mass of body Neptune    =  5.17759138448793649E-5
      mass of body Pluto      =  7.6923076923076926E-9 */


        mass = new double [SIZE];
        position = new double[SIZE][3];
        velocity = new double[SIZE][3];
        sigma = new double[SIZE][SIZE];
        
        mass[0]       =  1.;
        mass[1]    =  1.66013679527193035E-7;
        mass[2]      =  2.44783959796682464E-6;
        mass[3]     =  3.04043273871083524E-6;
        mass[4]      =  3.22714936215392876E-7;
        mass[5]    =  9.54790662147324233E-4;
        mass[6]     =  2.85877644368210402E-4;
        mass[7]     =  4.35540069686411149E-5;
        //mass[7]     =  1;
        mass[8]    =  5.17759138448793649E-5;
        mass[9]      =  7.6923076923076926E-9;
        
        //sun
        position[0][0]=0;
        position[0][1]=0;
        position[0][2]=0;
        velocity[0][0]=0;
        velocity[0][1]=0;
        velocity[0][2]=0;
        
        //mercury
        position[1][0] =  0.1633519000000E-02;
        position[1][1] =  0.3077199200000;
        position[1][2] =  0.2498653800000E-01;
        velocity[1][0] = -1.963534107001;
        velocity[1][1] =  0.6841909835729E-01;
        velocity[1][2] = 0.1858224458145;
        
        //venus
        position[2][0] =  0.2640622900000;
        position[2][1] = 0.6709840300000;
        position[2][2] =  -.6078524800000E-02;
        velocity[2][0] =  -1.097989091627;
        velocity[2][1] =  0.4250727728824;
        velocity[2][2] = 0.6918653377540E-01;
        
        //earth
        position[3][0] =  0.6643154100000E-01;
        position[3][1] =    0.9817401900000;
        position[3][2] =  0.6625301000000E-05;
        velocity[3][0] =  -1.014141067952;
        velocity[3][1] =  0.6377084582524E-01;
        velocity[3][2] =  -.1047343992878E-05;

        //mars
        position[4][0] =     1.105892500000;
        position[4][1] =  -.8315317200000 ;
        position[4][2] = -.4460530000000E-01 ;
        velocity[4][0] = 0.5199212681014;
        velocity[4][1] =  0.7198022773915;
        velocity[4][2] =  0.2297489167762E-02;

        //jupiter
        position[5][0] =  4.286062400000;
        position[5][1] =  -2.621093500000;
        position[5][2] =  -.8513666500000E-01;
        velocity[5][0] =  0.2231109535641;
        velocity[5][1] =  0.3949656910948;
        velocity[5][2] =  -.6631270424191E-02;
        
        //saturn
        position[6][0] =  8.834561200000;
        position[6][1] =  3.097512000000;
        position[6][2] = -.4051782400000;
        velocity[6][0] = -.1244215317121;
        velocity[6][1] =  0.3058012696789;
        velocity[6][2] = -.3744219597147E-03;
        
        //uranus
        position[7][0] =  12.29164300000;
        position[7][1] = -15.57520200000;
        position[7][2] = -.2172011500000;
        velocity[7][0] =  0.1780033999880;
        velocity[7][1] =  0.1314573649760;
        velocity[7][2] = -.1824027526613E-02;
        
        //neptune
        position[8][0] =  4.84009700000;
        position[8][1] = -26.23912700000;
        position[8][2] = 0.1982557900000;
        velocity[8][0] =  0.1578550564045;
        velocity[8][1] =  0.9132161165808E-01;
        velocity[8][2] =  -.5510764371051E-02;
        
        //pluto
        position[9][0] =  -12.10226300000;
        position[9][1] = -26.73256000000;
        position[9][2] = 6.362842900000;
        velocity[9][0] =  0.1714028159354;
        velocity[9][1] =  -.1021868903979;
        velocity[9][2] = -.3854629379438E-01;
        
        //compute S_ij
        for(int ii=0;ii<SIZE;ii++)
        {
            sigma[ii][ii] = 0.0;
            for(int jj=ii+1;jj<SIZE;jj++)
            {
                sigma[ii][jj] = 1.0/Math.sqrt(square(position[ii][0]-position[jj][0])
                                + square(position[ii][1]-position[jj][1])
                                + square(position[ii][2]-position[jj][2]));
                sigma[jj][ii] = sigma[ii][jj];
            }
        }
        
    }
    
    private Polynomial computeU(Polynomial x[],Polynomial s[][], int degree)
    {
        Polynomial result = new Polynomial();
        for(int jj=0;jj<degree;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(x[jj].subtract(x[degree]).scale(mass[jj]).multiply(sCubed));
         
        }
        for(int jj=degree+1;jj<SIZE;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(x[jj].subtract(x[degree]).scale(mass[jj]).multiply(sCubed));
         
        }
        return result;
    }
    
    private Polynomial computeV(Polynomial y[],Polynomial s[][], int degree)
    {
        Polynomial result = new Polynomial();
        for(int jj=0;jj<degree;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(y[jj].subtract(y[degree]).scale(mass[jj]).multiply(sCubed));
        }
        for(int jj=degree+1;jj<SIZE;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(y[jj].subtract(y[degree]).scale(mass[jj]).multiply(sCubed));
        }
        return result;
    }
    
    private Polynomial computeW(Polynomial z[],Polynomial s[][], int degree)
    {
        Polynomial result = new Polynomial();
        for(int jj=0;jj<degree;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(z[jj].subtract(z[degree]).scale(mass[jj]).multiply(sCubed));
        
        }
        for(int jj=degree+1;jj<SIZE;jj++)
        {
            Polynomial sCubed = s[degree][jj].power(3);
            result.add(z[jj].subtract(z[degree]).scale(mass[jj]).multiply(sCubed));
        
        }
        return result;
    }
    
    private Polynomial[][] computeS(Polynomial x[],Polynomial y[],Polynomial z[],
                                Polynomial u[],Polynomial v[],Polynomial w[],
                                Polynomial s[][])
    {
       Polynomial result[][] = new Polynomial[SIZE][SIZE];
        
        for(int ii=0;ii<SIZE;ii++)
        {
            for(int jj=ii+1;jj<SIZE;jj++)
            {
                Polynomial xDiff = x[ii].subtract(x[jj]);
                Polynomial yDiff = y[ii].subtract(y[jj]);
                Polynomial zDiff = z[ii].subtract(z[jj]);
                Polynomial uDiff = u[ii].subtract(u[jj]);
                Polynomial vDiff = v[ii].subtract(v[jj]);
                Polynomial wDiff = w[ii].subtract(w[jj]);
                Polynomial sum = xDiff.multiply(uDiff)
                                    .add(yDiff.multiply(vDiff))
                                    .add(zDiff.multiply(wDiff));
                result[ii][jj] = sum.multiply(s[ii][jj].power(3).scale(-1.0));
            }
            result[ii][ii] = new Polynomial();
            for(int jj=0;jj<ii;jj++)
                result[ii][jj] = result[jj][ii];
        }
        return result;
    }
    
    
    private void computeDegree(int degree, double timestep)
    {
        Polynomial x[],y[],z[],u[],v[],w[],s[][];
        x = new Polynomial[SIZE];
        y = new Polynomial[SIZE];
        z = new Polynomial[SIZE];
        u = new Polynomial[SIZE];
        v = new Polynomial[SIZE];
        w = new Polynomial[SIZE];
        s = new Polynomial[SIZE][SIZE];
        
        Polynomial newX[],newY[],newZ[],newU[],newV[],newW[],newS[][];
        newX = new Polynomial[SIZE];
        newY = new Polynomial[SIZE];
        newZ = new Polynomial[SIZE];
        newU = new Polynomial[SIZE];
        newV = new Polynomial[SIZE];
        newW = new Polynomial[SIZE];
        newS = new Polynomial[SIZE][SIZE];
        
        for(int ii=0;ii<SIZE;ii++)
        {
            x[ii] = new Polynomial(new double[]{position[ii][0]});
            y[ii] = new Polynomial(new double[]{position[ii][1]});
            z[ii] = new Polynomial(new double[]{position[ii][2]});
            u[ii] = new Polynomial(new double[]{velocity[ii][0]});
            v[ii] = new Polynomial(new double[]{velocity[ii][1]});
            w[ii] = new Polynomial(new double[]{velocity[ii][2]});
            for(int jj=0;jj<SIZE;jj++)
                s[ii][jj] = new Polynomial(new double[]{sigma[ii][jj]});
        }
        
        for(int zz=0;zz<degree;zz++)
        {
            //System.out.println("Computing degree " + zz);
            for(int ii=0;ii<SIZE;ii++)
            {
                newX[ii] = u[ii].integrate();
                newX[ii].setCoefficient(0, position[ii][0]);
                newY[ii] = v[ii].integrate();
                newY[ii].setCoefficient(0, position[ii][1]);
                newZ[ii] = w[ii].integrate();
                newZ[ii].setCoefficient(0, position[ii][2]);

                newU[ii] = computeU(x, s, ii).integrate();
                newU[ii].setCoefficient(0, velocity[ii][0]);
                newV[ii] = computeV(y, s, ii).integrate();
                newV[ii].setCoefficient(0, velocity[ii][1]);
                newW[ii] = computeW(z, s, ii).integrate();
                newW[ii].setCoefficient(0, velocity[ii][2]);
            }
            
            newS = computeS(x, y, z, u, v, w, s);
            for(int jj=0;jj<SIZE;jj++)
            {
                for(int kk=jj+1;kk<SIZE;kk++)
                {
                    newS[jj][kk] = newS[jj][kk].integrate();
                    newS[jj][kk].setCoefficient(0, sigma[jj][kk]);
                }
                newS[jj][jj] = new Polynomial();
                for(int kk=0;kk<jj;kk++)
                    newS[jj][kk] = newS[kk][jj].scale(1.0);
            }
            
            for(int ii=0;ii<SIZE;ii++)
            {
                x[ii] = newX[ii];
                y[ii] = newY[ii];
                z[ii] = newZ[ii];
                u[ii] = newU[ii];
                v[ii] = newV[ii];
                w[ii] = newW[ii];
                System.arraycopy(newS[ii], 0, s[ii], 0, SIZE);
                
            }
            
        }
        for(int ii=0;ii<SIZE;ii++)
        {
            position[ii][0] = newX[ii].evaluate(timestep);
            position[ii][1] = newY[ii].evaluate(timestep);
            position[ii][2] = newZ[ii].evaluate(timestep);
            velocity[ii][0] = newU[ii].evaluate(timestep);
            velocity[ii][1] = newV[ii].evaluate(timestep);
            velocity[ii][2] = newW[ii].evaluate(timestep);
            for(int jj=ii+1;jj<SIZE;jj++)
                sigma[ii][jj] = newS[ii][jj].evaluate(timestep);
            sigma[ii][ii] = 0.0;
            for(int jj=0;jj<ii;jj++)
                sigma[ii][jj] = sigma[jj][ii];
        }
    }
    
    
    private static double square(double x)
    {
        return x*x;
    }
    
    public void run()
    {
        double h = Math.pow(0.5, 14);
        int timesteps_in_sec = (int)Math.floor(1.0/h);
        System.out.println("Timesteps in 1 sec = " + timesteps_in_sec);
        System.out.println("Total timesteps = " + timesteps_in_sec*SECONDS_IN_YEAR*YEARS);
        int degree = 8;
        
        PrintWriter pw[] = new PrintWriter[SIZE];
        
        for(int ii=0;ii<SIZE;ii++)
        {
            try
            {
                pw[ii] = new PrintWriter("coord" + (ii+1));
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(NBody.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
        
        long counter = 0;
        for(int jj=0;jj<SECONDS_IN_YEAR*YEARS;jj++)
        {
            for(int ii=0;ii<timesteps_in_sec;ii++)
            {
                counter++;
                //if (counter%10000 == 0)
                    System.out.println("Computing timestep " + counter);
                computeDegree(degree, h);
                for(int kk=0;kk<SIZE;kk++)
                {
                    pw[kk].println(position[kk][0] + " " + position[kk][1] + " " + position[kk][2]);
                    pw[kk].flush();
                }
            }
            System.out.println("Finished " + (jj+1) + " seconds");
        }
        for(int ii=0;ii<SIZE;ii++)
            pw[ii].close();
            
        for(int ii=0;ii<SIZE;ii++)
        {
            System.out.println((ii+1) + ":" + position[ii][0] + " " + position[ii][1] + " " + position[ii][2]);
        }
    }
}
