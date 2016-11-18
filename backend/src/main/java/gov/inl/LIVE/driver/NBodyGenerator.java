/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.driver;

import gov.inl.LIVE.entity.Nbody;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author monejh
 */
public class NBodyGenerator
{
    
    public static class CoordinateInfo
    {
        public CoordinateInfo(double x, double y, double z,
                                double u, double v, double w)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
            this.w = w;
        }
        public double x;
        public double y;
        public double z;
        public double u;
        public double v;
        public double w;
        
    
    }
    public double square(double x)
    {
        return x * x;
    }

    public List<Nbody> run(double startTime, double endTime, List<Nbody> coords, double h, long initialTimestep)
    {
        int N = 10;
        int K1 = 8;
        int i, j, k, m, ns, nc;
        double alpha[][] = new double[N + 1][K1 + 1];
        double beta[][] = new double[N + 1][K1 + 1];
        double Gamma[][] = new double[N + 1][K1 + 1];
        double delta[][] = new double[N + 1][K1 + 1];
        double rho[][] = new double[N + 1][K1 + 1];
        double lambda[][] = new double[N + 1][K1 + 1];
        double mass[] = new double[N + 1];
        double sigma[][][] = new double[N + 1][N + 1][K1 + 1];
        double Q, T;
        double F[][][] = new double[N + 1][N + 1][K1 + 1];
        double G[][][] = new double[N + 1][N + 1][K1 + 1];
        double H[][][] = new double[N + 1][N + 1][K1 + 1];

        /*Give the time step and time.*/
        double tyme = endTime-startTime;
        double temp;
        //long steps_in_1AU = (long) (1.0 / h);


        /* Determine the number of time steps for this simulation.*/
        int num_time_steps = (int) (Math.ceil(tyme / h) + 1);
        //num_time_steps = pow(2,24);
        //num_time_steps = 500000;

        System.out.println(" h = " + h);
        System.out.println(" num = " + num_time_steps);
        


        /*  Give the masses. */

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
        mass[1] = 1.;
        mass[2] = 1.66013679527193035E-7;
        mass[3] = 2.44783959796682464E-6;
        mass[4] = 3.04043273871083524E-6;
        mass[5] = 3.22714936215392876E-7;
        mass[6] = 9.54790662147324233E-4;
        mass[7] = 2.85877644368210402E-4;
        mass[8] = 4.35540069686411149E-5;
        //  mass[8]     =  1;
        mass[9] = 5.17759138448793649E-5;
        mass[10] = 7.6923076923076926E-9;

        
        for(int ii=1;ii<=10;ii++)
        {
            alpha[ii][0] = coords.get(ii-1).getX();
            beta[ii][0] = coords.get(ii-1).getY();
            Gamma[ii][0] = coords.get(ii-1).getZ();
            delta[ii][0] = coords.get(ii-1).getU();
            rho[ii][0] = coords.get(ii-1).getV();
            lambda[ii][0] = coords.get(ii-1).getW();
        }

        /*  Set up sigma from the initial conditions */
        for (i = 1; i <= N; i++)
        {
            for (j = i + 1; j <= N; j++)
            {

                sigma[i][j][0] = Math.pow(square(alpha[i][0] - alpha[j][0]) + square(beta[i][0] - beta[j][0]) + square(Gamma[i][0] - Gamma[j][0]), (-0.5));

            }
        }

        /* sigma is symmetric */
        for (i = 1; i <= N; i++)
        {
            for (j = i + 1; j <= N; j++)
            {
                sigma[j][i][0] = sigma[i][j][0];
            }
        }

        
        /* Initialize for the output. */
       List<Nbody> results = new ArrayList<>(num_time_steps*10);
       for(int ii=0;ii<N;ii++)
            results.add(coords.get(ii));

        /*  Do the  simulation.  */
        nc = 0;
        
        for (ns = 2; ns <= num_time_steps; ns++)
        {
            for (k = 1; k <= K1; k++)
            {
                for (i = 1; i <= N; i++)
                {
                    alpha[i][k] = delta[i][k - 1] / k;
                    beta[i][k] = rho[i][k - 1] / k;
                    Gamma[i][k] = lambda[i][k - 1] / k;
                    for (j = i + 1; j <= N; j++)
                    {
                        F[i][j][k - 1] = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            F[i][j][k - 1] = F[i][j][k - 1] + sigma[i][j][m] * sigma[i][j][k - 1 - m];
                        }
                    }
                    for (j = 1; j <= i - 1; j++)
                    {
                        F[i][j][k - 1] = F[j][i][k - 1];
                    }
                    for (j = i + 1; j <= N; j++)
                    {
                        G[i][j][k - 1] = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            G[i][j][k - 1] = G[i][j][k - 1] + sigma[i][j][m] * F[i][j][k - 1 - m];
                        }
                    }
                    for (j = 1; j <= i - 1; j++)
                    {
                        G[i][j][k - 1] = G[j][i][k - 1];
                    }
                    delta[i][k] = 0;
                    for (j = 1; j <= i - 1; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (alpha[i][m] - alpha[j][m]) * G[i][j][k - 1 - m];
                        }
                        delta[i][k] = delta[i][k] - mass[j] * Q;
                    }
                    for (j = i + 1; j <= N; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (alpha[i][m] - alpha[j][m]) * G[i][j][k - 1 - m];
                        }
                        delta[i][k] = delta[i][k] - mass[j] * Q;
                    }
                    delta[i][k] = delta[i][k] / k;
                    rho[i][k] = 0;
                    for (j = 1; j <= i - 1; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (beta[i][m] - beta[j][m]) * G[i][j][k - 1 - m];
                        }
                        rho[i][k] = rho[i][k] - mass[j] * Q;
                    }
                    for (j = i + 1; j <= N; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (beta[i][m] - beta[j][m]) * G[i][j][k - 1 - m];
                        }
                        rho[i][k] = rho[i][k] - mass[j] * Q;
                    }
                    rho[i][k] = rho[i][k] / k;
                    lambda[i][k] = 0;
                    for (j = 1; j <= i - 1; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (Gamma[i][m] - Gamma[j][m]) * G[i][j][k - 1 - m];
                        }
                        lambda[i][k] = lambda[i][k] - mass[j] * Q;
                    }
                    for (j = i + 1; j <= N; j++)
                    {
                        Q = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            Q = Q + (Gamma[i][m] - Gamma[j][m]) * G[i][j][k - 1 - m];
                        }
                        lambda[i][k] = lambda[i][k] - mass[j] * Q;
                    }
                    lambda[i][k] = lambda[i][k] / k;
                    for (j = i + 1; j <= N; j++)
                    {
                        H[i][j][k - 1] = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            H[i][j][k - 1] = H[i][j][k - 1]
                                    + (alpha[i][m] - alpha[j][m]) * (delta[i][k - 1 - m] - delta[j][k - 1 - m])
                                    + (beta[i][m] - beta[j][m]) * (rho[i][k - 1 - m] - rho[j][k - 1 - m])
                                    + (Gamma[i][m] - Gamma[j][m]) * (lambda[i][k - 1 - m] - lambda[j][k - 1 - m]);
                        }
                        T = 0;
                        for (m = 0; m <= k - 1; m++)
                        {
                            T = T + H[i][j][m] * G[i][j][k - 1 - m];
                        }
                        sigma[i][j][k] = -T / k;
                    }
                    for (j = 1; j <= i - 1; j++)
                    {
                        sigma[i][j][k] = sigma[j][i][k];
                    }
                }
            }
            for (i = 1; i <= N; i++)
            {
                temp = alpha[i][K1 - 1] + alpha[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + alpha[i][K1 - k - 1];
                }
                alpha[i][0] = temp;

                temp = beta[i][K1 - 1] + beta[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + beta[i][K1 - k - 1];
                }
                beta[i][0] = temp;

                temp = Gamma[i][K1 - 1] + Gamma[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + Gamma[i][K1 - k - 1];
                }
                Gamma[i][0] = temp;

                temp = delta[i][K1 - 1] + delta[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + delta[i][K1 - k - 1];
                }
                delta[i][0] = temp;

                temp = rho[i][K1 - 1] + rho[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + rho[i][K1 - k - 1];
                }
                rho[i][0] = temp;

                temp = lambda[i][K1 - 1] + lambda[i][K1] * h;
                for (k = 1; k <= K1 - 1; k++)
                {
                    temp = temp * h + lambda[i][K1 - k - 1];
                }
                lambda[i][0] = temp;

                for (j = i + 1; j <= N; j++)
                {
                    temp = sigma[i][j][K1 - 1] + sigma[i][j][K1] * h;
                    for (k = 1; k <= K1 - 1; k++)
                    {
                        temp = temp * h + sigma[i][j][K1 - k - 1];
                    }
                    sigma[i][j][0] = temp;

                }
            }
            for (i = 1; i <= N; i++)
            {
                for (j = 1; j <= i - 1; j++)
                {
                    sigma[i][j][0] = sigma[j][i][0];
                }
            }
            nc = nc + 1;
            if (nc == 1000)
            {
                System.out.println(" step = " + ns);
                nc = 0;
            }
            //if (ns % steps_in_1AU == 0)
            //{
                for (int ii = 1; ii <= N; ii++)
                /* i from 1 by 1 to N do */
                {    
                    
                    
                    Nbody next = new Nbody();
                    next.setId((long)ii);
                    next.setPlanetNumber(ii);
                    next.setStep(initialTimestep+ns-1);
                    next.setX(alpha[ii][0]);
                    next.setY(beta[ii][0]);
                    next.setZ(Gamma[ii][0]);
                    next.setU(delta[ii][0]);
                    next.setV(rho[ii][0]);
                    next.setW(lambda[ii][0]);
                    results.add(next);
                }
            //}

        }
        return results;
        
    }
    
}
