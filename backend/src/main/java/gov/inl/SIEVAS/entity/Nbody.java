/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.entity;

import gov.inl.SIEVAS.common.IData;
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 *
 * @author monejh
 */
@Entity
@Table(name = "nbody")
@NamedQueries(
{
    @NamedQuery(name = "Nbody.findAll", query = "SELECT p FROM Nbody p"),
    @NamedQuery(name = "Nbody.findById", query = "SELECT p FROM Nbody p WHERE p.id = :id"),
    @NamedQuery(name = "Nbody.findByPlanetNumberTimeStep", query = "SELECT p FROM Nbody p WHERE p.planetNumber = :planetNumber AND p.step = :step")
})
public class Nbody extends IData implements Serializable, IIdentifier<Long>
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "planet_num")
    private int planetNumber;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "step")
    private long step;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "x")
    private double x;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "y")
    private double y;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "z")
    private double z;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "u")
    private double u;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "v")
    private double v;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "w")
    private double w;
    
    
    

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public int getPlanetNumber()
    {
        return planetNumber;
    }

    public void setPlanetNumber(int planetNumber)
    {
        this.planetNumber = planetNumber;
    }

    public long getStep()
    {
        return step;
    }

    public void setStep(long step)
    {
        this.step = step;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public double getU()
    {
        return u;
    }

    public void setU(double u)
    {
        this.u = u;
    }

    public double getV()
    {
        return v;
    }

    public void setV(double v)
    {
        this.v = v;
    }

    public double getW()
    {
        return w;
    }

    public void setW(double w)
    {
        this.w = w;
    }

   
    
    

    
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Nbody))
        {
            return false;
        }
        Nbody other = (Nbody) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Nbody{id=" + id + ", number=" + planetNumber + ", step=" + step + "}";
    }
    
}
