/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.SIEVAS.common.IIdentifier;
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
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author monejh
 */

@Entity
@Table(name = "datasource_option")
@NamedQueries(
{
    @NamedQuery(name = "DataSourceOption.findAll", query = "SELECT p FROM DataSourceOption p"),
    @NamedQuery(name = "DataSourceOption.findByIdAndName", query = "SELECT p FROM DataSourceOption p WHERE p.id = :id AND p.optionName = :optionName"),
})
public class DataSourceOption implements Serializable, IIdentifier<Long>
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
    @Column(name = "option_name")
    private String optionName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 0, max = 1024)
    @Column(name = "option_value")
    private String optionValue;
    
    //@JsonIgnore
    @ManyToOne( )//(optional = false, targetEntity = Datasource.class)
    @JoinColumn(name = "datasource_id", nullable = false)
    @JsonBackReference()
    private Datasource datasource;

    public DataSourceOption()
    {
    }

    public DataSourceOption(Long id)
    {
        this.id = id;
    }

    public DataSourceOption(Long id, String optionName)
    {
        this.id = id;
        this.optionName = optionName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    public String getOptionValue()
    {
        return optionValue;
    }

    public void setOptionValue(String optionValue)
    {
        this.optionValue = optionValue;
    }

    public Datasource getDatasource()
    {
        return datasource;
    }

    public void setDatasource(Datasource datasource)
    {
        this.datasource = datasource;
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
        if (!(object instanceof DataSourceOption))
        {
            return false;
        }
        DataSourceOption other = (DataSourceOption) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DataSourceOption{id=" + id + ", optionName=" + optionName + ", optionValue=" + optionValue + "}";
    }

    
    
}
