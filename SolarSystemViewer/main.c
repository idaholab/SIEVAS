#include<stdio.h>
#include<stdlib.h>
#include<math.h>

/* This program is a Picard-Maclaurin solver for Newton's N Body Problem. 
The Maclaurin polynomial are determined from Picard iteration using Cauchy 
products. The algorithm is at www.math.jmu.edu/~jim/picard.htm.  You are 
asked to give the number of bodies, N,the mass of each body, the initial 
position (alpha[i,0],beta[i,0],Gamma[i,0]) and the initial velocity 
(delta[i,0],rho[i,0],lambda[i,0]) of each body, the time step,  the time to 
run the simulation and the degree of the Maclaurin polynomial for the 
numerical solution.


   Give the number of bodies (masses). */

#define N 10

/* Give the degree of the Maclaurin polynomials.*/

#define K1 8

int main()
{

	int i,j,k,m,ns,nc;
	double alpha[N+1][K1+1], beta[N+1][K1+1], Gamma[N+1][K1+1];
	double delta[N+1][K1+1], rho[N+1][K1+1], lambda[N+1][K1+1];
	double mass[N+1], sigma[N+1][N+1][K1+1];
	double Q,T;
	double F[N+1][N+1][K1+1], G[N+1][N+1][K1+1];
	double H[N+1][N+1][K1+1];
	FILE *fpx[N+1];//,*fpy[N+1],*fpz[N+1];
        char filename[10];

   	/*Give the time step and time.*/

  	double h = pow(0.5,14); //can change to 14 or 16 to change time step
  	double tyme = 25600;
	double temp = 0;


  	/* Determine the number of time steps for this simulation.*/


  	int num_time_steps = ceil(tyme/h)+1;
	//num_time_steps = pow(2,24);
	//num_time_steps = 500000;

    printf(" h = %f \n",h);
    printf(" num = %d \n",num_time_steps);


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


  mass[1]       =  1.;
  mass[2]    =  1.66013679527193035E-7;
  mass[3]      =  2.44783959796682464E-6;
  mass[4]     =  3.04043273871083524E-6;
  mass[5]      =  3.22714936215392876E-7;
  mass[6]    =  9.54790662147324233E-4;
  mass[7]     =  2.85877644368210402E-4;
  mass[8]     =  4.35540069686411149E-5;
//  mass[8]     =  1;
  mass[9]    =  5.17759138448793649E-5;
  mass[10]      =  7.6923076923076926E-9;


	/*    Set up the initial conditions  */

/* SUN */

     alpha[1][0] = 0;
  	beta[1][0] = 0;
  	Gamma[1][0] = 0;
  	delta[1][0] = 0;
  	rho[1][0] = 0;
  	lambda[1][0] = 0;

/* MERCURY */

  	alpha[2][0] =  0.1633519000000E-02;
  	beta[2][0] =  0.3077199200000;
  	Gamma[2][0] =  0.2498653800000E-01;
  	delta[2][0] = -1.963534107001;
  	rho[2][0] =  0.6841909835729E-01;
  	lambda[2][0] = 0.1858224458145;

/* VENUS */

	alpha[3][0] =  0.2640622900000;
  	beta[3][0] = 0.6709840300000;
  	Gamma[3][0] =  -.6078524800000E-02;
  	delta[3][0] =  -1.097989091627;
  	rho[3][0] =  0.4250727728824;
  	lambda[3][0] = 0.6918653377540E-01;
	
/* EARTH */

	alpha[4][0] =  0.6643154100000E-01; 
  	beta[4][0] =    0.9817401900000;
  	Gamma[4][0] =  0.6625301000000E-05;
  	delta[4][0] =  -1.014141067952;
  	rho[4][0] =  0.6377084582524E-01;
  	lambda[4][0] =  -.1047343992878E-05;

/* MARS */ 

	alpha[5][0] =     1.105892500000;
  	beta[5][0] =  -.8315317200000 ;
  	Gamma[5][0] = -.4460530000000E-01 ;
  	delta[5][0] = 0.5199212681014;
  	rho[5][0] =  0.7198022773915;
  	lambda[5][0] =  0.2297489167762E-02;

/* JUPITER */

 	alpha[6][0] =  4.286062400000; 
  	beta[6][0] =  -2.621093500000;
  	Gamma[6][0] =  -.8513666500000E-01;
  	delta[6][0] =  0.2231109535641;
  	rho[6][0] =  0.3949656910948;
  	lambda[6][0] =  -.6631270424191E-02;

/* SATURN */

	alpha[7][0] =  8.834561200000;
  	beta[7][0] =  3.097512000000;
  	Gamma[7][0] = -.4051782400000;
  	delta[7][0] = -.1244215317121;
  	rho[7][0] =  0.3058012696789;
  	lambda[7][0] = -.3744219597147E-03;

/* URANUS */

	alpha[8][0] =  12.29164300000;
  	beta[8][0] = -15.57520200000;
  	Gamma[8][0] = -.2172011500000;
  	delta[8][0] =  0.1780033999880;
  	rho[8][0] =  0.1314573649760;
  	lambda[8][0] = -.1824027526613E-02;


/* NEPTUNE */

	alpha[9][0] =  4.84009700000;
  	beta[9][0] = -26.23912700000;
  	Gamma[9][0] = 0.1982557900000;
  	delta[9][0] =  0.1578550564045;
  	rho[9][0] =  0.9132161165808E-01;
  	lambda[9][0] =  -.5510764371051E-02;

/* PLUTO */

	alpha[10][0] =  -12.10226300000;
  	beta[10][0] = -26.73256000000;
  	Gamma[10][0] = 6.362842900000;
  	delta[10][0] =  0.1714028159354;
  	rho[10][0] =  -.1021868903979;
  	lambda[10][0] = -.3854629379438E-01;

/*  Set up sigma from the initial conditions */

  	for ( i = 1; i <=N; i++ )
	{
        for ( j = i+1; j <= N; j++ )
	{

          sigma[i][j][0]= 
pow((pow((alpha[i][0]-alpha[j][0]),2)+pow((beta[i][0]-beta[j][0]),2)+pow((Gamma[i][0]-Gamma[j][0]),2)),(-1/2.));

	}
	}

/* sigma is symmetric */

    for ( i = 1; i <= N; i++ )
    {
    for ( j = i+1; j <= N; j++ )
    {
      sigma[j][i][0] = sigma[i][j][0];
    }
    }


   	/* Initialize for the output. */


  	for(i=1; i<=N; i++)
  	{
    		sprintf(filename,"coord%d",i);
        	if ((fpx[i] = fopen(filename, "w")) == NULL)
        	{
            		printf("%s not opened\n", filename);
            		exit(1);
        	}
        	fprintf(fpx[i],"%3.16le %3.16le %3.16le\n", alpha[i][0], beta[i][0], Gamma[i][0]);

/*		sprintf(filename,"y%d",i);
        	if ((fpy[i] = fopen(filename, "w")) == NULL)
         	{
            		printf("%s not opened\n", filename);
            		exit(1);
         	}
         	fprintf(fpy[i],"%lf\n", beta[i][0]);

		sprintf(filename,"z%d",i);
         	if ((fpz[i] = fopen(filename, "w")) == NULL)
         	{
            		printf("%s not opened\n", filename);
            		exit(1);
         	}
         	fprintf(fpz[i],"%lf\n", Gamma[i][0]);*/
  	}



   	/*  Do the  simulation.  */

  nc = 0;

  for ( ns = 2; ns <= num_time_steps; ns++ )
  {
    for ( k = 1; k <= K1; k++ )
    {
      for ( i = 1; i <= N; i++ )
      {
        alpha[i][k] = delta[i][k-1]/k;
        beta[i][k] = rho[i][k-1]/k;
        Gamma[i][k] = lambda[i][k-1]/k;
        for ( j = i+1; j <= N; j++ )
        {
          F[i][j][k-1] = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            F[i][j][k-1] = F[i][j][k-1] + sigma[i][j][m]*sigma[i][j][k-1-m];
          }
        }
        for ( j = 1; j <= i-1; j++ )
        {
          F[i][j][k-1] = F[j][i][k-1];
        }
        for ( j = i+1; j <= N; j++ )
        {
          G[i][j][k-1] = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            G[i][j][k-1] = G[i][j][k-1] + sigma[i][j][m]*F[i][j][k-1-m];
          }
        }
        for ( j = 1; j <= i-1; j++ )
        {
          G[i][j][k-1] = G[j][i][k-1];
        }
        delta[i][k] = 0;
        for ( j = 1; j <= i-1; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (alpha[i][m]-alpha[j][m])*G[i][j][k-1-m];
          }
          delta[i][k] = delta[i][k] - mass[j]*Q;
        }
        for ( j = i+1; j <= N; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (alpha[i][m]-alpha[j][m])*G[i][j][k-1-m];
          }
          delta[i][k] = delta[i][k] - mass[j]*Q;
        }
        delta[i][k] = delta[i][k]/k;
        rho[i][k] = 0;
        for ( j = 1; j <= i-1; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (beta[i][m]-beta[j][m])*G[i][j][k-1-m];
          }
          rho[i][k] = rho[i][k] - mass[j]*Q;
        }
        for ( j = i+1; j <= N; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (beta[i][m]-beta[j][m])*G[i][j][k-1-m];
          }
          rho[i][k] = rho[i][k] - mass[j]*Q;
        }
        rho[i][k] = rho[i][k]/k;
        lambda[i][k] = 0;
        for ( j = 1; j <= i-1; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (Gamma[i][m]-Gamma[j][m])*G[i][j][k-1-m];
          }
          lambda[i][k] = lambda[i][k] - mass[j]*Q;
        }
        for ( j = i+1; j <= N; j++ )
        {
          Q = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            Q = Q + (Gamma[i][m]-Gamma[j][m])*G[i][j][k-1-m];
          }
          lambda[i][k] = lambda[i][k] - mass[j]*Q;
        }
        lambda[i][k] = lambda[i][k]/k;
        for ( j = i+1; j <= N; j++ )
        {
          H[i][j][k-1] = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            H[i][j][k-1] = H[i][j][k-1] + 
(alpha[i][m]-alpha[j][m])*(delta[i][k-1-m]-delta[j][k-1-m]) + 
(beta[i][m]-beta[j][m])*(rho[i][k-1-m]-rho[j][k-1-m]) + 
(Gamma[i][m]-Gamma[j][m])*(lambda[i][k-1-m]-lambda[j][k-1-m]);
          }
          T = 0;
          for ( m = 0; m <= k-1; m++ )
          {
            T = T + H[i][j][m]*G[i][j][k-1-m];
          }
          sigma[i][j][k] = -T/k;
        }
        for ( j = 1; j <= i-1; j++ )
        {
          sigma[i][j][k] = sigma[j][i][k];
        }
      }
    }
    for ( i = 1; i <= N; i++ )
	{
      temp = alpha[i][K1-1]+alpha[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+alpha[i][K1-k-1];
      }
      alpha[i][0] = temp;

      temp = beta[i][K1-1]+beta[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+beta[i][K1-k-1];
      }
      beta[i][0] = temp;

      temp = Gamma[i][K1-1]+Gamma[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+Gamma[i][K1-k-1];
      }
      Gamma[i][0] = temp;

      temp = delta[i][K1-1]+delta[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+delta[i][K1-k-1];
      }
      delta[i][0] = temp;

      temp = rho[i][K1-1]+rho[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+rho[i][K1-k-1];
      }
      rho[i][0] = temp;

      temp = lambda[i][K1-1]+lambda[i][K1]*h;
      for ( k = 1; k <= K1-1; k++ )
      {
        temp = temp*h+lambda[i][K1-k-1];
      }
      lambda[i][0] = temp;

      for ( j = i+1; j <= N; j++ )
      {
        temp = sigma[i][j][K1-1]+sigma[i][j][K1]*h;
        for ( k = 1; k <= K1-1; k++ )
        {
          temp = temp*h+sigma[i][j][K1-k-1];
        }
        sigma[i][j][0] = temp;

      }
    }
    for ( i = 1; i <= N; i++ )
    {
    for ( j = 1; j <= i-1; j++ )
    {
      sigma[i][j][0] = sigma[j][i][0];
    }
    }
	nc = nc+1;
	if (nc==1000)
	{
	  printf(" step = %d \n",ns);
	  nc = 0;
    }
	  for(i=1; i<=N; i++)	/* i from 1 by 1 to N do */
	  {

          fprintf(fpx[i],"%3.16le %3.16le %3.16le\n",alpha[i][0], beta[i][0], Gamma[i][0]);

/*       	  fprintf(fpx[i],"%lf\n",beta[i][0]);
       	  fprintf(fpx[i],"%lf\n",Gamma[i][0]);*/

	  }

  }


  	for(i=1; i<=N; i++)	/* i from 1 by 1 to N do */
	{
      	fclose(fpx[i]);
/*        fclose(fpy[i]);
        fclose(fpz[i]);*/
    }

    return(0);

}

