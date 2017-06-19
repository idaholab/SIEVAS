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
package gov.inl.SIEVAS.connector;

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
        return coefficients.length-1;
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
        return new Polynomial(newCoeffs);
    }
    
    public void setCoefficient(int degree, double value)
    {
        coefficients[degree] = value;
    }
    
    public double getCoefficient(int degree)
    {
        return coefficients[degree];
    }
    
    
    public Polynomial scale(double scalar)
    {
        Polynomial result = new Polynomial(this.coefficients);
        for(int ii=0;ii<result.getDegree();ii++)
            result.coefficients[ii] *= scalar;
        return result;
        
    }
    
    public Polynomial add(Polynomial poly2)
    {
        int newDegree = Math.max(this.coefficients.length, poly2.coefficients.length);
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<newDegree;ii++)
        {
            if (this.coefficients.length>newDegree)
                result.coefficients[ii] = this.coefficients[ii];
            else
                result.coefficients[ii] = 0.0;
            
            if (poly2.coefficients.length>newDegree)
                result.coefficients[ii] += poly2.coefficients[ii];
        }
        return result;
    }
    
    public Polynomial subtract(Polynomial poly2)
    {
        int newDegree = Math.max(this.coefficients.length, poly2.coefficients.length);
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<newDegree;ii++)
        {
            if (this.coefficients.length>newDegree)
                result.coefficients[ii] = this.coefficients[ii];
            else
                result.coefficients[ii] = 0.0;
            
            if (poly2.coefficients.length>newDegree)
                result.coefficients[ii] -= poly2.coefficients[ii];
        }
        return result;
    }
    
    
    public Polynomial multiply(Polynomial poly2)
    {
        
        int newDegree = getDegree()+poly2.getDegree();
        Polynomial result = new Polynomial(newDegree);
        for(int ii=0;ii<getDegree();ii++)
        {
            for(int jj=0;jj<poly2.getDegree();jj++)
            {
                result.coefficients[ii+jj] += this.coefficients[ii]*poly2.coefficients[jj];
            }
        }
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
    public String toString()
    {
        if ((coefficients == null) || (coefficients.length == 0)) 
            return "0.0";
        else
        {
            String value = "";
            for(int ii=coefficients.length-1;ii>-1;ii--)
            {
                value += (ii==0) ? coefficients[ii] : " " + coefficients[ii] + "t^" + ii;
            }
            return value;
        }
                    
    }
}
