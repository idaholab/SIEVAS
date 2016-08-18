/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.nbody;

/**
 *
 * @author monejh
 */
public class Polynomial
{
    private double coefficients[];
    
    public Polynomial()
    {
        coefficients = new double[0];
    }
    
    public Polynomial(int degree)
    {
        coefficients = new double [degree+1];
        for(int ii=0; ii< coefficients.length;ii++)
            coefficients[ii]=0.0;
    }
    
    public Polynomial(double coefficients[])
    {
        this.coefficients = new double[coefficients.length];
        for(int ii=0;ii<this.coefficients.length;ii++)
            this.coefficients[ii]=coefficients[ii];
    }
    
    
    public int getDegree()
    {
        return (coefficients.length>0) ? coefficients.length-1 : 0;
    }
    
    public double evaluate(double t)
    {
        double value = 0.0;
        double tValue = 1.0;
        if (coefficients.length>0)
            value = coefficients[0];
        for(int ii = 1; ii<coefficients.length;ii++)
        {
            tValue *= t;
            value += coefficients[ii]*tValue;
        }
        return value;
    }
    
    public Polynomial integrate()
    {
        double newCoeffs[] = new double[coefficients.length+1];
        for(int ii=0;ii<coefficients.length;ii++)
        {
            newCoeffs[ii+1] = coefficients[ii]/(ii+1.0);
        }
        newCoeffs[0] = 0;
        Polynomial result = new Polynomial(newCoeffs);
        result.removeNonZeros();
        return result;
    }
    
    public void setCoefficient(int degree, double value)
    {
        if (coefficients.length<(degree+1))
        {
            double[] newArray = new double[degree+1];
            System.arraycopy(coefficients, 0, newArray, 0, coefficients.length);
            coefficients = newArray;
        }
        coefficients[degree] = value;
    }
    
    public double getCoefficient(int degree)
    {
        return coefficients[degree];
    }
    
    
    public Polynomial scale(double scalar)
    {
        Polynomial result = new Polynomial(this.coefficients);
        for(int ii=0;ii<result.coefficients.length;ii++)
            result.coefficients[ii] *= scalar;
        result.removeNonZeros();
        return result;
        
    }
    
    public Polynomial add(Polynomial poly2)
    {
        int newDegree = Math.max(this.coefficients.length, poly2.coefficients.length) - 1;
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<result.coefficients.length;ii++)
        {
            if (ii<this.coefficients.length)
                result.coefficients[ii] = this.coefficients[ii];
            else
                result.coefficients[ii] = 0.0;
            
            if (ii<poly2.coefficients.length)
                result.coefficients[ii] += poly2.coefficients[ii];
        }
        result.removeNonZeros();
        return result;
    }
    
    public Polynomial subtract(Polynomial poly2)
    {
        int newDegree = Math.max(this.coefficients.length, poly2.coefficients.length) - 1;
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<result.coefficients.length;ii++)
        {
            if (ii<this.coefficients.length)
                result.coefficients[ii] = this.coefficients[ii];
            else
                result.coefficients[ii] = 0.0;
            
            if (ii<poly2.coefficients.length)
                result.coefficients[ii] -= poly2.coefficients[ii];
        }
        result.removeNonZeros();
        return result;
    }
    
    
    public Polynomial multiply(Polynomial poly2)
    {
        
        int newDegree = this.coefficients.length + poly2.coefficients.length;
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<this.coefficients.length;ii++)
        {
            for(int jj=0;jj<poly2.coefficients.length;jj++)
            {
                result.coefficients[ii+jj] += this.coefficients[ii]*poly2.coefficients[jj];
            }
        }
        result.removeNonZeros();
        return result;
    }
    
    public Polynomial power(int degree)
    {
        Polynomial result = new Polynomial(new double[]{1.0});
        for(int ii=0;ii<degree;ii++)
        {
            result = result.multiply(this);
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj2)
    {
        if (!(obj2 instanceof Polynomial))
            return false;
        else
        {
            this.removeNonZeros();
            Polynomial pol2 = (Polynomial)obj2;
            pol2.removeNonZeros();
            if (pol2.coefficients.length!=coefficients.length)
                return false;
            else
                for(int ii=0;ii<coefficients.length;ii++)
                    if (Math.abs(coefficients[ii] - pol2.coefficients[ii]) > (Math.ulp(1.0)*100))
                        return false;
            return true;
        }
    }
    
    private void removeNonZeros()
    {
        int lastNonZero = coefficients.length-1;
        for(int ii=coefficients.length-1;ii>-1;ii--)
        {
            if (coefficients[ii] == 0)
                lastNonZero = ii-1;
            else 
                break;
        }
        if ((lastNonZero>-1) && (lastNonZero!=(coefficients.length-1)))
        {
            double[] newArray = new double[lastNonZero+1];
            System.arraycopy(coefficients, 0, newArray, 0, lastNonZero+1);
            this.coefficients = newArray;
        }
        else if (lastNonZero == -1)
        {
            this.coefficients = new double[0];
        }
    }
    @Override
    public String toString()
    {
        if ((coefficients == null) || (coefficients.length == 0)) 
            return "0.0";
        else
        {
            String value = "";
            for(int ii=coefficients.length-1;ii>-1;ii--)
            {
                value += (ii==0) ? " " + coefficients[ii] : " " + coefficients[ii] + "t^" + ii + " +";
            }
            return value;
        }
                    
    }
}
