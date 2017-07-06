/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
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
import org.springframework.security.core.GrantedAuthority;

/**
 * Datasource JPA
 * @author monejh
 */
@Entity
@Table(name = "datasource")
@NamedQueries(
{
    @NamedQuery(name = "Datasource.findAll", query = "SELECT p FROM Datasource p"),
    @NamedQuery(name = "Datasource.findById", query = "SELECT p FROM Datasource p WHERE p.id = :id"),
    @NamedQuery(name = "Datasource.findByDatasourceName", query = "SELECT p FROM Datasource p WHERE p.name = :name"),
})
public class Datasource implements Serializable, IIdentifier<Long>
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "driver_name")
    private String driverName;
    
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "datasource", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Collection<DataSourceOption> options;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "datasources")
    private Collection<SIEVASSession> sessionCollection;
    

    public Datasource()
    {
    
    }

    
    public Datasource(Long id, String name, String driverName, String description)
    {
        this.id = id;
        this.name = name;
        this.driverName = driverName;
        this.description = description;
    }
    
    public Datasource(Long id, String name, DriverInfo info, String description)
    {
        this.id = id;
        this.name = name;
        this.driverName = info.getFullName();
        this.description = description;
    }
    
    

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Collection<DataSourceOption> getOptions()
    {
        return options;
    }

    public void setOptions(Collection<DataSourceOption> options)
    {
        this.options = options;
    }

    
    
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Datasource other = (Datasource) obj;
        if (!Objects.equals(this.id, other.id))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Datasource{" + "id=" + id + ", name=" + name + ", driverName=" + driverName + ", description=" + description + ", options=" + options + '}';
    }

    
    
    
}
