/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.LIVE.common.IIdentifier;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

/**
 *
 * @author monejh
 */
@Entity
@Table(name = "nbody_planet")
@NamedQueries(
{
    @NamedQuery(name = "NbodyPlanet.findAll", query = "SELECT p FROM NbodyPlanet p"),
    @NamedQuery(name = "NbodyPlanet.findById", query = "SELECT p FROM NbodyPlanet p WHERE p.id = :id"),
    @NamedQuery(name = "NbodyPlanet.findByPlanetNumber", query = "SELECT p FROM NbodyPlanet p WHERE p.planetNumber = :planetNumber")
})
public class NbodyPlanet implements Serializable, IIdentifier<Long>
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
    @Size(min = 1, max = 128)
    @Column(name = "planet_name")
    private String planetName;
    
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "planet_texture")
    private byte planetTexture[];
    
    @JsonIgnore
    @ManyToOne(optional = false, targetEntity = NbodyInfo.class)
    @JoinColumn(name = "nbody_info_id")
    private NbodyInfo nbodyInfo;
    

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

    public String getPlanetName()
    {
        return planetName;
    }

    public void setPlanetName(String planetName)
    {
        this.planetName = planetName;
    }

    public byte[] getPlanetTexture()
    {
        return planetTexture;
    }

    public void setPlanetTexture(byte[] planetTexture)
    {
        this.planetTexture = planetTexture;
    }

    public NbodyInfo getNbodyInfo()
    {
        return nbodyInfo;
    }

    public void setNbodyInfo(NbodyInfo nbodyInfo)
    {
        this.nbodyInfo = nbodyInfo;
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
        if (!(object instanceof NbodyPlanet))
        {
            return false;
        }
        NbodyPlanet other = (NbodyPlanet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "NbodyPlanet{id=" + id + ", name=" + planetName + ", number=" + planetNumber + "}";
    }
    
}
