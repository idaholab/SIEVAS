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
package gov.inl.SIEVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

/**
 *
 * @author monejh
 */
@Entity
@Table(name = "nbody_info")
@NamedQueries(
{
    @NamedQuery(name = "NbodyInfo.findAll", query = "SELECT p FROM NbodyInfo p"),
    @NamedQuery(name = "NbodyInfo.findById", query = "SELECT p FROM NbodyInfo p WHERE p.id = :id")
})
public class NbodyInfo implements Serializable, IIdentifier<Long>
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "timestep")
    private double timestep;
    
    @OneToMany(mappedBy = "nbodyInfo")
    private Collection<NbodyPlanet> nbodyPlanets;
    

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public double getTimestep()
    {
        return timestep;
    }

    public void setTimestep(double timestep)
    {
        this.timestep = timestep;
    }
    
    

    public Collection<NbodyPlanet> getNbodyPlanets()
    {
        return nbodyPlanets;
    }

    public void setNbodyPlanets(Collection<NbodyPlanet> nbodyPlanets)
    {
        this.nbodyPlanets = nbodyPlanets;
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
        if (!(object instanceof NbodyInfo))
        {
            return false;
        }
        NbodyInfo other = (NbodyInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "NbodyInfo{id=" + id + ", timestep=" + timestep + "}";
    }
    
}
